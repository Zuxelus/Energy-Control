package com.zuxelus.energycontrol.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonImage extends GuiButton {
	private final ResourceLocation resourceLocation;
	private final int xTexStart;
	private final int yTexStart;
	private final int yDiffText;

	public GuiButtonImage(int id, int left, int top, int width, int height, int p_i47392_6_, int p_i47392_7_, int p_i47392_8_, ResourceLocation p_i47392_9_) {
		super(id, left, top, width, height, "");
		this.xTexStart = p_i47392_6_;
		this.yTexStart = p_i47392_7_;
		this.yDiffText = p_i47392_8_;
		this.resourceLocation = p_i47392_9_;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;
		hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width
				&& mouseY < yPosition + height;
		mc.getTextureManager().bindTexture(this.resourceLocation);
		GlStateManager.disableDepth();
		int j = yTexStart;
		if (hovered)
			j += yDiffText;

		drawTexturedModalRect(xPosition, yPosition, xTexStart, j, width, height);
		GlStateManager.enableDepth();
	}
}
