package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmSlider;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIndustrialAlarm extends GuiBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_industrial_alarm.png");

	private TileEntityHowlerAlarm alarm;
	private GuiHowlerAlarmSlider slider;

	public GuiIndustrialAlarm(TileEntityHowlerAlarm alarm) {
		super("tile.industrial_alarm.name", 131, 64);
		this.alarm = alarm;
	}

	@Override
	public void initGui() {
		super.initGui();
		slider = new GuiHowlerAlarmSlider(3, guiLeft + 12, guiTop + 33, alarm);
		addButton(slider);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int which) {
		super.mouseReleased(mouseX, mouseY, which);
		if ((which == 0 || which == 1) && slider.dragging)
			slider.mouseReleased(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}
}
