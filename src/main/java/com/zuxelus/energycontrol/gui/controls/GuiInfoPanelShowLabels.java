package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;
import com.zuxelus.energycontrol.network.NetworkHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiInfoPanelShowLabels extends AbstractPressableButtonWidget {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_info_panel.png");

	private InfoPanelBlockEntity panel;
	private boolean checked;

	public GuiInfoPanelShowLabels(int x, int y, InfoPanelBlockEntity panel) {
		super(x, y, 18, 9, "");
		this.panel = panel;
		this.checked = panel.getShowLabels();
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		int deltauv = checked ? 12 : 21;
		blit(x, y + 1, 176, deltauv, 18, 9);
	}

	@Override
	public void onPress() {
		checked = !checked;
		if (panel.getWorld().isClient && panel.getShowLabels() != checked) {
			NetworkHelper.updateSeverTileEntity(panel.getPos(), 3, checked ? 1 : 0);
			panel.setShowLabels(checked);
		}
	}
}
