package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerEnergyCounter;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.zlib.gui.GuiContainerBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiEnergyCounter extends GuiContainerBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_energy_counter.png");
	private ContainerEnergyCounter container;

	public GuiEnergyCounter(ContainerEnergyCounter container) {
		super(container, "tile.energy_counter.name", TEXTURE);
		this.container = container;
	}

	@SuppressWarnings("unchecked")
	private void initControls() {
		buttonList.clear();
		buttonList.add(new GuiButton(0, guiLeft + 35, guiTop + 42, 127, 20, I18n.format("msg.ec.Reset")));
	}

	@Override
	public void initGui() {
		super.initGui();
		initControls();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(name, xSize, 6);
		drawLeftAlignedText(I18n.format("container.inventory"), 8, (ySize - 96) + 2);
		drawCenteredText(I18n.format("%s", container.te.counter), xSize, 22);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		initControls();
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if (guiButton.id == 0)
			NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, 1, 0);
	}
}
