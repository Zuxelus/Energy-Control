package com.zuxelus.energycontrol.gui.controls;

import com.zuxelus.energycontrol.EnergyControl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CompactButton extends ButtonWidget {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_thermal_monitor.png");
	private int id;

	public CompactButton(int id, int x, int y, int widthIn, int heightIn, Text buttonText, ButtonWidget.PressAction onPress) {
		super(x, y, widthIn, heightIn, buttonText, onPress, DEFAULT_NARRATION_SUPPLIER);
		this.id = id;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		MinecraftClient minecraft = MinecraftClient.getInstance();
		TextRenderer fontRenderer = minecraft.textRenderer;
		int i = !active ? 0 : isHovered() ? 2 : 1;
		context.drawTexture(TEXTURE, getX(), getY(), 0, 64 + i * 12, width / 2 + width % 2, height);
		context.drawTexture(TEXTURE, getX() + width / 2 + width % 2, getY(), 200 - width / 2, 64 + i * 12, width / 2, height);
		OrderedText ireorderingprocessor = getMessage().asOrderedText();
		context.drawText(fontRenderer, ireorderingprocessor, getX() + (width - fontRenderer.getWidth(ireorderingprocessor)) / 2, getY() + (height - 8) / 2, 0x404040, false);
	}

	public int getId() {
		return id;
	}
}
