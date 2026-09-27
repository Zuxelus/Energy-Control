package com.zuxelus.energycontrol.renderers;

import java.util.function.Function;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

// Render types for things drawn on top of a screen (images, bars, holo background).
// They use the world text pipeline with polygon offset, so they are drawn over the screen face without z-fighting
public final class ModRenderTypes {
	public static final Identifier WHITE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/misc/white.png");

	private static final Function<Identifier, RenderType> SCREEN_IMAGE = Util.memoize(texture -> RenderType.create("energycontrol_screen_image",
			RenderSetup.builder(RenderPipelines.TEXT_POLYGON_OFFSET).setOitPipelines(RenderPipelines.OIT_TEXT_POLYGON_OFFSET)
			.withTexture("Sampler0", texture).useLightmap().sortOnUpload().createRenderSetup()));

	private ModRenderTypes() { }

	// POSITION_COLOR_TEX_LIGHTMAP
	public static RenderType screenImage(Identifier texture) {
		return SCREEN_IMAGE.apply(texture);
	}

	// POSITION_COLOR_TEX_LIGHTMAP, use uv inside 0..1
	public static RenderType screenColor() {
		return SCREEN_IMAGE.apply(WHITE);
	}
}
