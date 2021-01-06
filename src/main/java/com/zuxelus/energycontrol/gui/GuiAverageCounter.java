package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerAverageCounter;
import com.zuxelus.energycontrol.utils.StringUtils;
import com.zuxelus.zlib.gui.GuiContainerBase;
import com.zuxelus.zlib.network.NetworkHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiAverageCounter extends GuiContainerBase {
	private ContainerAverageCounter container;

	public GuiAverageCounter(ContainerAverageCounter container) {
		super(container, "tile.average_counter.name", EnergyControl.MODID + ":textures/gui/gui_energy_counter.png");
		this.container = container;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButton(1, guiLeft + 35, guiTop + 42, 30, 20, "1"));
		buttonList.add(new GuiButton(2, guiLeft + 35 + 30, guiTop + 42, 30, 20, "3"));
		buttonList.add(new GuiButton(3, guiLeft + 35 + 60, guiTop + 42, 30, 20, "5"));
		buttonList.add(new GuiButton(4, guiLeft + 35 + 90, guiTop + 42, 30, 20, "10"));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(name, xSize, 6, 0x404040);
		drawLeftAlignedText(I18n.format("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
		drawCenteredText(I18n.format("msg.ec.InfoPanelOutput", container.te.getClientAverage()), xSize, 22, 0x404040);
		drawCenteredText(I18n.format("msg.ec.AverageCounterPeriod", container.te.period), xSize, 32, 0x404040);
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		int event = 1;
		switch (guiButton.id) {
		case 2:
			event = 3;
			break;
		case 3:
			event = 5;
			break;
		case 4:
			event = 10;
			break;
		}
		NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, 1, event);
	}
}