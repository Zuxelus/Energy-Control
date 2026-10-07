package com.zuxelus.energycontrol.gui.controls;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class GuiInfoPanelCheckBox extends AbstractButton {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/gui/gui_info_panel.png");

	private TileEntityInfoPanel panel;
	private boolean checked;
	private PanelSetting setting;
	private int slot;

	public GuiInfoPanelCheckBox(int x, int y, PanelSetting setting, TileEntityInfoPanel panel, int slot, Font renderer) {
		super(x, y, renderer.width(setting.title) + 8, renderer.lineHeight + 1, Component.literal(setting.title));
		this.setting = setting;
		this.slot = slot;
		this.panel = panel;
		checked = (panel.getDisplaySettingsForCardInSlot(slot) & setting.displayBit) > 0;
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;
		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		int delta = checked ? 6 : 0;
		matrixStack.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY() + 1, 176, delta, 6, 6, 256, 256);
		matrixStack.text(fontRenderer, getMessage(), getX() + 8, getY(), ARGB.opaque(0x404040), false);
	}

	@Override
	public void onPress(InputWithModifiers input) {
		checked = !checked;
		int value;
		if (checked)
			value = panel.getDisplaySettingsForCardInSlot(slot) | setting.displayBit;
		else
			value = panel.getDisplaySettingsForCardInSlot(slot) & (~setting.displayBit);
		UpdateServerSettings(value);
		panel.setDisplaySettings(slot, value);
	}

	private void UpdateServerSettings(int value) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", 1);
		tag.putInt("slot", slot);
		tag.putInt("value", value);
		NetworkHelper.updateSeverTileEntity(panel.getBlockPos(), tag);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) { }
}
