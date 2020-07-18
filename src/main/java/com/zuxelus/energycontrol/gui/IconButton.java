package com.zuxelus.energycontrol.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IconButton extends GuiButton {
	private ResourceLocation textureLocation;
	public int textureLeft;
	public int textureTop;

	public IconButton(int id, int left, int top, int width, int height, ResourceLocation textureLocation, int textureLeft, int textureTop) {
		super(id, left, top, width, height, "");
		this.textureLocation = textureLocation;
		this.textureLeft = textureLeft;
		this.textureTop = textureTop;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(!visible)
			return;

		mc.getTextureManager().bindTexture(textureLocation);
		if (mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height)
			drawGradientRect(xPosition, yPosition, xPosition + width, yPosition + height, 0x80FFFFFF, 0x80FFFFFF);
		drawTexturedModalRect(xPosition, yPosition, textureLeft, textureTop, width, height);
		mouseDragged(mc, mouseX, mouseY);
	}
}
