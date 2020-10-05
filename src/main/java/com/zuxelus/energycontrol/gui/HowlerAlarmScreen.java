package com.zuxelus.energycontrol.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blockentities.HowlerAlarmBlockEntity;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmListBox;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmSlider;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class HowlerAlarmScreen extends ScreenBase {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_howler_alarm.png");
	private HowlerAlarmBlockEntity alarm;
	private GuiHowlerAlarmSlider slider;
	private GuiHowlerAlarmListBox listBox;

	public HowlerAlarmScreen(HowlerAlarmBlockEntity alarm) {
		super(new TranslatableText("block.energycontrol.howler_alarm"), 131, 136);
		this.alarm = alarm;
	}

	@Override
	public void init() {
		super.init();
		slider = new GuiHowlerAlarmSlider(3, x + 12, y + 33, alarm);

		List<String> items = new ArrayList<String>(EnergyControl.INSTANCE.availableAlarms);
		items.retainAll(EnergyControl.INSTANCE.serverAllowedAlarms);

		listBox = new GuiHowlerAlarmListBox(4, x + 13, y + 63, 105, 65, items, alarm);
		addButton(slider);
		addButton(listBox);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		
		minecraft.textRenderer.draw(getTitle().asFormattedString(), (containerWidth - minecraft.textRenderer.getStringWidth(getTitle().asFormattedString())) / 2, 6, 0x404040);
		minecraft.textRenderer.draw(I18n.translate("msg.ec.HowlerAlarmSound"), 12, 53, 0x404040);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int left = (width - containerWidth) / 2;
		int top = (height - containerHeight) / 2;
		blit(left, top, 0, 0, containerWidth, containerHeight);
	}
}
