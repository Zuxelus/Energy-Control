package com.zuxelus.energycontrol.gui.controls;

import com.zuxelus.energycontrol.EnergyControl;
import net.minecraft.client.renderer.RenderPipelines;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

@Environment(EnvType.CLIENT)
public class CompactButton extends Button {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/gui/gui_thermal_monitor.png");
	private int id;

	public CompactButton(int id, int x, int y, int widthIn, int heightIn, Component buttonText, Button.OnPress onPress) {
		super(x, y, widthIn, heightIn, buttonText, onPress, DEFAULT_NARRATION);
		this.id = id;
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		int i = !active ? 0 : isHovered() ? 2 : 1;
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY(), 0, 64 + i * 12, width / 2 + width % 2, height, 256, 256);
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX() + width / 2 + width % 2, getY(), 200 - width / 2, 64 + i * 12, width / 2, height, 256, 256);
		FormattedCharSequence ireorderingprocessor = getMessage().getVisualOrderText();
		context.text(fontRenderer, ireorderingprocessor, getX() + (width - fontRenderer.width(ireorderingprocessor)) / 2, getY() + (height - 8) / 2, 0xFF404040, false);
	}

	public int getId() {
		return id;
	}
}
