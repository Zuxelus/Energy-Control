package com.zuxelus.energycontrol.renderers;

import java.util.List;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.PanelRenderData;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.blocks.FacingBlockActive;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.quad.MutableQuad;

/**
 * The panel body is baked into the chunk mesh; the block entity renderers only draw the text.
 * Used from the blockstate files: {@code { "type": "energycontrol:panel", "body": ..., "screen": ..., "particle": ..., "default_color": ... }}.
 * The block entity hands its screen texture, color, power and shape over as {@link PanelRenderData} model data.
 */
public record PanelModel(Identifier body, Identifier screen, Identifier particle, int defaultColor) implements CustomUnbakedBlockStateModel {
	public static final Identifier ID = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "panel");
	public static final MapCodec<PanelModel> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Identifier.CODEC.fieldOf("body").forGetter(PanelModel::body),
			Identifier.CODEC.fieldOf("screen").forGetter(PanelModel::screen),
			Identifier.CODEC.fieldOf("particle").forGetter(PanelModel::particle),
			Codec.INT.fieldOf("default_color").forGetter(PanelModel::defaultColor)
		).apply(instance, PanelModel::new));
	private static final RotationOffset FULL = new RotationOffset();

	@Override
	public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
		return CODEC;
	}

	@Override
	public void resolveDependencies(Resolver resolver) { }

	@Override
	public BlockStateModel bake(ModelBaker baker) {
		return new Baked(material(baker, body), material(baker, screen), material(baker, particle), defaultColor);
	}

	private Material.Baked material(ModelBaker baker, Identifier sprite) {
		return baker.materials().get(new Material(sprite), () -> ID + " " + body);
	}

	// an unpowered screen is drawn at 60% brightness
	public static int getFaceColor(int color, boolean isPowered) {
		if (isPowered)
			return 0xFF000000 | color;
		int r = (color >> 16 & 255) * 3 / 5;
		int g = (color >> 8 & 255) * 3 / 5;
		int b = (color & 255) * 3 / 5;
		return 0xFF000000 | r << 16 | g << 8 | b;
	}

	private static class Baked implements DynamicBlockStateModel {
		private static final float EPSILON = 1.0E-4F;

		private final Material.Baked body;
		private final Material.Baked screen;
		private final Material.Baked particle;
		private final int defaultColor;

		Baked(Material.Baked body, Material.Baked screen, Material.Baked particle, int defaultColor) {
			this.body = body;
			this.screen = screen;
			this.particle = particle;
			this.defaultColor = defaultColor;
		}

		@Override
		public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
			PanelRenderData data = level.getModelData(pos).get(PanelRenderData.PROPERTY);
			if (data == null)
				data = new PanelRenderData(15, defaultColor, state.getValue(FacingBlockActive.ACTIVE), null, null);

			// same geometry and rotation as the block entity renderer used, so the screen borders match findTexture()
			PoseStack matrixStack = new PoseStack();
			PanelCube.rotateBlock(matrixStack, state.getValue(FacingBlock.FACING), data.rotation());
			QuadCollection.Builder quads = new QuadCollection.Builder();
			PanelCube model = data.offset() == null ? PanelCube.MODEL : PanelCube.getModel(data.offset());
			// the body's face quad lies under the screen
			model.visitQuads(matrixStack, (index, positions, u, v, normal) -> {
				if (index != PanelCube.FACE)
					addQuad(quads, positions, u, v, normal, body, -1, false);
			});
			int faceColor = getFaceColor(data.color(), data.powered());
			boolean glowing = data.powered();
			PanelCube.getFaceModel(data.offset() == null ? FULL : data.offset(), data.textureId()).visitQuads(matrixStack,
					(index, positions, u, v, normal) -> addQuad(quads, positions, u, v, normal, screen, faceColor, glowing));
			parts.add(new SimpleModelWrapper(quads.build(), true, particle));
		}

		// a powered screen glows like a display: full brightness, no ambient occlusion
		private static void addQuad(QuadCollection.Builder quads, Vector3f[] positions, float[] u, float[] v, Vector3f normal, Material.Baked material, int color, boolean glowing) {
			TextureAtlasSprite sprite = material.sprite();
			Direction side = Direction.getApproximateNearest(normal.x(), normal.y(), normal.z());
			MutableQuad mutable = new MutableQuad().setSprite(material);
			float bound = side.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0F : 0.0F;
			boolean flush = true;
			for (int i = 0; i < 4; i++) {
				Vector3f p = positions[i];
				mutable.setPosition(i, p.x(), p.y(), p.z());
				mutable.setUv(i, sprite.getU(u[i]), sprite.getV(v[i]));
				mutable.setColor(i, color);
				double coord = side.getAxis().choose(p.x(), p.y(), p.z());
				if (Math.abs(coord - bound) > EPSILON)
					flush = false;
			}
			mutable.setDirection(side);
			mutable.recomputeNormals(false);
			mutable.setLightEmission(glowing ? 15 : 0);
			mutable.setAmbientOcclusion(!glowing);
			// only faces lying on the block boundary may be culled by a neighbour
			if (flush)
				quads.addCulledFace(side, mutable.toBakedQuad());
			else
				quads.addUnculledFace(mutable.toBakedQuad());
		}

		@Override
		public Material.Baked particleMaterial() {
			return particle;
		}

		// no tinted or translucent quads
		@Override
		public int materialFlags() {
			return 0;
		}
	}
}
