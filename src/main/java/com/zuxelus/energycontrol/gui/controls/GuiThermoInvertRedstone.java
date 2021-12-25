package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiThermoInvertRedstone extends PressableWidget {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID + ":textures/gui/gui_thermal_monitor.png");

	TileEntityThermalMonitor thermo;
	private boolean checked;

	public GuiThermoInvertRedstone(int x, int y, TileEntityThermalMonitor thermo) {
		super(x, y, 0, 0, LiteralText.EMPTY);
		height = 15;
		width = 51;
		this.thermo = thermo;
		checked = thermo.getInvertRedstone();
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int delta = checked ? 15 : 0;
		drawTexture(matrixStack, x, y + 1, 199, delta, 51, 15);
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
	public void appendNarrations(NarrationMessageBuilder var1) {
		// TODO Auto-generated method stub
	}
}
