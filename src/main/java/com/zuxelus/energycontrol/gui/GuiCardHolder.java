package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.containers.ContainerBase;
import com.zuxelus.energycontrol.containers.ContainerCardHolder;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiCardHolder extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private final int inventoryRows;
	private EntityPlayer player;
	private String name;

	public GuiCardHolder(EntityPlayer player) {
		super(new ContainerCardHolder(player));
		this.player = player;
		name = I18n.format("item.card_holder.name");
		inventoryRows = 6;
		ySize = 114 + inventoryRows * 18;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(name, 8, 6, 4210752);
		this.fontRenderer.drawString(player.inventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, inventoryRows * 18 + 17);
		drawTexturedModalRect(left, top + inventoryRows * 18 + 17, 0, 126, xSize, 96);
	}
}
