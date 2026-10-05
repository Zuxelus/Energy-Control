package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/** A vanilla button that stays clickable but looks pressed (like a disabled one) while it is on. */
@Environment(EnvType.CLIENT)
public class ToggleButton extends ButtonWidget {
	private static final Identifier PRESSED = Identifier.ofVanilla("widget/button_disabled");
	private boolean pressed;

	public ToggleButton(int x, int y, int width, int height, Text message, boolean pressed, PressAction onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
		this.pressed = pressed;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (!pressed || !active) {
			super.renderWidget(context, mouseX, mouseY, delta);
			return;
		}
		context.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		context.drawGuiTexture(PRESSED, getX(), getY(), getWidth(), getHeight());
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		drawMessage(context, MinecraftClient.getInstance().textRenderer, 0xA0A0A0 | MathHelper.ceil(alpha * 255.0F) << 24);
	}
}
