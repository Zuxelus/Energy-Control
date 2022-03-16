package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerRemoteThermalMonitor;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiThermoInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiRemoteThermalMonitor extends GuiContainerBase<ContainerRemoteThermalMonitor> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_remote_thermo.png");

	private TileEntityRemoteThermalMonitor te;
	private TextFieldWidget textboxHeat;

	public GuiRemoteThermalMonitor(ContainerRemoteThermalMonitor container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title, TEXTURE);
		this.te = container.te;
		imageWidth = 178;
		imageHeight = 166;
	}

	@Override
	public void init() {
		super.init();
		addButton(new CompactButton(0, leftPos + 40, topPos - 5 + 20, 22, 12, new StringTextComponent("-1"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(1, leftPos + 40, topPos - 5 + 31, 22, 12, new StringTextComponent("-10"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(2, leftPos + 5, topPos - 5 + 20, 36, 12, new StringTextComponent("-100"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(3, leftPos + 5, topPos - 5 + 31, 36, 12, new StringTextComponent("-1000"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(4, leftPos + 5, topPos - 5 + 42, 57, 12, new StringTextComponent("-10000"), (button) -> { actionPerformed(button); }));

		addButton(new CompactButton(5, leftPos + 115, topPos - 5 + 20, 22, 12, new StringTextComponent("+1"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(6, leftPos + 115, topPos - 5 + 31, 22, 12, new StringTextComponent("+10"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(7, leftPos + 136, topPos - 5 + 20, 36, 12, new StringTextComponent("+100"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(8, leftPos + 136, topPos - 5 + 31, 36, 12, new StringTextComponent("+1000"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(9, leftPos + 115, topPos - 5 + 42, 57, 12, new StringTextComponent("+10000"), (button) -> { actionPerformed(button); }));

		addButton(new GuiThermoInvertRedstone(leftPos + 63, topPos + 33, te));

		textboxHeat = addTextFieldWidget(63, 16, 51, 12, true, Integer.toString(te.getHeatLevel()));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		textboxHeat.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
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
		if (te.getLevel().isClientSide && te.getHeatLevel() != heat) {
			NetworkHelper.updateSeverTileEntity(te.getBlockPos(), 1, heat);
			te.setHeatLevel(heat);
		}
		textboxHeat.setValue(Integer.toString(heat));
	}

	@Override
	public void tick() {
		super.tick();
		textboxHeat.tick();
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, imageWidth, 6);
		drawLeftAlignedText(matrixStack, I18n.get("container.inventory"), 8, (imageHeight - 96) + 2);
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
