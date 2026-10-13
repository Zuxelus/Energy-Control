package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.containers.ContainerCardHolder;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;

public class GuiCardHolder extends AbstractContainerScreen<ContainerCardHolder> {
	private static final Identifier TEXTURE = Identifier.parse("textures/gui/container/generic_54.png");
	private final int inventoryRows;

	public GuiCardHolder(ContainerCardHolder container, Inventory inventory, Component title) {
		super(container, inventory, title, DEFAULT_IMAGE_WIDTH, 114 + 6 * 18);
		inventoryRows = 6;
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
		matrixStack.text(font, title, 8, 6, ARGB.opaque(0x404040), false);
		matrixStack.text(font, playerInventoryTitle, 8, imageHeight - 96 + 2, ARGB.opaque(0x404040), false);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(matrixStack, mouseX, mouseY, partialTicks);
		matrixStack.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, inventoryRows * 18 + 17, 256, 256);
		matrixStack.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos + inventoryRows * 18 + 17, 0, 126, imageWidth, 96, 256, 256);
	}
}
