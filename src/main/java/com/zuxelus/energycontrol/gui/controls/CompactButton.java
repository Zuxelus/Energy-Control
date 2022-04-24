package com.zuxelus.energycontrol.gui.controls;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CompactButton extends GuiButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_thermal_monitor.png");

	public CompactButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;

		FontRenderer fontRenderer = mc.fontRendererObj;
		mc.getTextureManager().bindTexture(TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		hovered = mouseX > xPosition && mouseY > yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		int i = getHoverState(hovered);
		drawTexturedModalRect(xPosition, yPosition, 0, 64 + i * 12, width / 2 + width % 2, height);
		drawTexturedModalRect(xPosition + width / 2 + width % 2, yPosition, 200 - width / 2, 64 + i * 12, width / 2, height);
		mouseDragged(mc, mouseX, mouseY);
		fontRenderer.drawString(displayString, xPosition + (width - fontRenderer.getStringWidth(displayString)) / 2, yPosition + 2, 0x404040);
	}
}
