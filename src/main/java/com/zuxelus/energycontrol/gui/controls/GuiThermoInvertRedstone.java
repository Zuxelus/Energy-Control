package com.zuxelus.energycontrol.gui.controls;

import com.zuxelus.energycontrol.EnergyControl;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class GuiThermoInvertRedstone extends AbstractButton {
	private static final Identifier TEXTURE = Identifier.parse(EnergyControl.MODID + ":textures/gui/gui_thermal_monitor.png");

	TileEntityThermalMonitor thermo;
	private boolean checked;

	public GuiThermoInvertRedstone(int x, int y, TileEntityThermalMonitor thermo) {
		super(x, y, 0, 0, CommonComponents.EMPTY);
		height = 15;
		width = 51;
		this.thermo = thermo;
		checked = thermo.getInvertRedstone();
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		int delta = checked ? 15 : 0;
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY() + 1, 199, delta, 51, 15, 256, 256);
	}

	@SuppressWarnings("resource")
	@Override
	public void onPress(InputWithModifiers input) {
		checked = !checked;
		if (thermo.getLevel().isClientSide() && thermo.getInvertRedstone() != checked) {
			NetworkHelper.updateSeverTileEntity(thermo.getBlockPos(), 2, checked ? (int) 1 : (int) 0);
			thermo.setInvertRedstone(checked);
		}
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput output) { }
}
