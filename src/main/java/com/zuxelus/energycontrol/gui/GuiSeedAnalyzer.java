package com.zuxelus.energycontrol.gui;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerSeedAnalyzer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiSeedAnalyzer extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_seed_analyzer.png");

	private String name;
	private ContainerSeedAnalyzer container;

	public GuiSeedAnalyzer(ContainerSeedAnalyzer container) {
		super(container);
		this.container = container;
		name = I18n.format("tile.seed_analyzer.name");
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(name, (xSize - fontRendererObj.getStringWidth(name)) / 2, 6, 0x404040);
		fontRendererObj.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);

		int energyHeight = container.te.getEnergyFactor();
		if (energyHeight > 0)
			drawTexturedModalRect(left + 56, top + 36 + 14 - energyHeight, 176, 14 - energyHeight, 14, energyHeight);
		int productionWidth = container.te.getProductionFactor();
		if (energyHeight > 0)
			drawTexturedModalRect(left + 79, top + 35, 176, 15, productionWidth, 17);
	}
}