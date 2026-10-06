package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.renderers.RotationOffset;

import net.minecraft.core.Direction;

import net.neoforged.neoforge.model.data.ModelProperty;

/**
 * Snapshot of what the baked panel model needs, taken on the main thread when the chunk mesh is built.
 * @param textureId face border variant (0-15), see findTexture()
 * @param color screen background color (ARGB)
 * @param rotation screen rotation of a panel facing up or down, null means north
 * @param offset sloped/thin geometry of an advanced panel, null for a full cube
 */
public record PanelRenderData(int textureId, int color, boolean powered, Direction rotation, RotationOffset offset) {
	public static final ModelProperty<PanelRenderData> PROPERTY = new ModelProperty<>();
}
