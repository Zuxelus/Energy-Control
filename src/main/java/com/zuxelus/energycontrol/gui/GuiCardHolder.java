package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.containers.ContainerCardHolder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiCardHolder extends HandledScreen<ContainerCardHolder> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
	private final int inventoryRows;
	private PlayerEntity player;
	private String name;

	public GuiCardHolder(ContainerCardHolder container, PlayerInventory inventory, Text title) {
		super(container, inventory, title);
		this.player = inventory.player;
		inventoryRows = 6;
		backgroundHeight = 114 + inventoryRows * 18;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		drawMouseoverTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int x, int y) {
		textRenderer.draw(matrixStack, name, 8, 6, 4210752);
		textRenderer.draw(matrixStack, player.getInventory().getDisplayName().asString(), 8, backgroundHeight - 96 + 2, 4210752);
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexture(matrixStack, x, y, 0, 0, backgroundWidth, inventoryRows * 18 + 17);
		drawTexture(matrixStack, x, y + inventoryRows * 18 + 17, 0, 126, backgroundWidth, 96);
	}
}
