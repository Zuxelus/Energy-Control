package com.zuxelus.energycontrol.gui.controls;

import com.zuxelus.energycontrol.EnergyControl;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;


@Environment(EnvType.CLIENT)
public class GuiRangeTriggerInvertRedstone extends AbstractButton {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/gui/gui_range_trigger.png");

	TileEntityRangeTrigger trigger;
	private boolean checked;

	public GuiRangeTriggerInvertRedstone(int x, int y, TileEntityRangeTrigger trigger) {
		super(x, y, 0, 0, CommonComponents.EMPTY);
		height = 15;
		width = 18;
		this.trigger = trigger;
		checked = trigger.getInvertRedstone();
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY() + 1, 176, checked ? 15 : 0, 18, 15, 256, 256);
	}

	@Override
	public void onPress(InputWithModifiers input) {
		checked = !checked;

		if (trigger.getLevel().isClientSide() && trigger.getInvertRedstone() != checked) {
			NetworkHelper.updateSeverTileEntity(trigger.getBlockPos(), 2, checked ? 1 : 0);
			trigger.setInvertRedstone(checked);
		}
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput var1) {
		// TODO Auto-generated method stub
	}
}
