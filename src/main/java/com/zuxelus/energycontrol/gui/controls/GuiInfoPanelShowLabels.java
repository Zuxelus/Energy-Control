package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiInfoPanelShowLabels extends AbstractButton {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_info_panel.png");

	private TileEntityInfoPanel panel;
	private boolean checked;

	public GuiInfoPanelShowLabels(int x, int y, TileEntityInfoPanel panel) {
		super(x, y, 0, 0, "");
		height = 9;
		width = 18;
		this.panel = panel;
		checked = panel.getShowLabels();
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int delta = checked ? 12 : 21;
		blit(x, y + 1, 176, delta, 18, 9);
	}

	@Override
	public void onPress() {
		// TODO Auto-generated method stub
		
	}

/*	@Override
	public int getHoverState(boolean flag) {
		return 0;
	}

	@Override
	public boolean mousePressed(Minecraft minecraft, int i, int j) {
		if (super.mousePressed(minecraft, i, j)) {
			checked = !checked;
			if (panel.getWorld().isRemote && panel.getShowLabels() != checked) {
				NetworkHelper.updateSeverTileEntity(panel.getPos(), 3, checked ? 1 : 0);
				panel.setShowLabels(checked);
			}
			return true;
		}
		return false;
	}*/
}
