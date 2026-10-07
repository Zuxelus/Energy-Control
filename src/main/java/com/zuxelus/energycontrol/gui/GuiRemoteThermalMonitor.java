package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.KeyEvent;
import com.zuxelus.energycontrol.containers.ContainerRemoteThermalMonitor;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiThermoInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class GuiRemoteThermalMonitor extends GuiContainerBase<ContainerRemoteThermalMonitor> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/gui/gui_remote_thermo.png");

	private TileEntityRemoteThermalMonitor te;
	private EditBox textboxHeat;

	public GuiRemoteThermalMonitor(ContainerRemoteThermalMonitor container, Inventory inventory, Component title) {
		super(container, inventory, title, TEXTURE, 178, 166);
		this.te = container.te;
	}

	@Override
	public void init() {
		super.init();
		addRenderableWidget(new CompactButton(0, leftPos + 40, topPos - 5 + 20, 22, 12, Component.literal("-1"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(1, leftPos + 40, topPos - 5 + 31, 22, 12, Component.literal("-10"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(2, leftPos + 5, topPos - 5 + 20, 36, 12, Component.literal("-100"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(3, leftPos + 5, topPos - 5 + 31, 36, 12, Component.literal("-1000"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(4, leftPos + 5, topPos - 5 + 42, 57, 12, Component.literal("-10000"), (button) -> { actionPerformed(button); }));

		addRenderableWidget(new CompactButton(5, leftPos + 115, topPos - 5 + 20, 22, 12, Component.literal("+1"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(6, leftPos + 115, topPos - 5 + 31, 22, 12, Component.literal("+10"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(7, leftPos + 136, topPos - 5 + 20, 36, 12, Component.literal("+100"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(8, leftPos + 136, topPos - 5 + 31, 36, 12, Component.literal("+1000"), (button) -> { actionPerformed(button); }));
		addRenderableWidget(new CompactButton(9, leftPos + 115, topPos - 5 + 42, 57, 12, Component.literal("+10000"), (button) -> { actionPerformed(button); }));

		addRenderableWidget(new GuiThermoInvertRedstone(leftPos + 63, topPos + 33, te));

		textboxHeat = addTextFieldWidget(63, 16, 51, 12, true, Integer.toString(te.getHeatLevel()));
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(context, mouseX, mouseY, partialTicks);
		textboxHeat.extractRenderState(context, mouseX, mouseY, partialTicks);
		extractTooltip(context, mouseX, mouseY);
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
		if (te.getLevel().isClientSide() && te.getHeatLevel() != heat) {
			NetworkHelper.updateSeverTileEntity(te.getBlockPos(), 1, heat);
			te.setHeatLevel(heat);
		}
		textboxHeat.setValue(Integer.toString(heat));
	}

	// while typing, keys must not close the screen (inventory key) or move hotbar items (number keys)
	@Override
	public boolean keyPressed(KeyEvent event) {
		int keyCode = event.key();
		if (textboxHeat.isFocused() && keyCode != InputConstants.KEY_ESCAPE) {
			if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) // Enter
				updateHeat(0);
			else
				textboxHeat.keyPressed(event);
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor context, int mouseX, int mouseY) {
		drawCenteredText(context, title, imageWidth, 6);
		drawLeftAlignedText(context, I18n.get("container.inventory"), 8, (imageHeight - 96) + 2);
	}

	@Override
	public void onClose() {
		updateHeat(0);
		super.onClose();
	}

	protected void actionPerformed(Button button) {
		if (((CompactButton) button).getId() >= 10)
			return;

		int delta = Integer.parseInt(button.getMessage().getString().replace("+", ""));
		updateHeat(delta);
	}
}
