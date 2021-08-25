package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiThermoInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiThermalMonitor extends GuiBase {
	private TileEntityThermalMonitor thermo;
	private TextFieldWidget textboxHeat = null;

	public GuiThermalMonitor(TileEntityThermalMonitor thermo) {
		super("block.energycontrol.thermal_monitor", 191, 64, EnergyControl.MODID + ":textures/gui/gui_thermal_monitor.png");
		this.thermo = thermo;
	}

	@Override
	public void init() {
		super.init();
		addButton(new CompactButton(guiLeft + 47, guiTop + 20, 22, 12, new StringTextComponent("-1"), (button) -> { actionPerformed(button, 0); }));
		addButton(new CompactButton(guiLeft + 47, guiTop + 31, 22, 12, new StringTextComponent("-10"), (button) -> { actionPerformed(button, 1); }));
		addButton(new CompactButton(guiLeft + 12, guiTop + 20, 36, 12, new StringTextComponent("-100"), (button) -> { actionPerformed(button, 2); }));
		addButton(new CompactButton(guiLeft + 12, guiTop + 31, 36, 12, new StringTextComponent("-1000"), (button) -> { actionPerformed(button, 3); }));
		addButton(new CompactButton(guiLeft + 12, guiTop + 42, 57, 12, new StringTextComponent("-10000"), (button) -> { actionPerformed(button, 4); }));

		addButton(new CompactButton(guiLeft + 122, guiTop + 20, 22, 12, new StringTextComponent("+1"), (button) -> { actionPerformed(button, 5); }));
		addButton(new CompactButton(guiLeft + 122, guiTop + 31, 22, 12, new StringTextComponent("+10"), (button) -> { actionPerformed(button, 6); }));
		addButton(new CompactButton(guiLeft + 143, guiTop + 20, 36, 12, new StringTextComponent("+100"), (button) -> { actionPerformed(button, 7); }));
		addButton(new CompactButton(guiLeft + 143, guiTop + 31, 36, 12, new StringTextComponent("+1000"), (button) -> { actionPerformed(button, 8); }));
		addButton(new CompactButton(guiLeft + 122, guiTop + 42, 57, 12, new StringTextComponent("+10000"), (button) -> { actionPerformed(button, 9); }));

		addButton(new GuiThermoInvertRedstone(guiLeft + 70, guiTop + 38, thermo));

		textboxHeat = addTextFieldWidget(70, 21, 51, 12, true, Integer.toString(thermo.getHeatLevel()));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		textboxHeat.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawTitle(matrixStack);
	}

	@Override
	public void tick() {
		super.tick();
		textboxHeat.tick();
	}

	@Override
	public void onClose() {
		updateHeat(0);
		super.onClose();
	}

	@SuppressWarnings("resource")
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

	protected void actionPerformed(Button button, int id) {
		if (id >= 10)
			return;

		int delta = Integer.parseInt(button.getMessage().getString().replace("+", ""));
		updateHeat(delta);
	}

	/*@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if(keyCode == 1) // Esc button
			mc.player.closeScreen();
		else if(typedChar == 13) // Enter
			updateHeat(0);
		else if(textboxHeat != null && textboxHeat.isFocused() && (Character.isDigit(typedChar) || typedChar == 0 || typedChar == 8))
			textboxHeat.textboxKeyTyped(typedChar, keyCode);
	}*/
}
