package com.zuxelus.energycontrol.renderers;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

// Render types for things drawn on top of a screen (images, bars, holo background).
// They go through MultiBufferSource like everything else, so they are sorted and flushed together with the rest of the world
public abstract class ModRenderTypes extends RenderType {
	public static final RenderType SCREEN_COLOR = create("energycontrol_screen_color", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,
			CompositeState.builder().setShaderState(RENDERTYPE_TEXT_BACKGROUND_SHADER).setTextureState(NO_TEXTURE)
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false));

	private static final Function<ResourceLocation, RenderType> SCREEN_IMAGE = Util.memoize(texture -> create("energycontrol_screen_image", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,
			CompositeState.builder().setShaderState(RENDERTYPE_TEXT_SHADER).setTextureState(new TextureStateShard(texture, false, false))
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false)));

	private ModRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}

	public static RenderType screenImage(ResourceLocation texture) {
		return SCREEN_IMAGE.apply(texture);
	}
}
