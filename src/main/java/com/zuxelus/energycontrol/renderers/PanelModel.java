package com.zuxelus.energycontrol.renderers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.joml.Vector3f;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.PanelRenderData;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.blocks.FacingBlockActive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

/**
 * The panel body is baked into the chunk mesh; the block entity renderers only draw the text.
 * Used from block model files:
 * {@code { "loader": "energycontrol:panel", "default_color": ..., "textures": { "body": ..., "screen": ..., "particle": ... } }}.
 * The block entity hands its screen texture, color, power and shape over as {@link PanelRenderData} model data.
 */
public class PanelModel implements IUnbakedGeometry<PanelModel> {
	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "panel");
	private final int defaultColor;

	public PanelModel(int defaultColor) {
		this.defaultColor = defaultColor;
	}

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		return new Baked(spriteGetter.apply(context.getMaterial("body")), spriteGetter.apply(context.getMaterial("screen")),
				spriteGetter.apply(context.getMaterial("particle")), defaultColor);
	}

	public static class Loader implements IGeometryLoader<PanelModel> {
		@Override
		public PanelModel read(JsonObject json, JsonDeserializationContext context) {
			return new PanelModel(GsonHelper.getAsInt(json, "default_color"));
		}
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

	private static class Baked implements IDynamicBakedModel {
		private static final float EPSILON = 1.0E-4F;
		private static final RotationOffset FULL = new RotationOffset();

		private final TextureAtlasSprite body;
		private final TextureAtlasSprite screen;
		private final TextureAtlasSprite particle;
		private final int defaultColor;

		Baked(TextureAtlasSprite body, TextureAtlasSprite screen, TextureAtlasSprite particle, int defaultColor) {
			this.body = body;
			this.screen = screen;
			this.particle = particle;
			this.defaultColor = defaultColor;
		}

		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
			if (state == null)
				return Collections.emptyList();
			PanelRenderData data = extraData.get(PanelRenderData.PROPERTY);
			if (data == null)
				data = new PanelRenderData(15, defaultColor, state.getValue(FacingBlockActive.ACTIVE), null, null);

			// same geometry and rotation as the block entity renderer uses, so the screen borders match findTexture()
			PoseStack matrixStack = new PoseStack();
			PanelCube.rotateBlock(matrixStack, state.getValue(FacingBlock.FACING), data.rotation());
			List<BakedQuad> quads = new ArrayList<>();
			PanelCube model = data.offset() == null ? PanelCube.MODEL : PanelCube.getModel(data.offset());
			// the body's face quad lies under the screen
			model.visitQuads(matrixStack, (index, positions, u, v, normal) -> {
				if (index != PanelCube.FACE)
					addQuad(quads, side, positions, u, v, normal, body, 0xFFFFFFFF, false);
			});
			int faceColor = getFaceColor(data.color(), data.powered());
			boolean glowing = data.powered();
			PanelCube.getFaceModel(data.offset() == null ? FULL : data.offset(), data.textureId()).visitQuads(matrixStack,
					(index, positions, u, v, normal) -> addQuad(quads, side, positions, u, v, normal, screen, faceColor, glowing));
			return quads;
		}

		// keeps only the quads asked for: those culled by side, or the unculled ones when side is null.
		// A powered screen glows like a display: full brightness, no ambient occlusion
		private static void addQuad(List<BakedQuad> quads, Direction side, Vector3f[] positions, float[] u, float[] v, Vector3f normal, TextureAtlasSprite sprite, int color, boolean glowing) {
			Direction direction = Direction.getNearest(normal.x(), normal.y(), normal.z());
			if (getCullFace(positions, direction) != side)
				return;
			QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer();
			builder.setDirection(direction);
			builder.setSprite(sprite);
			builder.setShade(true);
			builder.setHasAmbientOcclusion(!glowing);
			int light = glowing ? 0xF0 : 0;
			for (int i = 0; i < 4; i++)
				builder.addVertex(positions[i].x(), positions[i].y(), positions[i].z())
						.setColor(color >> 16 & 255, color >> 8 & 255, color & 255, 255)
						.setUv(sprite.getU(u[i]), sprite.getV(v[i]))
						.setUv2(light, light)
						.setNormal(normal.x(), normal.y(), normal.z());
			quads.add(builder.bakeQuad());
		}

		// a quad lying on the block boundary is hidden by a full neighbour on that side
		private static Direction getCullFace(Vector3f[] positions, Direction direction) {
			float bound = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0F : 0.0F;
			for (Vector3f p : positions) {
				double coord = direction.getAxis().choose(p.x(), p.y(), p.z());
				if (Math.abs(coord - bound) > EPSILON)
					return null;
			}
			return direction;
		}

		@Override
		public boolean useAmbientOcclusion() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean usesBlockLight() {
			return true;
		}

		@Override
		public boolean isCustomRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return particle;
		}

		@Override
		public ItemOverrides getOverrides() {
			return ItemOverrides.EMPTY;
		}
	}
}
