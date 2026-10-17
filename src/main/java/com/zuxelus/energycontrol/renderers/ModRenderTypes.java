package com.zuxelus.energycontrol.renderers;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

// Render types for things drawn on top of a screen (images, bars, holo background).
// They use polygon offset, so they are drawn over the screen face without z-fighting
@Environment(EnvType.CLIENT)
public final class ModRenderTypes {
	private static final Function<ResourceLocation, RenderType> SCREEN_IMAGE = Util.memoize(texture -> RenderType.create("energycontrol_screen_image",
			DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,
			RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_TEXT_SHADER)
			.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
			.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setCullState(RenderStateShard.NO_CULL)
			.setLightmapState(RenderStateShard.LIGHTMAP).setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING).createCompositeState(false)));

	// not culled and without depth write: a depth-writing translucent quad would hide translucent terrain behind it, and one quad is visible from both sides
	private static final RenderType SCREEN_COLOR = RenderType.create("energycontrol_screen_color",
			DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true,
			RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
			.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setCullState(RenderStateShard.NO_CULL)
			.setWriteMaskState(RenderStateShard.COLOR_WRITE).setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING).createCompositeState(false));

	private ModRenderTypes() { }

	// POSITION_COLOR_TEX_LIGHTMAP
	public static RenderType screenImage(ResourceLocation texture) {
		return SCREEN_IMAGE.apply(texture);
	}

	// POSITION_COLOR; doesn't write depth
	public static RenderType screenColor() {
		return SCREEN_COLOR;
	}
}
