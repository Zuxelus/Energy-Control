package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.containers.ContainerCardHolder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiCardHolder extends HandledScreen<ContainerCardHolder> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
	private final int inventoryRows;

	public GuiCardHolder(ContainerCardHolder container, PlayerInventory inventory, Text title) {
		super(container, inventory, title);
		inventoryRows = 6;
		backgroundHeight = 114 + inventoryRows * 18;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		super.render(context, mouseX, mouseY, partialTicks);
		drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		context.drawText(textRenderer, title, 8, 6, 4210752, false);
		context.drawText(textRenderer, playerInventoryTitle, 8, backgroundHeight - 96 + 2, 4210752, false);
	}

	@Override
	// the last two parameters are the mouse position; x and y below are the screen position fields
	protected void drawBackground(DrawContext context, float partialTicks, int mouseX, int mouseY) {
		context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, inventoryRows * 18 + 17);
		context.drawTexture(TEXTURE, x, y + inventoryRows * 18 + 17, 0, 126, backgroundWidth, 96);
	}
}
