package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** A vanilla button that stays clickable but looks pressed (like a disabled one) while it is on. */
public class ToggleButton extends Button {
	private static final ResourceLocation PRESSED = ResourceLocation.withDefaultNamespace("widget/button_disabled");
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
	protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
		if (!pressed || !active) {
			super.renderWidget(context, mouseX, mouseY, delta);
			return;
		}
		context.setColor(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		context.blitSprite(PRESSED, getX(), getY(), getWidth(), getHeight());
		context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		renderString(context, Minecraft.getInstance().font, 0xA0A0A0 | Mth.ceil(alpha * 255.0F) << 24);
	}
}
