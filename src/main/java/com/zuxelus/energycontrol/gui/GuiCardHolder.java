package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.containers.ContainerCardHolder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class GuiCardHolder extends AbstractContainerScreen<ContainerCardHolder> {
	private static final ResourceLocation TEXTURE = ResourceLocation.parse("textures/gui/container/generic_54.png");
	private final int inventoryRows;

	public GuiCardHolder(ContainerCardHolder container, Inventory inventory, Component title) {
		super(container, inventory, title);
		imageHeight = 114 + 6 * 18;
		inventoryLabelY = imageHeight - 94;
		inventoryRows = 6;
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
		super.render(context, mouseX, mouseY, partialTicks);
		renderTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
		context.drawString(font, title, 8, 6, 0x404040, false);
		context.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 0x404040, false);
	}

	@Override
	protected void renderBg(GuiGraphics context, float partialTicks, int mouseX, int mouseY) {
		context.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, inventoryRows * 18 + 17, 256, 256);
		context.blit(TEXTURE, leftPos, topPos + inventoryRows * 18 + 17, 0, 126, imageWidth, 96, 256, 256);
	}
}
