package com.zuxelus.energycontrol.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blockentities.HowlerAlarmBlockEntity;
import com.zuxelus.energycontrol.screen.controls.GuiHowlerAlarmSlider;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IndustrialAlarmScreen extends ScreenBase {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_industrial_alarm.png");

	private HowlerAlarmBlockEntity alarm;
	private GuiHowlerAlarmSlider slider;

	public IndustrialAlarmScreen(HowlerAlarmBlockEntity alarm) {
		super(new TranslatableText("block.energycontrol.industrial_alarm"), 131, 64);
		this.alarm = alarm;
	}

	@Override
	public void init() {
		super.init();
		slider = new GuiHowlerAlarmSlider(3, x + 12, y + 33, alarm);
		addButton(slider);
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		textRenderer.draw(matrices, getTitle().getString(), (containerWidth - textRenderer.getWidth(getTitle().getString())) / 2, 6, 0x404040);
	}

	/*@Override
	protected void mouseReleased(int mouseX, int mouseY, int which) {
		super.mouseReleased(mouseX, mouseY, which);
		if ((which == 0 || which == 1) && slider.dragging)
			slider.mouseReleased(mouseX, mouseY);
	}*/

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		client.getTextureManager().bindTexture(TEXTURE);
		int left = (width - containerWidth) / 2;
		int top = (height - containerHeight) / 2;
		drawTexture(matrices, left, top, 0, 0, containerWidth, containerHeight);
	}
}
