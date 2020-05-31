package com.zuxelus.energycontrol.gui;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiThermoInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityThermo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiThermalMonitor extends GuiBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_thermal_monitor.png");

	private TileEntityThermo thermo;
	private GuiTextField textboxHeat = null;

	public GuiThermalMonitor(TileEntityThermo thermo) {
		super("tile.thermal_monitor.name", 191, 64);
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
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
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
		if (thermo.getWorldObj().isRemote && thermo.getHeatLevel() != heat) {
			NetworkHelper.updateSeverTileEntity(thermo.xCoord, thermo.yCoord, thermo.zCoord, 1, heat);
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
			mc.thePlayer.closeScreen();
		else if(typedChar == 13) // Enter
			updateHeat(0);
		else if(textboxHeat != null && textboxHeat.isFocused() && (Character.isDigit(typedChar) || typedChar == 0 || typedChar == 8))
			textboxHeat.textboxKeyTyped(typedChar, keyCode);
	}
}
