package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.containers.ContainerCardHolder;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class GuiCardHolder extends AbstractContainerScreen<ContainerCardHolder> {
	private static final Identifier TEXTURE = Identifier.parse("textures/gui/container/generic_54.png");
	private final int inventoryRows;
	private Player player;
	private String name;

	public GuiCardHolder(ContainerCardHolder container, Inventory inventory, Component title) {
		super(container, inventory, title, 176, 114 + 6 * 18);
		this.player = inventory.player;
		inventoryRows = 6;
		name = title.getString();
	}


	@Override
	protected void extractLabels(GuiGraphicsExtractor matrixStack, int x, int y) {
		matrixStack.text(font, name, 8, 6, ARGB.opaque(4210752), false);
		matrixStack.text(font, player.getInventory().getDisplayName(), 8, imageHeight - 96 + 2, ARGB.opaque(4210752), false);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor matrixStack, int x, int y, float partialTicks) {
		super.extractBackground(matrixStack, x, y, partialTicks);
		matrixStack.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, inventoryRows * 18 + 17, 256, 256);
		matrixStack.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos + inventoryRows * 18 + 17, 0, 126, imageWidth, 96, 256, 256);
	}
}
