package com.zuxelus.energycontrol.renderers;

import java.util.function.Function;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

// Render types for things drawn on top of a screen (images, bars, holo background).
// They use the world text pipeline with polygon offset, so they are drawn over the screen face without z-fighting
public final class ModRenderTypes {
	public static final Identifier WHITE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/misc/white.png");

	// like TEXT_POLYGON_OFFSET, but without depth write: translucent features are drawn before water and other
	// translucent terrain, so a depth-writing holo background would hide everything translucent behind it
	public static final RenderPipeline HOLO_PIPELINE = RenderPipeline.builder(RenderPipelines.WORLD_TEXT_SNIPPET)
			.withLocation(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "pipeline/holo_background"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 1.0F, 10.0F))
			.build();

	private static final Function<Identifier, RenderType> SCREEN_IMAGE = Util.memoize(texture -> RenderType.create("energycontrol_screen_image",
			RenderSetup.builder(RenderPipelines.TEXT_POLYGON_OFFSET).setOitPipelines(RenderPipelines.OIT_TEXT_POLYGON_OFFSET)
			.withTexture("Sampler0", texture).useLightmap().sortOnUpload().createRenderSetup()));

	private static final RenderType HOLO_COLOR = RenderType.create("energycontrol_holo_color",
			RenderSetup.builder(HOLO_PIPELINE).setOitPipelines(RenderPipelines.OIT_TEXT_POLYGON_OFFSET)
			.withTexture("Sampler0", WHITE).useLightmap().sortOnUpload().createRenderSetup());

	private ModRenderTypes() { }

	public static void registerPipelines(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(HOLO_PIPELINE);
	}

	// POSITION_COLOR_TEX_LIGHTMAP
	public static RenderType screenImage(Identifier texture) {
		return SCREEN_IMAGE.apply(texture);
	}

	// POSITION_COLOR_TEX_LIGHTMAP, use uv inside 0..1
	public static RenderType screenColor() {
		return SCREEN_IMAGE.apply(WHITE);
	}

	// POSITION_COLOR_TEX_LIGHTMAP, use uv inside 0..1; doesn't write depth
	public static RenderType holoColor() {
		return HOLO_COLOR;
	}
}
