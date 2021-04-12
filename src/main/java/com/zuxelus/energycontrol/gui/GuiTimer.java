package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTimer extends GuiBase {
	private TileEntityTimer timer;
	private GuiTextField textboxTimer = null;
	private boolean lastIsWorking;

	public GuiTimer(TileEntityTimer timer) {
		super("tile.timer.name", 100, 136, EnergyControl.MODID + ":textures/gui/gui_timer.png");
		this.timer = timer;
		lastIsWorking = timer.getIsWorking();
	}

	@Override
	public void initGui() {
		super.initGui();
		lastIsWorking = timer.getIsWorking();

		addButton(new CompactButton(0, guiLeft + 14, guiTop + 50, 34, 12, "+1"));
		addButton(new CompactButton(1, guiLeft + 14, guiTop + 64, 34, 12, "+10"));
		addButton(new CompactButton(2, guiLeft + 50, guiTop + 50, 34, 12, "+100"));
		addButton(new CompactButton(3, guiLeft + 50, guiTop + 64, 34, 12, "+1000"));
		addButton(new CompactButton(4, guiLeft + 14, guiTop + 78, 70, 12, "+10000"));

		addButton(new CompactButton(5, guiLeft + 14, guiTop + 36, 34, 12, "Reset"));
		addButton(new CompactButton(6, guiLeft + 50, guiTop + 36, 34, 12, "Ticks"));
		addButton(new CompactButton(7, guiLeft + 14, guiTop + 92, 70, 12, timer.getInvertRedstone() ? "No Redstone" : "Redstone"));
		addButton(new CompactButton(8, guiLeft + 14, guiTop + 106, 70, 12, lastIsWorking ? "Stop" : "Start"));

		updateCaptions(timer.getIsTicks());

		textboxTimer = new GuiTextField(11, fontRenderer, 20, 20, 58, 12);
		textboxTimer.setEnabled(!lastIsWorking);
		textboxTimer.setFocused(!lastIsWorking);
		textboxTimer.setText(timer.getTimeString());
	}

	private void updateCaptions(boolean isTicks) {
		buttonList.get(0).displayString = isTicks ? "+1" : "+1s";
		buttonList.get(1).displayString = isTicks ? "+10" : "+30s";
		buttonList.get(2).displayString = isTicks ? "+100" : "+1m";
		buttonList.get(3).displayString = isTicks ? "+1000" : "+30m";
		buttonList.get(4).displayString = isTicks ? "+10000" : "+1h";
		buttonList.get(6).displayString = isTicks ? "Ticks" : "Time";
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
		if (textboxTimer != null)
			textboxTimer.drawTextBox();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (textboxTimer != null) {
			textboxTimer.updateCursorCounter();
			boolean isWorking = timer.getIsWorking();
			if (isWorking != lastIsWorking) {
				textboxTimer.setEnabled(!isWorking);
				textboxTimer.setFocused(!isWorking);
				buttonList.get(8).displayString = isWorking ? "Stop" : "Start";
				lastIsWorking = isWorking;
			}
			if (isWorking)
				textboxTimer.setText(timer.getTimeString());
		}
	}

	@Override
	public void onGuiClosed() {
		updateTime(0);
		super.onGuiClosed();
	}

	private void updateTime(int delta) {
		if (textboxTimer == null)
			return;
		int time = 0;
		try {
			String value = textboxTimer.getText();
			if (timer.getIsTicks()) {
				if (!"".equals(value))
					time = Integer.parseInt(value);
			} else {
				String[] times = value.split(":");
				if (times.length == 2)
					time = (Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1])) * 20;
				if (times.length == 3)
					time = (Integer.parseInt(times[0]) * 3600 + Integer.parseInt(times[1]) * 60 + Integer.parseInt(times[2])) * 20;
			}
		} catch (NumberFormatException e) {	}
		time += delta;
		if (time < 0)
			time = 0;
		if (time >= 1000000)
			time = 1000000;
		if (timer.getTime() != time) {
			NetworkHelper.updateSeverTileEntity(timer.getPos(), 1, time);
			timer.setTime(time);
		}
		textboxTimer.setText(timer.getTimeString());
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		boolean isTicks = timer.getIsTicks();
		switch(button.id) {
		case 0:
			updateTime(isTicks ? 1 : 1 * 20);
			break;
		case 1:
			updateTime(isTicks ? 10 : 30 * 20);
			break;
		case 2:
			updateTime(isTicks ? 100 : 60 * 20);
			break;
		case 3:
			updateTime(isTicks ? 1000 : 30 * 60 * 20);
			break;
		case 4:
			updateTime(isTicks ? 10000 : 60 * 60 * 20);
			break;
		case 5:
			NetworkHelper.updateSeverTileEntity(timer.getPos(), 1, 0);
			timer.setTime(0);
			textboxTimer.setText(timer.getTimeString());
			break;
		case 6:
			NetworkHelper.updateSeverTileEntity(timer.getPos(), 4, isTicks ? 0 : 1);
			timer.setIsTicks(!isTicks);
			textboxTimer.setText(timer.getTimeString());
			updateCaptions(!isTicks);
			break;
		case 7:
			boolean invertRedstone = timer.getInvertRedstone();
			NetworkHelper.updateSeverTileEntity(timer.getPos(), 2, invertRedstone ? 0 : 1);
			timer.setInvertRedstone(!invertRedstone);
			buttonList.get(7).displayString = !invertRedstone ? "No Redstone" : "Redstone";
			break;
		case 8:
			updateTime(0);
			boolean isWorking = timer.getIsWorking();
			NetworkHelper.updateSeverTileEntity(timer.getPos(), 3, isWorking ? 0 : 1);
			timer.setIsWorking(!isWorking);
			buttonList.get(8).displayString = !isWorking ? "Stop" : "Start";
			textboxTimer.setEnabled(isWorking);
			textboxTimer.setFocused(isWorking);
			break;
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (keyCode == 1) // Esc button
			mc.player.closeScreen();
		else if (typedChar == 13) // Enter
			updateTime(0);
		else if(textboxTimer != null && textboxTimer.isFocused() && (Character.isDigit(typedChar) || typedChar == 0 || typedChar == 8))
			textboxTimer.textboxKeyTyped(typedChar, keyCode);
	}
}
