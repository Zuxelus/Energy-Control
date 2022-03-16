package com.zuxelus.energycontrol.gui;

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
		xSize = 178;
		ySize = 166;
	}

	@Override
	public void init() {
		super.init();
		addButton(new CompactButton(0, guiLeft + 40, guiTop - 5 + 20, 22, 12, "-1", (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(1, guiLeft + 40, guiTop - 5 + 31, 22, 12, "-10", (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(2, guiLeft + 5, guiTop - 5 + 20, 36, 12, "-100", (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(3, guiLeft + 5, guiTop - 5 + 31, 36, 12, "-1000", (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(4, guiLeft + 5, guiTop - 5 + 42, 57, 12, "-10000", (button) -> { actionPerformed(button); }));

		addButton(new CompactButton(5, guiLeft + 115, guiTop - 5 + 20, 22, 12, "+1", (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(6, guiLeft + 115, guiTop - 5 + 31, 22, 12, "+10", (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(7, guiLeft + 136, guiTop - 5 + 20, 36, 12, "+100", (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(8, guiLeft + 136, guiTop - 5 + 31, 36, 12, "+1000", (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(9, guiLeft + 115, guiTop - 5 + 42, 57, 12, "+10000", (button) -> { actionPerformed(button); }));

		addButton(new GuiThermoInvertRedstone(guiLeft + 63, guiTop + 33, te));

		textboxHeat = addTextFieldWidget(63, 16, 51, 12, true, Integer.toString(te.getHeatLevel()));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		textboxHeat.renderButton(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
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
		if (te.getWorld().isRemote && te.getHeatLevel() != heat) {
			NetworkHelper.updateSeverTileEntity(te.getPos(), 1, heat);
			te.setHeatLevel(heat);
		}
		textboxHeat.setText(Integer.toString(heat));
	}

	@Override
	public void tick() {
		super.tick();
		textboxHeat.tick();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(title.getString(), xSize, 6);
		drawLeftAlignedText(I18n.format("container.inventory"), 8, (ySize - 96) + 2);
	}

	@Override
	public void onClose() {
		updateHeat(0);
		super.onClose();
	}

	protected void actionPerformed(Button button) {
		if (((CompactButton) button).getId() >= 10)
			return;

		int delta = Integer.parseInt(button.getMessage().replace("+", ""));
		updateHeat(delta);
	}
}
