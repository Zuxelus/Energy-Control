package com.zuxelus.energycontrol.renderers;

import com.zuxelus.zlib.tileentities.BlockEntityFacing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

/**
 * The renderers read most of their data straight from the block entity (screens, cards, ...),
 * so the state keeps a reference to it. It is only used on the render thread during submit.
 */
@Environment(EnvType.CLIENT)
public class BlockEntityFacingRenderState<T extends BlockEntityFacing> extends BlockEntityRenderState {
	public T te;
	public int[] light;
}
