package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.containers.ContainerCardHolder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCardHolder extends AbstractContainerScreen<ContainerCardHolder> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private final int inventoryRows;
	private Player player;
	private String name;

	public GuiCardHolder(ContainerCardHolder container, Inventory inventory, Component title) {
		super(container, inventory, title);
		this.player = inventory.player;
		inventoryRows = 6;
		imageHeight = 114 + inventoryRows * 18;
	}

	@Override
	public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics matrixStack, int x, int y) {
		matrixStack.drawString(font, name, 8, 6, 4210752, false);
		matrixStack.drawString(font, player.getInventory().getDisplayName(), 8, imageHeight - 96 + 2, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics matrixStack, float partialTicks, int x, int y) {
		matrixStack.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, inventoryRows * 18 + 17);
		matrixStack.blit(TEXTURE, leftPos, topPos + inventoryRows * 18 + 17, 0, 126, imageWidth, 96);
	}
}
