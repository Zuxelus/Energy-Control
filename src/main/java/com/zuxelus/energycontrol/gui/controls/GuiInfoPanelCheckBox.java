package com.zuxelus.energycontrol.gui.controls;

import net.minecraft.text.Text;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiInfoPanelCheckBox extends PressableWidget {
	private static final Identifier TEXTURE = Identifier.of(EnergyControl.MODID, "textures/gui/gui_info_panel.png");

	private TileEntityInfoPanel panel;
	private boolean checked;
	private PanelSetting setting;
	private int slot;

	public GuiInfoPanelCheckBox(int x, int y, PanelSetting setting, TileEntityInfoPanel panel, int slot, TextRenderer renderer) {
		super(x, y, renderer.getWidth(setting.title) + 8, renderer.fontHeight + 1, Text.literal(setting.title));
		this.setting = setting;
		this.slot = slot;
		this.panel = panel;
		checked = (panel.getDisplaySettingsForCardInSlot(slot) & setting.displayBit) > 0;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;
		MinecraftClient minecraft = MinecraftClient.getInstance();
		TextRenderer fontRenderer = minecraft.textRenderer;
		int delta = checked ? 6 : 0;
		context.drawTexture(TEXTURE, getX(), getY() + 1, 176, delta, 6, 6);
		context.drawText(fontRenderer, getMessage(), getX() + 8, getY(), 0x404040, false);
	}

	@Override
	public void onPress() {
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
		NbtCompound tag = new NbtCompound();
		tag.putInt("type", 1);
		tag.putInt("slot", slot);
		tag.putInt("value", value);
		NetworkHelper.updateSeverTileEntity(panel.getPos(), tag);
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder var1) {
		// TODO Auto-generated method stub
	}
}
