package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerRemoteThermalMonitor;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiThermoInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.zlib.gui.GuiContainerBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiRemoteThermo extends GuiContainerBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_remote_thermo.png");

	private ContainerRemoteThermalMonitor container;
	private GuiTextField textboxHeat;

	public GuiRemoteThermo(ContainerRemoteThermalMonitor container) {
		super(container, "tile.remote_thermo.name", TEXTURE);
		this.container = container;
		xSize = 178;
		ySize = 166;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		buttonList.add(new CompactButton(0, guiLeft + 40, guiTop - 5 + 20, 22, 12, "-1"));
		buttonList.add(new CompactButton(1, guiLeft + 40, guiTop - 5 + 31, 22, 12, "-10"));
		buttonList.add(new CompactButton(2, guiLeft + 5, guiTop - 5 + 20, 36, 12, "-100"));
		buttonList.add(new CompactButton(3, guiLeft + 5, guiTop - 5 + 31, 36, 12, "-1000"));
		buttonList.add(new CompactButton(4, guiLeft + 5, guiTop - 5 + 42, 57, 12, "-10000"));

		buttonList.add(new CompactButton(5, guiLeft + 115, guiTop - 5 + 20, 22, 12, "+1"));
		buttonList.add(new CompactButton(6, guiLeft + 115, guiTop - 5 + 31, 22, 12, "+10"));
		buttonList.add(new CompactButton(7, guiLeft + 136, guiTop - 5 + 20, 36, 12, "+100"));
		buttonList.add(new CompactButton(8, guiLeft + 136, guiTop - 5 + 31, 36, 12, "+1000"));
		buttonList.add(new CompactButton(9, guiLeft + 115, guiTop - 5 + 42, 57, 12, "+10000"));

		buttonList.add(new GuiThermoInvertRedstone(10, guiLeft + 63, guiTop + 33, container.te));

		textboxHeat = new GuiTextField(fontRendererObj, 63, 16, 51, 12);
		textboxHeat.setFocused(true);
		textboxHeat.setText(Integer.toString(container.te.getHeatLevel()));
	}

	private void updateHeat(int delta) {
		if (textboxHeat != null) {
			int heat = 0;
			try {
				String value = textboxHeat.getText();
				if (!"".equals(value))
					heat = Integer.parseInt(value);
			} catch (NumberFormatException e) { }
			heat += delta;
			if (heat <= 0)
				heat = 0;
			if (heat >= 1000000)
				heat = 1000000;
			if (container.te.getWorldObj().isRemote && container.te.getHeatLevel() != heat) {
				NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, 1, heat);
				container.te.setHeatLevel(heat);
			}
			textboxHeat.setText(Integer.toString(heat));
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (textboxHeat != null)
			textboxHeat.updateCursorCounter();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(name, xSize, 6);
		drawLeftAlignedText(I18n.format("container.inventory"), 8, (ySize - 96) + 2);
		if (textboxHeat != null)
			textboxHeat.drawTextBox();
	}

	@Override
	public void onGuiClosed() {
		updateHeat(0);
		super.onGuiClosed();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id >= 10)
			return;

		int delta = Integer.parseInt(button.displayString.replace("+", ""));
		updateHeat(delta);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (keyCode == 1) // Esc
			mc.thePlayer.closeScreen();
		else if (typedChar == 13) // Enter
			updateHeat(0);
		else if (textboxHeat != null && textboxHeat.isFocused() && (Character.isDigit(typedChar) || typedChar == 0 || typedChar == 8))
			textboxHeat.textboxKeyTyped(typedChar, keyCode);
	}
}
