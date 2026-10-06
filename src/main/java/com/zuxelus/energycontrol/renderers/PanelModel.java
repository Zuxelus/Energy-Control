package com.zuxelus.energycontrol.renderers;

import java.util.List;
import java.util.function.Predicate;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;

import com.zuxelus.energycontrol.tileentities.PanelRenderData;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.blocks.FacingBlockActive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

// since 1.21.5 block models are BlockStateModels; the panel is emitted through FabricBlockStateModel.emitQuads
@Environment(EnvType.CLIENT)
public class PanelModel implements BlockStateModel.UnbakedRoot {
	private static final RotationOffset FULL = new RotationOffset();

	private final Material body;
	private final Material screen;
	private final Material particle;
	private final int defaultColor;

	public PanelModel(Identifier body, Identifier screen, Identifier particle, int defaultColor) {
		this.body = new Material(body);
		this.screen = new Material(screen);
		this.particle = new Material(particle);
		this.defaultColor = defaultColor;
	}

	@Override
	public void resolveDependencies(Resolver resolver) { }

	@Override
	public BlockStateModel bake(BlockState state, ModelBaker baker) {
		return new Baked(baker.materials().get(body, this::debugName).sprite(), baker.materials().get(screen, this::debugName).sprite(),
				baker.materials().get(particle, this::debugName), defaultColor);
	}

	// all states share one baked model, the facing is applied while emitting
	@Override
	public Object visualEqualityGroup(BlockState state) {
		return this;
	}

	private String debugName() {
		return "energycontrol:panel/" + body.sprite();
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

	private static class Baked implements BlockStateModel {
		private static final float EPSILON = 1.0E-4F;

		private final TextureAtlasSprite body;
		private final TextureAtlasSprite screen;
		private final Material.Baked particle;
		private final int defaultColor;

		Baked(TextureAtlasSprite body, TextureAtlasSprite screen, Material.Baked particle, int defaultColor) {
			this.body = body;
			this.screen = screen;
			this.particle = particle;
			this.defaultColor = defaultColor;
		}

		@Override
		public void emitQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random, Predicate<Direction> cullTest) {
			PanelRenderData data = blockView.getBlockEntityRenderData(pos) instanceof PanelRenderData d ? d : null;
			if (data == null)
				data = new PanelRenderData(15, defaultColor, state.getValue(FacingBlockActive.ACTIVE), null, null);

			// same geometry and rotation as the 1.20.1 block entity renderer, so the screen borders match findTexture()
			PoseStack matrixStack = new PoseStack();
			PanelCube.rotateBlock(matrixStack, state.getValue(FacingBlock.FACING), data.rotation());
			PanelCube model = data.offset() == null ? PanelCube.MODEL : PanelCube.getModel(data.offset());
			// the body's face quad lies under the screen
			model.visitQuads(matrixStack, (index, positions, u, v, normal) -> {
				if (index != PanelCube.FACE)
					emitQuad(emitter, cullTest, positions, u, v, normal, body, -1, false);
			});
			int faceColor = getFaceColor(data.color(), data.powered());
			boolean glowing = data.powered();
			PanelCube.getFaceModel(data.offset() == null ? FULL : data.offset(), data.textureId()).visitQuads(matrixStack,
					(index, positions, u, v, normal) -> emitQuad(emitter, cullTest, positions, u, v, normal, screen, faceColor, glowing));
		}

		// a powered screen glows like a display: full brightness, no ambient occlusion
		private static void emitQuad(QuadEmitter emitter, Predicate<Direction> cullTest, Vector3f[] positions, float[] u, float[] v, Vector3f normal, TextureAtlasSprite sprite, int color, boolean glowing) {
			Direction side = Direction.getApproximateNearest(normal.x(), normal.y(), normal.z());
			float bound = side.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0F : 0.0F;
			boolean flush = true;
			for (int i = 0; i < 4; i++) {
				Vector3f p = positions[i];
				emitter.pos(i, p.x(), p.y(), p.z());
				emitter.uv(i, sprite.getU(u[i]), sprite.getV(v[i]));
				emitter.color(i, color);
				double coord = side.getAxis().choose(p.x(), p.y(), p.z());
				if (Math.abs(coord - bound) > EPSILON)
					flush = false;
			}
			if (flush && cullTest.test(side))
				return;
			emitter.emissive(glowing);
			emitter.ambientOcclusion(glowing ? TriState.FALSE : TriState.DEFAULT);
			emitter.nominalFace(side);
			emitter.cullFace(flush ? side : null);
			emitter.emit();
		}

		// everything is emitted by emitQuads
		@Override
		public void collectParts(RandomSource random, List<BlockStateModelPart> output) { }

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
