package com.zuxelus.energycontrol.gui.controls;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;

/** A vanilla button that stays clickable but looks pressed (like a disabled one) while it is on. */
@Environment(EnvType.CLIENT)
public class ToggleButton extends Button {
	private static final Identifier PRESSED = Identifier.withDefaultNamespace("widget/button_disabled");
	private boolean pressed;

	public ToggleButton(int x, int y, int width, int height, Component message, boolean pressed, OnPress onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
		this.pressed = pressed;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		if (!pressed || !active) {
			extractDefaultSprite(context);
			extractDefaultLabel(context.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
			return;
		}
		context.blitSprite(RenderPipelines.GUI_TEXTURED, PRESSED, getX(), getY(), getWidth(), getHeight(), ARGB.white(alpha));
		Font font = Minecraft.getInstance().font;
		context.centeredText(font, getMessage(), getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, ARGB.color(alpha, 0xA0A0A0));
	}
}
