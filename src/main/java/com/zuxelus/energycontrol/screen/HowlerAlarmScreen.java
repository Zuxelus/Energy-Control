package com.zuxelus.energycontrol.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blockentities.HowlerAlarmBlockEntity;
import com.zuxelus.energycontrol.screen.controls.GuiHowlerAlarmListBox;
import com.zuxelus.energycontrol.screen.controls.GuiHowlerAlarmSlider;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
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
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		textRenderer.draw(matrices, getTitle().getString(), (containerWidth - textRenderer.getWidth(getTitle().getString())) / 2, 6, 0x404040);
		textRenderer.draw(matrices, I18n.translate("msg.ec.HowlerAlarmSound"), 12, 53, 0x404040);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		client.getTextureManager().bindTexture(TEXTURE);
		int left = (width - containerWidth) / 2;
		int top = (height - containerHeight) / 2;
		drawTexture(matrices, left, top, 0, 0, containerWidth, containerHeight);
	}
}
