package com.zuxelus.energycontrol.tileentities;

import java.util.Objects;

import com.zuxelus.energycontrol.renderers.RotationOffset;

/**
 * Snapshot of what the baked panel model needs, taken on the main thread when the chunk mesh is built.
 * textureId: face border variant (0-15), see findTexture()
 * color: screen background color (ARGB)
 * offset: sloped/thin geometry of an advanced panel, null for a full cube
 */
public final class PanelRenderData {
	private final int textureId;
	private final int color;
	private final boolean powered;
	private final RotationOffset offset;

	public PanelRenderData(int textureId, int color, boolean powered, RotationOffset offset) {
		this.textureId = textureId;
		this.color = color;
		this.powered = powered;
		this.offset = offset;
	}

	public int textureId() {
		return textureId;
	}

	public int color() {
		return color;
	}

	public boolean powered() {
		return powered;
	}

	public RotationOffset offset() {
		return offset;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PanelRenderData))
			return false;
		PanelRenderData other = (PanelRenderData) o;
		return textureId == other.textureId && color == other.color && powered == other.powered && Objects.equals(offset, other.offset);
	}

	@Override
	public int hashCode() {
		return Objects.hash(textureId, color, powered, offset);
	}
}
