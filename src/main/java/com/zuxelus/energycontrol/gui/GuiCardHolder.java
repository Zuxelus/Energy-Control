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
		ySize = 114 + inventoryRows * 18;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		font.drawString(name, 8, 6, 4210752);
		font.drawString(player.inventory.getDisplayName().getUnformattedComponentText(), 8, ySize - 96 + 2, 4210752);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		blit(guiLeft, guiTop, 0, 0, xSize, inventoryRows * 18 + 17);
		blit(guiLeft, guiTop + inventoryRows * 18 + 17, 0, 126, xSize, 96);
	}
}
