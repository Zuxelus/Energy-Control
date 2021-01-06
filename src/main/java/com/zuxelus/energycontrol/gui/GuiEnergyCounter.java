package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerEnergyCounter;
import com.zuxelus.zlib.gui.GuiContainerBase;
import com.zuxelus.zlib.network.NetworkHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiEnergyCounter extends GuiContainerBase {
	private ContainerEnergyCounter container;

	public GuiEnergyCounter(ContainerEnergyCounter container) {
		super(container, "tile.energy_counter.name", EnergyControl.MODID + ":textures/gui/gui_energy_counter.png");
		this.container = container;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButton(0, guiLeft + 35, guiTop + 42, 127, 20, I18n.format("msg.ec.Reset")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(name, xSize, 6, 0x404040);
		drawLeftAlignedText(I18n.format("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
		drawCenteredText(I18n.format("%s", container.te.counter), xSize, 22, 0x404040);
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if (guiButton.id == 0)
			NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, 1, 0);
	}
}
