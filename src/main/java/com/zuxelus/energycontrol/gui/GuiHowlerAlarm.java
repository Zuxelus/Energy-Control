package com.zuxelus.energycontrol.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmListBox;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmSlider;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiHowlerAlarm extends GuiBase {
	private TileEntityHowlerAlarm alarm;
	private GuiHowlerAlarmSlider slider;
	private GuiHowlerAlarmListBox listBox;
	private boolean isBig;

	public GuiHowlerAlarm(TileEntityHowlerAlarm alarm, boolean isBig) {
		super("block.energycontrol.howler_alarm", 131, isBig ? 236 : 136, isBig ? 
			EnergyControl.MODID + ":textures/gui/gui_howler_alarm_big.png" :
			EnergyControl.MODID + ":textures/gui/gui_howler_alarm.png");
		this.alarm = alarm;
		this.isBig = isBig;
	}

	@Override
	public void init() {
		super.init();
		slider = new GuiHowlerAlarmSlider(guiLeft + 12, guiTop + 33, alarm);

		List<String> items = new ArrayList<String>(EnergyControl.INSTANCE.availableAlarms);
		items.retainAll(EnergyControl.INSTANCE.serverAllowedAlarms);

		listBox = new GuiHowlerAlarmListBox(guiLeft + 13, guiTop + 63, 105, isBig? 165 : 65, items, alarm);
		addRenderableWidget(slider);
		addRenderableWidget(listBox);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(PoseStack matrixStack, int mouseX, int mouseY) {
		drawTitle(matrixStack);
		font.draw(matrixStack, Component.translatable("msg.ec.HowlerAlarmSound"), 12, 53, 0x404040);
	}
}
