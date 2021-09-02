package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiInfoPanelCheckBox extends AbstractButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_info_panel.png");

	private TileEntityInfoPanel panel;
	private boolean checked;
	private PanelSetting setting;
	private int slot;

	public GuiInfoPanelCheckBox(int x, int y, PanelSetting setting, TileEntityInfoPanel panel, int slot, FontRenderer renderer) {
		super(x, y, renderer.getStringWidth(setting.title) + 8, renderer.FONT_HEIGHT + 1, new StringTextComponent(setting.title));
		this.setting = setting;
		this.slot = slot;
		this.panel = panel;
		checked = (panel.getDisplaySettingsForCardInSlot(slot) & setting.displayBit) > 0;
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontRenderer = minecraft.fontRenderer;
		minecraft.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int delta = checked ? 6 : 0;
		blit(matrixStack, x, y + 1, 176, delta, 6, 6);
		fontRenderer.func_243248_b(matrixStack, getMessage(), x + 8, y, 0x404040);
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
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("type", 1);
		tag.putInt("slot", slot);
		tag.putInt("value", value);
		NetworkHelper.updateSeverTileEntity(panel.getPos(), tag);
	}
}
