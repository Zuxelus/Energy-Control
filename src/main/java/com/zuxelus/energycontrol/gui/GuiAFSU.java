package com.zuxelus.energycontrol.gui;

import java.util.Arrays;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerAFSU;
import com.zuxelus.energycontrol.gui.controls.GuiButtonImage;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAFSU extends GuiContainerBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/gui/gui_afsu.png");

	private ContainerAFSU container;

	public GuiAFSU(ContainerAFSU container) {
		super(container, "tile.afsu.name", TEXTURE);
		this.container = container;
		ySize = 196;
	}

	@Override
	public void initGui() {
		super.initGui();
		addButton((GuiButton) new GuiButtonImage(1, guiLeft + 152, guiTop + 4, 20, 20, 176, 32, 20, TEXTURE));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (buttonList.get(0).isMouseOver())
			drawHoveringText(Arrays.asList(I18n.format("info.redstoneMode"),I18n.format("info.redstoneMode" + container.te.getRedstoneMode())), mouseX, mouseY);
		if (mouseX >= guiLeft + 51 && mouseY >= guiTop + 34 && mouseX < guiLeft + 84 && mouseY < guiTop + 51)
			drawCreativeTabHoveringText(String.format("%.2f M/%d M EU", container.te.getEnergy() / 1000000, container.te.getCapacity() / 1000000), mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(name, xSize, 6);
		drawLeftAlignedText(I18n.format("ic2.EUStorage.gui.info.armor"), 8, ySize - 126 + 3);
		drawLeftAlignedText(I18n.format("ic2.EUStorage.gui.info.level"), 51, 20);
		int e = (int) Math.min(container.te.getEnergy(), container.te.getCapacity());
		drawLeftAlignedText(" " + e, 92, 35);
		drawLeftAlignedText("/" + container.te.getCapacity(), 90, 45);
		String output = I18n.format("ic2.EUStorage.gui.info.output", container.te.getOutput());
		drawLeftAlignedText(output, 51, 60);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		int energyWidth = (int) (25.0D * container.te.getEnergy() / container.te.getCapacity());
		if (energyWidth > 0)
			drawTexturedModalRect(guiLeft + 55, guiTop + 34, 176, 14, energyWidth, 14);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 1) {
			byte mode = container.te.getRedstoneMode();
			mode++;
			if (mode > 6)
				mode = 0;
			NetworkHelper.updateSeverTileEntity(container.te.getPos(), 1, mode);
			container.te.setRedstoneMode(mode);
		}
	}
}
