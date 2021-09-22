package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.containers.ContainerCardHolder;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
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
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int x, int y) {
		font.draw(matrixStack, name, 8, 6, 4210752);
		font.draw(matrixStack, player.getInventory().getDisplayName().getContents(), 8, imageHeight - 96 + 2, 4210752);
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, inventoryRows * 18 + 17);
		blit(matrixStack, leftPos, topPos + inventoryRows * 18 + 17, 0, 126, imageWidth, 96);
	}
}
