package com.zuxelus.energycontrol.gui.controls;

import net.minecraft.screen.ScreenTexts;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;


@Environment(EnvType.CLIENT)
public class GuiRangeTriggerInvertRedstone extends PressableWidget {
	private static final Identifier TEXTURE = Identifier.of(EnergyControl.MODID, "textures/gui/gui_range_trigger.png");

	TileEntityRangeTrigger trigger;
	private boolean checked;

	public GuiRangeTriggerInvertRedstone(int x, int y, TileEntityRangeTrigger trigger) {
		super(x, y, 0, 0, ScreenTexts.EMPTY);
		height = 15;
		width = 18;
		this.trigger = trigger;
		checked = trigger.getInvertRedstone();
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		context.drawTexture(TEXTURE, getX(), getY() + 1, 176, checked ? 15 : 0, 18, 15);
	}

	@Override
	public void onPress() {
		checked = !checked;

		if (trigger.getWorld().isClient && trigger.getInvertRedstone() != checked) {
			NetworkHelper.updateSeverTileEntity(trigger.getPos(), 2, checked ? 1 : 0);
			trigger.setInvertRedstone(checked);
		}
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder var1) {
		// TODO Auto-generated method stub
	}
}
