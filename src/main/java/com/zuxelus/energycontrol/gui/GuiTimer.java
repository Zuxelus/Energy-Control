package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerTimer;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class GuiTimer extends GuiContainerBase<ContainerTimer> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/gui/gui_timer.png");
	private TileEntityTimer timer;
	private EditBox textboxTimer = null;
	private boolean lastIsWorking;
	private CompactButton buttons[] = new CompactButton[9];

	public GuiTimer(ContainerTimer container, Inventory inventory, Component title) {
		super(container, inventory, title, TEXTURE, 100, 136);
		this.timer = container.te;
		lastIsWorking = timer.getIsWorking();
	}

	@Override
	public void init() {
		super.init();
		lastIsWorking = timer.getIsWorking();

		addButton(new CompactButton(0, leftPos + 14, topPos + 50, 34, 12, Component.literal("+1"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(1, leftPos + 14, topPos + 64, 34, 12, Component.literal("+10"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(2, leftPos + 50, topPos + 50, 34, 12, Component.literal("+100"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(3, leftPos + 50, topPos + 64, 34, 12, Component.literal("+1000"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(4, leftPos + 14, topPos + 78, 70, 12, Component.literal("+10000"), (button) -> { actionPerformed(button); }));

		addButton(new CompactButton(5, leftPos + 14, topPos + 36, 34, 12, Component.literal("Reset"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(6, leftPos + 50, topPos + 36, 34, 12, Component.literal("Ticks"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(7, leftPos + 14, topPos + 92, 70, 12,
				Component.literal(timer.getInvertRedstone() ? "No Redstone" : "Redstone"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(8, leftPos + 14, topPos + 106, 70, 12,
				Component.literal(lastIsWorking ? "Stop" : "Start"), (button) -> { actionPerformed(button); }));

		updateCaptions(timer.getIsTicks());

		textboxTimer = addTextFieldWidget(20, 20, 58, 12, !lastIsWorking, timer.getTimeString());
	}

	private void addButton(CompactButton button) {
		addRenderableWidget(button);
		buttons[button.getId()] = button;
	}

	private void updateCaptions(boolean isTicks) {
		buttons[0].setMessage(Component.literal(isTicks ? "+1" : "+1s"));
		buttons[1].setMessage(Component.literal(isTicks ? "+10" : "+30s"));
		buttons[2].setMessage(Component.literal(isTicks ? "+100" : "+1m"));
		buttons[3].setMessage(Component.literal(isTicks ? "+1000" : "+30m"));
		buttons[4].setMessage(Component.literal(isTicks ? "+10000" : "+1h"));
		buttons[6].setMessage(Component.literal(isTicks ? "Ticks" : "Time"));
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(context, mouseX, mouseY, partialTicks);
		textboxTimer.extractRenderState(context, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor context, int mouseX, int mouseY) {
		drawCenteredText(context, title, imageWidth, 6);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		boolean isWorking = timer.getIsWorking();
		if (isWorking != lastIsWorking) {
			textboxTimer.setEditable(!isWorking);
			buttons[8].setMessage(Component.literal(isWorking ? "Stop" : "Start"));
			lastIsWorking = isWorking;
		}
		if (isWorking)
			textboxTimer.setValue(timer.getTimeString());
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
			String value = textboxTimer.getValue();
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
			NetworkHelper.updateSeverTileEntity(timer.getBlockPos(), 1, time);
			timer.setTime(time);
		}
		textboxTimer.setValue(timer.getTimeString());
	}

	protected void actionPerformed(Button button) {
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
			NetworkHelper.updateSeverTileEntity(timer.getBlockPos(), 1, 0);
			timer.setTime(0);
			textboxTimer.setValue(timer.getTimeString());
			break;
		case 6:
			NetworkHelper.updateSeverTileEntity(timer.getBlockPos(), 4, isTicks ? 0 : 1);
			timer.setIsTicks(!isTicks);
			textboxTimer.setValue(timer.getTimeString());
			updateCaptions(!isTicks);
			break;
		case 7:
			boolean invertRedstone = timer.getInvertRedstone();
			NetworkHelper.updateSeverTileEntity(timer.getBlockPos(), 2, invertRedstone ? 0 : 1);
			timer.setInvertRedstone(!invertRedstone);
			buttons[7].setMessage(Component.literal(!invertRedstone ? "No Redstone" : "Redstone"));
			break;
		case 8:
			updateTime(0);
			boolean isWorking = timer.getIsWorking();
			NetworkHelper.updateSeverTileEntity(timer.getBlockPos(), 3, isWorking ? 0 : 1);
			timer.setIsWorking(!isWorking);
			buttons[8].setMessage(Component.literal(!isWorking ? "Stop" : "Start"));
			textboxTimer.setEditable(isWorking);
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
