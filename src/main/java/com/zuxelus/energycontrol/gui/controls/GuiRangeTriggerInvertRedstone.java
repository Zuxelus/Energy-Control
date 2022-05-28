package com.zuxelus.energycontrol.gui.controls;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRangeTriggerInvertRedstone extends GuiButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_range_trigger.png");

	TileEntityRangeTrigger trigger;
	private boolean checked;

	public GuiRangeTriggerInvertRedstone(int id, int x, int y, TileEntityRangeTrigger trigger) {
		super(id, x, y, 0, 0, "");
		height = 15;
		width = 18;
		this.trigger = trigger;
		checked = trigger.getInvertRedstone();
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		mc.getTextureManager().bindTexture(TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(x, y + 1, 176, checked ? 15 : 0, 18, 15);
	}

	@Override
	public int getHoverState(boolean flag) {
		return 0;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (!super.mousePressed(mc, mouseX, mouseY))
			return false;

		checked = !checked;

		if (trigger.getWorld().isRemote && trigger.getInvertRedstone() != checked) {
			NetworkHelper.updateSeverTileEntity(trigger.getPos(), 2, checked ? 1 : 0);
			trigger.setInvertRedstone(checked);
		}
		return true;
	}
}
