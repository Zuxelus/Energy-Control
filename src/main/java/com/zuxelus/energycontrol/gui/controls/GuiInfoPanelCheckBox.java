package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiInfoPanelCheckBox extends PressableWidget {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_info_panel.png");

	private TileEntityInfoPanel panel;
	private boolean checked;
	private PanelSetting setting;
	private int slot;

	public GuiInfoPanelCheckBox(int x, int y, PanelSetting setting, TileEntityInfoPanel panel, int slot, TextRenderer renderer) {
		super(x, y, renderer.getWidth(setting.title) + 8, renderer.fontHeight + 1, new LiteralText(setting.title));
		this.setting = setting;
		this.slot = slot;
		this.panel = panel;
		checked = (panel.getDisplaySettingsForCardInSlot(slot) & setting.displayBit) > 0;
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;
		MinecraftClient minecraft = MinecraftClient.getInstance();
		TextRenderer fontRenderer = minecraft.textRenderer;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int delta = checked ? 6 : 0;
		drawTexture(matrixStack, x, y + 1, 176, delta, 6, 6);
		fontRenderer.draw(matrixStack, getMessage(), x + 8, y, 0x404040);
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
	public void appendNarrations(NarrationMessageBuilder var1) {
		// TODO Auto-generated method stub
	}
}
