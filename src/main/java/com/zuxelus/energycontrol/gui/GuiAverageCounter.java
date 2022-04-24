package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerAverageCounter;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.zlib.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAverageCounter extends GuiContainerBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_energy_counter.png");

	private ContainerAverageCounter container;

	public GuiAverageCounter(ContainerAverageCounter container) {
		super(container, "tile.average_counter.name", TEXTURE);
		this.container = container;
	}

	private void initControls() {
		buttonList.clear();
		addButton(new GuiButton(1, guiLeft + 35, guiTop + 42, 30, 20, "1"));
		addButton(new GuiButton(2, guiLeft + 35 + 30, guiTop + 42, 30, 20, "3"));
		addButton(new GuiButton(3, guiLeft + 35 + 60, guiTop + 42, 30, 20, "5"));
		addButton(new GuiButton(4, guiLeft + 35 + 90, guiTop + 42, 30, 20, "10"));
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
		drawCenteredText(I18n.format("msg.ec.InfoPanelOutput", container.te.getClientAverage()), xSize, 22);
		drawCenteredText(I18n.format("msg.ec.AverageCounterPeriod", container.te.period), xSize, 32);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		initControls();
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		int event = 0;
		switch (guiButton.id) {
		case 1:
			event = 1;
			break;
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
		NetworkHelper.updateSeverTileEntity(container.te.getPos(), 1, event);
	}
}
