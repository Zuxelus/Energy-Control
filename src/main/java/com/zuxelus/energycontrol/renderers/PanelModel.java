package com.zuxelus.energycontrol.renderers;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.tileentities.PanelRenderData;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.blocks.FacingBlockActive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class PanelModel implements UnbakedModel {
	private static final RotationOffset FULL = new RotationOffset();

	private final Material body;
	private final Material screen;
	private final Material particle;
	private final int defaultColor;

	public PanelModel(ResourceLocation body, ResourceLocation screen, ResourceLocation particle, int defaultColor) {
		this.body = new Material(InventoryMenu.BLOCK_ATLAS, body);
		this.screen = new Material(InventoryMenu.BLOCK_ATLAS, screen);
		this.particle = new Material(InventoryMenu.BLOCK_ATLAS, particle);
		this.defaultColor = defaultColor;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelLoader) { }

	@Override
	public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> textureGetter, ModelState rotationContainer) {
		return new Baked(textureGetter.apply(body), textureGetter.apply(screen), textureGetter.apply(particle), defaultColor);
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

	private static class Baked implements BakedModel {
		private static final float EPSILON = 1.0E-4F;

		private final TextureAtlasSprite body;
		private final TextureAtlasSprite screen;
		private final TextureAtlasSprite particle;
		private final int defaultColor;
		// a powered screen glows like a display: full brightness, no ambient occlusion (null = default material)
		private final RenderMaterial glowing;

		Baked(TextureAtlasSprite body, TextureAtlasSprite screen, TextureAtlasSprite particle, int defaultColor) {
			this.body = body;
			this.screen = screen;
			this.particle = particle;
			this.defaultColor = defaultColor;
			Renderer renderer = RendererAccess.INSTANCE.getRenderer();
			glowing = renderer == null ? null : renderer.materialFinder().emissive(true).ambientOcclusion(TriState.FALSE).find();
		}

		@Override
		public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
			PanelRenderData data = blockView.getBlockEntityRenderData(pos) instanceof PanelRenderData d ? d : null;
			if (data == null)
				data = new PanelRenderData(15, defaultColor, state.getValue(FacingBlockActive.ACTIVE), null, null);

			// same geometry and rotation as the block entity renderer uses, so the screen borders match findTexture()
			PoseStack matrixStack = new PoseStack();
			PanelCube.rotateBlock(matrixStack, state.getValue(FacingBlock.FACING), data.rotation());
			QuadEmitter emitter = context.getEmitter();
			PanelCube model = data.offset() == null ? PanelCube.MODEL : PanelCube.getModel(data.offset());
			// the body's face quad lies under the screen
			model.visitQuads(matrixStack, (index, positions, u, v, normal) -> {
				if (index != PanelCube.FACE)
					emitQuad(emitter, positions, u, v, normal, body, -1, null);
			});
			int faceColor = getFaceColor(data.color(), data.powered());
			RenderMaterial material = data.powered() ? glowing : null;
			PanelCube.getFaceModel(data.offset() == null ? FULL : data.offset(), data.textureId()).visitQuads(matrixStack,
					(index, positions, u, v, normal) -> emitQuad(emitter, positions, u, v, normal, screen, faceColor, material));
		}

		private static void emitQuad(QuadEmitter emitter, Vector3f[] positions, float[] u, float[] v, Vector3f normal, TextureAtlasSprite sprite, int color, RenderMaterial material) {
			Direction side = Direction.getNearest(normal.x(), normal.y(), normal.z());
			float bound = side.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0F : 0.0F;
			emitter.material(material);
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
			// a quad lying on the block boundary is hidden by a full neighbour on that side
			emitter.nominalFace(side);
			emitter.cullFace(flush ? side : null);
			emitter.emit();
		}

		@Override
		public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) { }

		@Override
		public boolean isVanillaAdapter() {
			return false;
		}

		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction face, RandomSource random) {
			return Collections.emptyList();
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
		public ItemTransforms getTransforms() {
			return ItemTransforms.NO_TRANSFORMS;
		}

		@Override
		public ItemOverrides getOverrides() {
			return ItemOverrides.EMPTY;
		}
	}
}
