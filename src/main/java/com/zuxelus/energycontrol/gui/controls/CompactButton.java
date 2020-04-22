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
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_thermal_monitor.png");

	public CompactButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;

		FontRenderer fontrenderer = mc.fontRendererObj;
        mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int i = this.getHoverState(this.hovered);
		this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 64 + i * 12, this.width / 2 + width % 2, this.height);
		this.drawTexturedModalRect(this.xPosition + this.width / 2 + width % 2, this.yPosition, 200 - this.width / 2, 64 + i * 12, this.width / 2, this.height);
		this.mouseDragged(mc, mouseX, mouseY);
		fontrenderer.drawString(displayString, xPosition + (width - fontrenderer.getStringWidth(displayString)) / 2, yPosition + 2, 0x404040);
	}
}
