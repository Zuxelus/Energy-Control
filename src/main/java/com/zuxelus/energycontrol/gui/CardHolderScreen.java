package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.CardHolderContainer;

import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class CardHolderScreen extends ContainerScreen<CardHolderContainer> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_card_holder.png");
	private final int inventoryRows;
	private PlayerEntity player;

	public CardHolderScreen(CardHolderContainer container, PlayerEntity player) {
		super(container, player.inventory, new TranslatableText("item.energycontrol.card_holder"));
		this.player = player;
		inventoryRows = 6;
		containerHeight = 114 + inventoryRows * 18;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		super.render(mouseX, mouseY, delta);
		drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		font.draw(getTitle().asFormattedString(), 8, 6, 4210752);
		font.draw(player.inventory.getDisplayName().asFormattedString(), 8, containerHeight - 96 + 2, 4210752);
	}

	@Override
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int left = (width - containerWidth) / 2;
		int top = (height - containerHeight) / 2;
		blit(left, top, 0, 0, containerWidth, inventoryRows * 18 + 17);
		blit(left, top + inventoryRows * 18 + 17, 0, 126, containerWidth, 96);
	}
}
