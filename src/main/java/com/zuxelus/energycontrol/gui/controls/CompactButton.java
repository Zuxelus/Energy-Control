package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class CompactButton extends Button {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_thermal_monitor.png");
	private int id;

	public CompactButton(int id, int x, int y, int widthIn, int heightIn, Component buttonText, Button.OnPress onPress) {
		super(x, y, widthIn, heightIn, buttonText, onPress, Button.DEFAULT_NARRATION);
		this.id = id;
	}

	@Override
	public void renderWidget(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		matrixStack.setColor(1.0F, 1.0F, 1.0F, this.alpha);
		int i = getTextureY();
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		matrixStack.blit(TEXTURE, getX(), getY(), 0, 64 + i * 12, width / 2 + width % 2, height);
		matrixStack.blit(TEXTURE, getX() + width / 2 + width % 2, getY(), 200 - width / 2, 64 + i * 12, width / 2, height);
		FormattedCharSequence ireorderingprocessor = getMessage().getVisualOrderText();
		matrixStack.drawString(fontRenderer, ireorderingprocessor, getX() + (width - fontRenderer.width(ireorderingprocessor)) / 2, getY() + (height - 8) / 2, 0x404040, false);
	}

	private int getTextureY() {
		int i = 1;
		if (!this.active) {
			i = 0;
		} else if (this.isHoveredOrFocused()) {
			i = 2;
		}
		return i;
	}

	public int getId() {
		return id;
	}
}
