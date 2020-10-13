package com.zuxelus.energycontrol.screen.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;
import com.zuxelus.energycontrol.network.NetworkHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiInfoPanelCheckBox extends AbstractPressableButtonWidget {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_info_panel.png");

	private InfoPanelBlockEntity panel;
	private boolean checked;
	private PanelSetting setting;
	private int slot;

	public GuiInfoPanelCheckBox(int x, int y, PanelSetting setting, InfoPanelBlockEntity panel, int slot, TextRenderer renderer) {
		super(x, y, renderer.getWidth(setting.title) + 8, renderer.fontHeight + 1, new LiteralText(setting.title));
		this.setting = setting;
		this.slot = slot;
		this.panel = panel;
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		checked = (panel.getDisplaySettingsForCardInSlot(slot) & setting.displayBit) > 0;
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int deltauv = checked ? 6 : 0;
		drawTexture(matrices, x, y + 1, 176, deltauv, 6, 6);
		mc.textRenderer.draw(matrices, getMessage(), x + 8, y, 0x404040);
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
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", 1);
		tag.putInt("slot", slot);
		tag.putInt("value", value);
		NetworkHelper.updateSeverTileEntity(panel.getPos(), tag);
	}
}
