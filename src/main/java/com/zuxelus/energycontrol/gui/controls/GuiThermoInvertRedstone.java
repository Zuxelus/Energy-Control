package com.zuxelus.energycontrol.gui.controls;

import net.minecraft.screen.ScreenTexts;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiThermoInvertRedstone extends PressableWidget {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID + ":textures/gui/gui_thermal_monitor.png");

	TileEntityThermalMonitor thermo;
	private boolean checked;

	public GuiThermoInvertRedstone(int x, int y, TileEntityThermalMonitor thermo) {
		super(x, y, 0, 0, ScreenTexts.EMPTY);
		height = 15;
		width = 51;
		this.thermo = thermo;
		checked = thermo.getInvertRedstone();
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		int delta = checked ? 15 : 0;
		context.drawTexture(TEXTURE, getX(), getY() + 1, 199, delta, 51, 15);
	}

	@SuppressWarnings("resource")
	@Override
	public void onPress() {
		checked = !checked;
		if (thermo.getWorld().isClient && thermo.getInvertRedstone() != checked) {
			NetworkHelper.updateSeverTileEntity(thermo.getPos(), 2, checked ? (int) 1 : (int) 0);
			thermo.setInvertRedstone(checked);
		}
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder var1) {
		// TODO Auto-generated method stub
	}
}
