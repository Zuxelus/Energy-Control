package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiRangeTriggerInvertRedstone extends AbstractButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_range_trigger.png");

	TileEntityRangeTrigger trigger;
	private boolean checked;

	public GuiRangeTriggerInvertRedstone(int x, int y, TileEntityRangeTrigger trigger) {
		super(x, y, 0, 0, "");
		height = 15;
		width = 18;
		this.trigger = trigger;
		checked = trigger.getInvertRedstone();
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		blit(x, y + 1, 176, checked ? 15 : 0, 18, 15);
	}

	@Override
	public void onPress() {
		checked = !checked;

		if (trigger.getWorld().isRemote && trigger.getInvertRedstone() != checked) {
			NetworkHelper.updateSeverTileEntity(trigger.getPos(), 2, checked ? 1 : 0);
			trigger.setInvertRedstone(checked);
		}
	}
}
