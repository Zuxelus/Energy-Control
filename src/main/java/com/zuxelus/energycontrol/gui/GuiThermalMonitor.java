package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiThermoInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;
import com.zuxelus.zlib.gui.GuiBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

@SideOnly(Side.CLIENT)
public class GuiThermalMonitor extends GuiBase {
	private TileEntityThermalMonitor thermo;
	private GuiTextField textboxHeat;

	public GuiThermalMonitor(TileEntityThermalMonitor thermo) {
		super("tile.thermal_monitor.name", 191, 64, EnergyControl.MODID + ":textures/gui/gui_thermal_monitor.png");
		this.thermo = thermo;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new CompactButton(0, guiLeft + 47, guiTop + 20, 22, 12, "-1"));
		buttonList.add(new CompactButton(1, guiLeft + 47, guiTop + 31, 22, 12, "-10"));
		buttonList.add(new CompactButton(2, guiLeft + 12, guiTop + 20, 36, 12, "-100"));
		buttonList.add(new CompactButton(3, guiLeft + 12, guiTop + 31, 36, 12, "-1000"));
		buttonList.add(new CompactButton(4, guiLeft + 12, guiTop + 42, 57, 12, "-10000"));

		buttonList.add(new CompactButton(5, guiLeft + 122, guiTop + 20, 22, 12, "+1"));
		buttonList.add(new CompactButton(6, guiLeft + 122, guiTop + 31, 22, 12, "+10"));
		buttonList.add(new CompactButton(7, guiLeft + 143, guiTop + 20, 36, 12, "+100"));
		buttonList.add(new CompactButton(8, guiLeft + 143, guiTop + 31, 36, 12, "+1000"));
		buttonList.add(new CompactButton(9, guiLeft + 122, guiTop + 42, 57, 12, "+10000"));

		buttonList.add(new GuiThermoInvertRedstone(10, guiLeft + 70, guiTop + 38, thermo));

		textboxHeat = new GuiTextField(fontRendererObj, 70, 21, 51, 12);
		textboxHeat.setFocused(true);
		textboxHeat.setText(Integer.toString(thermo.getHeatLevel()));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRendererObj.drawString(name, (xSize - fontRendererObj.getStringWidth(name)) / 2, 6, 0x404040);

		if (textboxHeat != null)
			textboxHeat.drawTextBox();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (textboxHeat != null)
			textboxHeat.updateCursorCounter();
	}

	@Override
	public void onGuiClosed() {
		updateHeat(0);
		super.onGuiClosed();
	}

	private void updateHeat(int delta) {
		if (textboxHeat == null)
			return;
		int heat = 0;
		try {
			String value = textboxHeat.getText();
			if (!"".equals(value))
				heat = Integer.parseInt(value);
		} catch (NumberFormatException e) { }
		heat += delta;
		if (heat < 0)
			heat = 0;
		if (heat >= 1000000)
			heat = 1000000;
		if (thermo.getWorldObj().isRemote && thermo.getHeatLevel() != heat) {
			NetworkHelper.updateSeverTileEntity(thermo.xCoord, thermo.yCoord, thermo.zCoord, 1, heat);
			thermo.setHeatLevel(heat);
		}
		textboxHeat.setText(Integer.toString(heat));
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
		if(keyCode == 1) // Esc button
			mc.thePlayer.closeScreen();
		else if(typedChar == 13) // Enter
			updateHeat(0);
		else if(textboxHeat != null && textboxHeat.isFocused() && (Character.isDigit(typedChar) || typedChar == 0 || typedChar == 8))
			textboxHeat.textboxKeyTyped(typedChar, keyCode);
	}
}
