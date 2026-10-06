package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.containers.ContainerCardHolder;
import net.minecraft.client.renderer.RenderPipelines;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class GuiCardHolder extends AbstractContainerScreen<ContainerCardHolder> {
	private static final Identifier TEXTURE = Identifier.parse("textures/gui/container/generic_54.png");
	private final int inventoryRows;

	public GuiCardHolder(ContainerCardHolder container, Inventory inventory, Component title) {
		super(container, inventory, title, DEFAULT_IMAGE_WIDTH, 114 + 6 * 18);
		inventoryRows = 6;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(context, mouseX, mouseY, partialTicks);
		extractTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor context, int mouseX, int mouseY) {
		context.text(font, title, 8, 6, 0xFF404040, false);
		context.text(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 0xFF404040, false);
	}

	@Override
	// the last two parameters are the mouse position; x and y below are the screen position fields
	public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(context, mouseX, mouseY, partialTicks);
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, inventoryRows * 18 + 17, 256, 256);
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos + inventoryRows * 18 + 17, 0, 126, imageWidth, 96, 256, 256);
	}
}
