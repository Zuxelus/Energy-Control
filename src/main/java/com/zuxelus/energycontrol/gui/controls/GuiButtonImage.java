package com.zuxelus.energycontrol.gui.controls;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiButtonImage extends GuiButton {
	private final ResourceLocation resourceLocation;
	private int xTexStart;
	private int yTexStart;
	private int yDiffText;
	protected boolean hovered;
	public String tooltip;

	public GuiButtonImage(int id, int left, int top, int width, int height, int p_i47392_6_, int p_i47392_7_, int p_i47392_8_, ResourceLocation location) {
		super(id, left, top, width, height, "");
		this.xTexStart = p_i47392_6_;
		this.yTexStart = p_i47392_7_;
		this.yDiffText = p_i47392_8_;
		this.resourceLocation = location;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;
		hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		mc.getTextureManager().bindTexture(this.resourceLocation);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		int j = yTexStart;
		if (hovered)
			j += yDiffText;

		drawTexturedModalRect(xPosition, yPosition, xTexStart, j, width, height);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public boolean isMouseOver() {
		return hovered;
	}
	
	public void setTextureY(int y) {
		yTexStart = y;
	}

	public String getActiveTooltip(int mouseX, int mouseY)
	{
		if (mouseX < xPosition || mouseX >= xPosition + width || mouseY < yPosition || mouseY >= yPosition + height)
			return null;
		return tooltip;
	}
}
