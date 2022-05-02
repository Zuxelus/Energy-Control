package com.zuxelus.energycontrol.gui;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.containers.ContainerCardHolder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiCardHolder extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private final int inventoryRows;
	private EntityPlayer player;
	private String name;

	public GuiCardHolder(EntityPlayer player) {
		super(new ContainerCardHolder(player));
		this.player = player;
		name = I18n.format("item.card_holder.name");
		inventoryRows = 6;
		ySize = 114 + inventoryRows * 18;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(name, 8, 6, 4210752);
		fontRendererObj.drawString(player.inventory.getInventoryName(), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, inventoryRows * 18 + 17);
		drawTexturedModalRect(guiLeft, guiTop + inventoryRows * 18 + 17, 0, 126, xSize, 96);
	}
}
