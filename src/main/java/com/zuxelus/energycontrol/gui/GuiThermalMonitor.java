package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiThermoInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiThermalMonitor extends GuiBase {
	private TileEntityThermalMonitor thermo;
	private EditBox textboxHeat;

	public GuiThermalMonitor(TileEntityThermalMonitor thermo) {
		super("block.energycontrol.thermal_monitor", 191, 64, EnergyControl.MODID + ":textures/gui/gui_thermal_monitor.png");
		this.thermo = thermo;
	}

	@Override
	public void init() {
		super.init();
		addRenderableWidget(new CompactButton(0, guiLeft + 47, guiTop + 20, 22, 12, Component.literal("-1"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(1, guiLeft + 47, guiTop + 31, 22, 12, Component.literal("-10"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(2, guiLeft + 12, guiTop + 20, 36, 12, Component.literal("-100"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(3, guiLeft + 12, guiTop + 31, 36, 12, Component.literal("-1000"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(4, guiLeft + 12, guiTop + 42, 57, 12, Component.literal("-10000"), (button) -> { actionPerformed(button); }));

		addRenderableWidget(new CompactButton(5, guiLeft + 122, guiTop + 20, 22, 12, Component.literal("+1"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(6, guiLeft + 122, guiTop + 31, 22, 12, Component.literal("+10"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(7, guiLeft + 143, guiTop + 20, 36, 12, Component.literal("+100"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(8, guiLeft + 143, guiTop + 31, 36, 12, Component.literal("+1000"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(9, guiLeft + 122, guiTop + 42, 57, 12, Component.literal("+10000"), (button) -> { actionPerformed(button); }));

		addRenderableWidget(new GuiThermoInvertRedstone(guiLeft + 70, guiTop + 38, thermo));

		textboxHeat = addTextFieldWidget(70, 21, 51, 12, true, Integer.toString(thermo.getHeatLevel()));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		textboxHeat.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(PoseStack matrixStack, int mouseX, int mouseY) {
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
			String value = textboxHeat.getValue();
			if (!"".equals(value))
				heat = Integer.parseInt(value);
		} catch (NumberFormatException e) { }
		heat += delta;
		if (heat < 0)
			heat = 0;
		if (heat >= 1000000)
			heat = 1000000;
		if (thermo.getLevel().isClientSide && thermo.getHeatLevel() != heat) {
			NetworkHelper.updateSeverTileEntity(thermo.getBlockPos(), 1, heat);
			thermo.setHeatLevel(heat);
		}
		textboxHeat.setValue(Integer.toString(heat));
	}

	protected void actionPerformed(Button button) {
		if (((CompactButton) button).getId() >= 10)
			return;

		int delta = Integer.parseInt(button.getMessage().getString().replace("+", ""));
		updateHeat(delta);
	}
}
