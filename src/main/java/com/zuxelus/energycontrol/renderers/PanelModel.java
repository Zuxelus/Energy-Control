package com.zuxelus.energycontrol.renderers;

import java.util.List;

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
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
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
	private static final float TEX_SIZE = 128.0F;
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
		private static final Direction[] SIDES = { Direction.EAST, Direction.WEST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH };
		private static final int FACE = 3;
		private static final Direction[] BODY_SIDES = { Direction.EAST, Direction.WEST, Direction.DOWN, Direction.UP, Direction.SOUTH };
		private static final float[][] BODY_UV = buildBodyUv();

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
				data = new PanelRenderData(15, defaultColor, state.getValue(FacingBlockActive.ACTIVE), null);

			Direction facing = state.getValue(FacingBlock.FACING);
			float[][][] box = buildBox(data.offset() == null ? FULL : data.offset());
			int faceColor = getFaceColor(data.color(), data.powered());
			QuadCollection.Builder quads = new QuadCollection.Builder();
			for (int n = 0; n < box.length; n++) {
				if (n == FACE)
					addQuad(quads, box[n], screenUv(data.textureId()), screen, facing, rotate(SIDES[n], facing), faceColor, data.powered());
				else
					addQuad(quads, box[n], bodyUv(box[n], SIDES[n]), body, facing, rotate(SIDES[n], facing), -1, false);
			}
			parts.add(new SimpleModelWrapper(quads.build(), true, particle));
		}

		// a powered screen glows like a display: full brightness, no ambient occlusion
		private static void addQuad(QuadCollection.Builder quads, float[][] quad, float[][] uv, Material.Baked material, Direction facing, Direction side, int color, boolean glowing) {
			TextureAtlasSprite sprite = material.sprite();
			MutableQuad mutable = new MutableQuad().setSprite(material);
			boolean flush = true;
			for (int i = 0; i < 4; i++) {
				float[] p = transform(quad[i][0], quad[i][1], quad[i][2], facing);
				mutable.setPosition(i, p[0], p[1], p[2]);
				mutable.setUv(i, sprite.getU(uv[i][0]), sprite.getV(uv[i][1]));
				mutable.setColor(i, color);
				float coord = p[side.getAxis().ordinal()];
				if (coord != (side.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0F : 0.0F))
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

		private static float[][][] buildBox(RotationOffset offset) {
			float[] v1 = { 0, 0, 0 };
			float[] v2 = { 1, 0, 0 };
			float[] v3 = { 1, 1 - offset.leftTop / 32, 0 };
			float[] v4 = { 0, 1 - offset.leftBottom / 32, 0 };
			float[] v5 = { 0, 0, 1 };
			float[] v6 = { 1, 0, 1 };
			float[] v7 = { 1, 1 - offset.rightTop / 32, 1 };
			float[] v8 = { 0, 1 - offset.rightBottom / 32, 1 };
			float[][][] quads = new float[6][][];
			quads[0] = new float[][] { v6, v2, v3, v7 };
			quads[1] = new float[][] { v1, v5, v8, v4 };
			quads[2] = new float[][] { v6, v5, v1, v2 };
			quads[3] = new float[][] { v3, v4, v8, v7 };
			quads[4] = new float[][] { v2, v1, v4, v3 };
			quads[5] = new float[][] { v5, v6, v7, v8 };
			return quads;
		}

		private static float[][] screenUv(int textureId) {
			float u1 = textureId / 4 * 32 / TEX_SIZE;
			float u2 = u1 + 32 / TEX_SIZE;
			float v2 = textureId % 4 * 32 / TEX_SIZE;
			float v1 = v2 + 32 / TEX_SIZE;
			// mirrored so the side borders (findTexture bits 1 and 2) land on the outer edges of the screen
			return new float[][] { { u1, v1 }, { u2, v1 }, { u2, v2 }, { u1, v2 } };
		}

		private static float[][] bodyUv(float[][] quad, Direction side) {
			Direction north = rotate(side, Direction.NORTH);
			float[] c = BODY_UV[0];
			for (int k = 0; k < BODY_SIDES.length; k++)
				if (BODY_SIDES[k] == north)
					c = BODY_UV[k];
			int[] axes = planeAxes(north);
			float[][] uv = new float[4][];
			for (int i = 0; i < 4; i++) {
				float[] q = transform(quad[i][0], quad[i][1], quad[i][2], Direction.NORTH);
				float a = q[axes[0]], b = q[axes[1]];
				uv[i] = new float[] { c[0] + c[1] * a + c[2] * b, c[3] + c[4] * a + c[5] * b };
			}
			return uv;
		}

		private static float[][] buildBodyUv() {
			float[] p7 = { 0, 0, 0 }, p0 = { 1, 0, 0 }, p1 = { 1, 1, 0 }, p2 = { 0, 1, 0 };
			float[] p3 = { 0, 0, 1 }, p4 = { 1, 0, 1 }, p5 = { 1, 1, 1 }, p6 = { 0, 1, 1 };
			float[][][] quads = { { p1, p5, p4, p0 }, { p6, p2, p7, p3 }, { p7, p0, p4, p3 }, { p6, p5, p1, p2 }, { p5, p6, p3, p4 } };
			float[][] rects = { { 0, 32, 32, 64 }, { 64, 32, 96, 64 }, { 32, 64, 64, 96 }, { 32, 0, 64, 32 }, { 96, 32, 128, 64 } };
			float[][] result = new float[quads.length][];
			for (int k = 0; k < quads.length; k++) {
				float u1 = rects[k][0] / TEX_SIZE, v1 = rects[k][1] / TEX_SIZE, u2 = rects[k][2] / TEX_SIZE, v2 = rects[k][3] / TEX_SIZE;
				float[][] uv = { { u2, v1 }, { u1, v1 }, { u1, v2 }, { u2, v2 } };
				int[] axes = planeAxes(BODY_SIDES[k]);
				float[][] m = new float[3][];
				for (int i = 0; i < 3; i++)
					m[i] = new float[] { 1, quads[k][i][axes[0]], quads[k][i][axes[1]] };
				float[] cu = solve(m, uv[0][0], uv[1][0], uv[2][0]);
				float[] cv = solve(m, uv[0][1], uv[1][1], uv[2][1]);
				result[k] = new float[] { cu[0], cu[1], cu[2], cv[0], cv[1], cv[2] };
			}
			return result;
		}

		private static int[] planeAxes(Direction side) {
			switch (side.getAxis()) {
			case X:
				return new int[] { 1, 2 };
			case Y:
				return new int[] { 0, 2 };
			default:
				return new int[] { 0, 1 };
			}
		}

		// Cramer's rule for m * c = (r0, r1, r2)
		private static float[] solve(float[][] m, float r0, float r1, float r2) {
			float det = det(m[0][0], m[0][1], m[0][2], m[1][0], m[1][1], m[1][2], m[2][0], m[2][1], m[2][2]);
			return new float[] {
				det(r0, m[0][1], m[0][2], r1, m[1][1], m[1][2], r2, m[2][1], m[2][2]) / det,
				det(m[0][0], r0, m[0][2], m[1][0], r1, m[1][2], m[2][0], r2, m[2][2]) / det,
				det(m[0][0], m[0][1], r0, m[1][0], m[1][1], r1, m[2][0], m[2][1], r2) / det };
		}

		private static float det(float a, float b, float c, float d, float e, float f, float g, float h, float i) {
			return a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g);
		}

		// the model is built facing UP; these match the rotations the block entity renderer used
		private static float[] transform(float x, float y, float z, Direction facing) {
			switch (facing) {
			case NORTH:
				return new float[] { x, z, 1 - y };
			case SOUTH:
				return new float[] { x, 1 - z, y };
			case DOWN:
				return new float[] { x, 1 - y, 1 - z };
			case WEST:
				return new float[] { 1 - y, x, z };
			case EAST:
				return new float[] { y, 1 - x, z };
			default:
				return new float[] { x, y, z };
			}
		}

		private static Direction rotate(Direction side, Direction facing) {
			float[] p = transform(side.getStepX(), side.getStepY(), side.getStepZ(), facing);
			float[] o = transform(0, 0, 0, facing);
			return Direction.getApproximateNearest(p[0] - o[0], p[1] - o[1], p[2] - o[2]);
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
