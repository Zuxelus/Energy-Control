package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiThermoInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityThermo;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiThermalMonitor extends GuiBase {
	private TileEntityThermo thermo;
	private GuiTextField textboxHeat = null;

	public GuiThermalMonitor(TileEntityThermo thermo) {
		super("tile.thermal_monitor.name", 191, 64, EnergyControl.MODID + ":textures/gui/gui_thermal_monitor.png");
		this.thermo = thermo;
	}

	@Override
	public void initGui() {
		super.initGui();
		addButton(new CompactButton(0, guiLeft + 47, guiTop + 20, 22, 12, "-1"));
		addButton(new CompactButton(1, guiLeft + 47, guiTop + 31, 22, 12, "-10"));
		addButton(new CompactButton(2, guiLeft + 12, guiTop + 20, 36, 12, "-100"));
		addButton(new CompactButton(3, guiLeft + 12, guiTop + 31, 36, 12, "-1000"));
		addButton(new CompactButton(4, guiLeft + 12, guiTop + 42, 57, 12, "-10000"));

		addButton(new CompactButton(5, guiLeft + 122, guiTop + 20, 22, 12, "+1"));
		addButton(new CompactButton(6, guiLeft + 122, guiTop + 31, 22, 12, "+10"));
		addButton(new CompactButton(7, guiLeft + 143, guiTop + 20, 36, 12, "+100"));
		addButton(new CompactButton(8, guiLeft + 143, guiTop + 31, 36, 12, "+1000"));
		addButton(new CompactButton(9, guiLeft + 122, guiTop + 42, 57, 12, "+10000"));

		addButton(new GuiThermoInvertRedstone(10, guiLeft + 70, guiTop + 38, thermo));

		textboxHeat = new GuiTextField(11, fontRenderer, 70, 21, 51, 12);
		textboxHeat.setFocused(true);
		textboxHeat.setText(Integer.toString(thermo.getHeatLevel()));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
		
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
		} catch (NumberFormatException e) {	}
		heat += delta;
		if (heat < 0)
			heat = 0;
		if (heat >= 1000000)
			heat = 1000000;
		if (thermo.getWorld().isRemote && thermo.getHeatLevel() != heat) {
			NetworkHelper.updateSeverTileEntity(thermo.getPos(), 1, heat);
			thermo.setHeatLevel(heat);
		}
		textboxHeat.setText(new Integer(heat).toString());
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
			mc.player.closeScreen();
		else if(typedChar == 13) // Enter
			updateHeat(0);
		else if(textboxHeat != null && textboxHeat.isFocused() && (Character.isDigit(typedChar) || typedChar == 0 || typedChar == 8))
			textboxHeat.textboxKeyTyped(typedChar, keyCode);
	}
}
