package com.zuxelus.energycontrol.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.screen.handlers.CardHolderScreenHandler;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CardHolderScreen extends HandledScreen<CardHolderScreenHandler> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_card_holder.png");
	private final int inventoryRows;
	private PlayerEntity player;

	public CardHolderScreen(CardHolderScreenHandler container, PlayerInventory playerInventory, Text text) {
		super(container, playerInventory, text);
		this.player = playerInventory.player;
		inventoryRows = 6;
		backgroundHeight = 114 + inventoryRows * 18;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		textRenderer.draw(matrices, getTitle(), 8, 6, 4210752);
		textRenderer.draw(matrices, player.inventory.getDisplayName(), 8, backgroundHeight - 96 + 2, 4210752);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		client.getTextureManager().bindTexture(TEXTURE);
		int left = (width - backgroundWidth) / 2;
		int top = (height - backgroundHeight) / 2;
		drawTexture(matrices, left, top, 0, 0, backgroundWidth, inventoryRows * 18 + 17);
		drawTexture(matrices, left, top + inventoryRows * 18 + 17, 0, 126, backgroundWidth, 96);
	}
}
