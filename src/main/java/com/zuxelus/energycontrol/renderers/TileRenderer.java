package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Base for the mod's block entity renderers. The render state keeps the block entity itself,
 * the actual drawing is done in {@link #render} with the submit node collector.
 */
public abstract class TileRenderer<T extends BlockEntity> implements BlockEntityRenderer<T, TileRenderer.State<T>> {

	public static class State<T> extends BlockEntityRenderState {
		public T te;
	}

	@Override
	public State<T> createRenderState() {
		return new State<>();
	}

	@Override
	public void extractRenderState(T blockEntity, State<T> state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.te = blockEntity;
	}

	@Override
	public void submit(State<T> state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.te == null || state.te.isRemoved())
			return;
		render(state.te, poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY);
	}

	protected abstract void render(T te, PoseStack matrixStack, SubmitNodeCollector collector, int combinedLight, int combinedOverlay);

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
