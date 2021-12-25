package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerTimer;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiTimer extends GuiContainerBase<ContainerTimer> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_timer.png");
	private TileEntityTimer timer;
	private TextFieldWidget textboxTimer = null;
	private boolean lastIsWorking;
	private CompactButton buttons[] = new CompactButton[9];

	public GuiTimer(ContainerTimer container, PlayerInventory inventory, Text title) {
		super(container, inventory, title, TEXTURE);
		backgroundWidth = 100;
		backgroundHeight = 136;
		this.timer = container.te;
		lastIsWorking = timer.getIsWorking();
	}

	@Override
	public void init() {
		super.init();
		lastIsWorking = timer.getIsWorking();

		addButton(new CompactButton(0, x + 14, y + 50, 34, 12, new LiteralText("+1"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(1, x + 14, y + 64, 34, 12, new LiteralText("+10"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(2, x + 50, y + 50, 34, 12, new LiteralText("+100"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(3, x + 50, y + 64, 34, 12, new LiteralText("+1000"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(4, x + 14, y + 78, 70, 12, new LiteralText("+10000"), (button) -> { actionPerformed(button); }));

		addButton(new CompactButton(5, x + 14, y + 36, 34, 12, new LiteralText("Reset"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(6, x + 50, y + 36, 34, 12, new LiteralText("Ticks"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(7, x + 14, y + 92, 70, 12,
				new LiteralText(timer.getInvertRedstone() ? "No Redstone" : "Redstone"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(8, x + 14, y + 106, 70, 12,
				new LiteralText(lastIsWorking ? "Stop" : "Start"), (button) -> { actionPerformed(button); }));

		updateCaptions(timer.getIsTicks());

		textboxTimer = addTextFieldWidget(20, 20, 58, 12, !lastIsWorking, timer.getTimeString());
	}

	private void addButton(CompactButton button) {
		addDrawableChild(button);
		buttons[button.getId()] = button;
	}

	private void updateCaptions(boolean isTicks) {
		buttons[0].setMessage(new LiteralText(isTicks ? "+1" : "+1s"));
		buttons[1].setMessage(new LiteralText(isTicks ? "+10" : "+30s"));
		buttons[2].setMessage(new LiteralText(isTicks ? "+100" : "+1m"));
		buttons[3].setMessage(new LiteralText(isTicks ? "+1000" : "+30m"));
		buttons[4].setMessage(new LiteralText(isTicks ? "+10000" : "+1h"));
		buttons[6].setMessage(new LiteralText(isTicks ? "Ticks" : "Time"));
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackground(matrixStack, partialTicks, mouseX, mouseY);
		textboxTimer.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, backgroundWidth, 6);
	}

	@Override
	public void handledScreenTick() {
		super.handledScreenTick();
		textboxTimer.tick();
		boolean isWorking = timer.getIsWorking();
		if (isWorking != lastIsWorking) {
			textboxTimer.setEditable(!isWorking);
			textboxTimer.changeFocus(!isWorking);
			buttons[8].setMessage(new LiteralText(isWorking ? "Stop" : "Start"));
			lastIsWorking = isWorking;
		}
		if (isWorking)
			textboxTimer.setText(timer.getTimeString());
	}

	@Override
	public void onClose() {
		updateTime(0);
		super.onClose();
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

	protected void actionPerformed(ButtonWidget button) {
		int id = ((CompactButton) button).getId();
		boolean isTicks = timer.getIsTicks();
		switch(id) {
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
			buttons[7].setMessage(new LiteralText(!invertRedstone ? "No Redstone" : "Redstone"));
			break;
		case 8:
			updateTime(0);
			boolean isWorking = timer.getIsWorking();
			NetworkHelper.updateSeverTileEntity(timer.getPos(), 3, isWorking ? 0 : 1);
			timer.setIsWorking(!isWorking);
			buttons[8].setMessage(new LiteralText(!isWorking ? "Stop" : "Start"));
			textboxTimer.setEditable(isWorking);
			textboxTimer.changeFocus(isWorking);
			break;
		}
	}

	/*@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (keyCode == 1) // Esc button
			mc.player.closeScreen();
		else if (typedChar == 13) // Enter
			updateTime(0);
		else if(textboxTimer != null && textboxTimer.isFocused() && (Character.isDigit(typedChar) || typedChar == 0 || typedChar == 8))
			textboxTimer.textboxKeyTyped(typedChar, keyCode);
	}*/
}
