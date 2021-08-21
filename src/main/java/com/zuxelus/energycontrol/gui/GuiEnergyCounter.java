package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerEnergyCounter;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.zlib.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnergyCounter extends GuiContainerBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_energy_counter.png");
	private ContainerEnergyCounter container;

	public GuiEnergyCounter(ContainerEnergyCounter container) {
		super(container, "tile.energy_counter.name", TEXTURE);
		this.container = container;
	}

	private void initControls() {
		buttonList.clear();
		addButton(new GuiButton(0, guiLeft + 35, guiTop + 42, 127, 20, I18n.format("msg.ec.Reset")));
	}

	@Override
	public void initGui() {
		super.initGui();
		initControls();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
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
			NetworkHelper.updateSeverTileEntity(container.te.getPos(), 1, 0);
	}
}
