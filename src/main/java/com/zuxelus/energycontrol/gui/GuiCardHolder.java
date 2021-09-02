package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.containers.ContainerCardHolder;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCardHolder extends ContainerScreen<ContainerCardHolder> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private final int inventoryRows;
	private PlayerEntity player;
	private String name;

	public GuiCardHolder(ContainerCardHolder container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title);
		this.player = inventory.player;
		inventoryRows = 6;
		imageHeight = 114 + inventoryRows * 18;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int x, int y) {
		font.draw(matrixStack, name, 8, 6, 4210752);
		font.draw(matrixStack, player.inventory.getDisplayName().getContents(), 8, imageHeight - 96 + 2, 4210752);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, inventoryRows * 18 + 17);
		blit(matrixStack, leftPos, topPos + inventoryRows * 18 + 17, 0, 126, imageWidth, 96);
	}
}
