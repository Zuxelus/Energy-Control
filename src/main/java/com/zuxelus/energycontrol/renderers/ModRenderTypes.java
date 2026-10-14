package com.zuxelus.energycontrol.renderers;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

// Render types for things drawn on top of a screen (images, bars, holo background).
// They use the text shaders with polygon offset, so they are drawn over the screen face without z-fighting
public final class ModRenderTypes {
	private static final Function<ResourceLocation, RenderType> SCREEN_IMAGE = Util.memoize(texture -> RenderType.create("energycontrol_screen_image",
			DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,
			RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_TEXT_SHADER)
			.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
			.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setCullState(RenderStateShard.NO_CULL)
			.setLightmapState(RenderStateShard.LIGHTMAP).setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING).createCompositeState(false)));

	private static final RenderType SCREEN_COLOR = RenderType.create("energycontrol_screen_color",
			DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,
			RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_TEXT_BACKGROUND_SHADER)
			.setTextureState(RenderStateShard.NO_TEXTURE)
			.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setCullState(RenderStateShard.NO_CULL)
			.setLightmapState(RenderStateShard.LIGHTMAP).setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING).createCompositeState(false));

	// back-face culled and without depth write: a depth-writing holo background would hide translucent terrain behind it
	private static final RenderType HOLO_COLOR = RenderType.create("energycontrol_holo_color",
			DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,
			RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_TEXT_BACKGROUND_SHADER)
			.setTextureState(RenderStateShard.NO_TEXTURE)
			.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setWriteMaskState(RenderStateShard.COLOR_WRITE)
			.setLightmapState(RenderStateShard.LIGHTMAP).setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING).createCompositeState(false));

	private ModRenderTypes() { }

	// POSITION_COLOR_TEX_LIGHTMAP
	public static RenderType screenImage(ResourceLocation texture) {
		return SCREEN_IMAGE.apply(texture);
	}

	// POSITION_COLOR_LIGHTMAP
	public static RenderType screenColor() {
		return SCREEN_COLOR;
	}

	// POSITION_COLOR_LIGHTMAP; doesn't write depth
	public static RenderType holoColor() {
		return HOLO_COLOR;
	}
}
