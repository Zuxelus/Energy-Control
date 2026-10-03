package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerRemoteThermalMonitor;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiThermoInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiRemoteThermalMonitor extends GuiContainerBase<ContainerRemoteThermalMonitor> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_remote_thermo.png");

	private TileEntityRemoteThermalMonitor te;
	private TextFieldWidget textboxHeat;

	public GuiRemoteThermalMonitor(ContainerRemoteThermalMonitor container, PlayerInventory inventory, Text title) {
		super(container, inventory, title, TEXTURE);
		this.te = container.te;
		backgroundWidth = 178;
		backgroundHeight = 166;
	}

	@Override
	public void init() {
		super.init();
		addDrawableChild(new CompactButton(0, x + 40, y - 5 + 20, 22, 12, new LiteralText("-1"), (button) -> { actionPerformed(button); }));
		addDrawableChild(new CompactButton(1, x + 40, y - 5 + 31, 22, 12, new LiteralText("-10"), (button) -> { actionPerformed(button); }));
		addDrawableChild(new CompactButton(2, x + 5, y - 5 + 20, 36, 12, new LiteralText("-100"), (button) -> { actionPerformed(button); }));
		addDrawableChild(new CompactButton(3, x + 5, y - 5 + 31, 36, 12, new LiteralText("-1000"), (button) -> { actionPerformed(button); }));
		addDrawableChild(new CompactButton(4, x + 5, y - 5 + 42, 57, 12, new LiteralText("-10000"), (button) -> { actionPerformed(button); }));

		addDrawableChild(new CompactButton(5, x + 115, y - 5 + 20, 22, 12, new LiteralText("+1"), (button) -> { actionPerformed(button); }));
		addDrawableChild(new CompactButton(6, x + 115, y - 5 + 31, 22, 12, new LiteralText("+10"), (button) -> { actionPerformed(button); }));
		addDrawableChild(new CompactButton(7, x + 136, y - 5 + 20, 36, 12, new LiteralText("+100"), (button) -> { actionPerformed(button); }));
		addDrawableChild(new CompactButton(8, x + 136, y - 5 + 31, 36, 12, new LiteralText("+1000"), (button) -> { actionPerformed(button); }));
		addDrawableChild(new CompactButton(9, x + 115, y - 5 + 42, 57, 12, new LiteralText("+10000"), (button) -> { actionPerformed(button); }));

		addDrawableChild(new GuiThermoInvertRedstone(x + 63, y + 33, te));

		textboxHeat = addTextFieldWidget(63, 16, 51, 12, true, Integer.toString(te.getHeatLevel()));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		textboxHeat.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		drawMouseoverTooltip(matrixStack, mouseX, mouseY);
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
		} catch (NumberFormatException e) { }
		heat += delta;
		if (heat < 0)
			heat = 0;
		if (heat >= 1000000)
			heat = 1000000;
		if (te.getWorld().isClient && te.getHeatLevel() != heat) {
			NetworkHelper.updateSeverTileEntity(te.getPos(), 1, heat);
			te.setHeatLevel(heat);
		}
		textboxHeat.setText(Integer.toString(heat));
	}

	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		textboxHeat.tick();
	}

	// while typing, keys must not close the screen (inventory key) or move hotbar items (number keys)
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (textboxHeat.isFocused() && keyCode != 256) {
			if (keyCode == 257 || keyCode == 335) // Enter
				updateHeat(0);
			else
				textboxHeat.keyPressed(keyCode, scanCode, modifiers);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, backgroundWidth, 6);
		drawLeftAlignedText(matrixStack, I18n.translate("container.inventory"), 8, (backgroundHeight - 96) + 2);
	}

	@Override
	public void close() {
		updateHeat(0);
		super.close();
	}

	protected void actionPerformed(ButtonWidget button) {
		if (((CompactButton) button).getId() >= 10)
			return;

		int delta = Integer.parseInt(button.getMessage().getString().replace("+", ""));
		updateHeat(delta);
	}
}
