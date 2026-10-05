package com.zuxelus.energycontrol.gui.controls;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/** A vanilla button that stays clickable but looks pressed (like a disabled one) while it is on. */
@Environment(EnvType.CLIENT)
public class ToggleButton extends ButtonWidget {
	private boolean pressed;

	public ToggleButton(int x, int y, int width, int height, Text message, boolean pressed, PressAction onPress, TooltipSupplier tooltipSupplier) {
		super(x, y, width, height, message, onPress, tooltipSupplier);
		this.pressed = pressed;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		if (!pressed || !active) {
			super.renderButton(matrixStack, mouseX, mouseY, delta);
			return;
		}
		// vanilla draws the disabled sprite and grey text when inactive; the button stays clickable
		active = false;
		super.renderButton(matrixStack, mouseX, mouseY, delta);
		active = true;
	}
}
