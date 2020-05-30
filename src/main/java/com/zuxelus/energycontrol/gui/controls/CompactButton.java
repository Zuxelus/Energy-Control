package com.zuxelus.energycontrol.gui.controls;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

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

		FontRenderer fontRenderer = mc.fontRenderer;
		mc.getTextureManager().bindTexture(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		int i = this.getHoverState(this.field_146123_n);
		this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 64 + i * 12, this.width / 2 + width % 2, this.height);
		this.drawTexturedModalRect(this.xPosition + this.width / 2 + width % 2, this.yPosition, 200 - this.width / 2, 64 + i * 12, this.width / 2, this.height);
		this.mouseDragged(mc, mouseX, mouseY);
		fontRenderer.drawString(displayString, xPosition + (width - fontRenderer.getStringWidth(displayString)) / 2, yPosition + 2, 0x404040);
	}
}
