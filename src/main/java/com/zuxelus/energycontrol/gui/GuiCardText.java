package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;
import com.zuxelus.zlib.gui.controls.GuiTextArea;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class GuiCardText extends GuiBase {
	private ICardReader reader;
	private ItemStack stack;
	private TileEntityInfoPanel panel;
	private GuiPanelBase<?> parentGui;
	private int slot;
	private GuiTextArea textArea;

	private static final int lineCount = 10;

	public GuiCardText(ItemStack card, TileEntityInfoPanel panel, GuiPanelBase<?> gui, int slot) {
		super("", 226, 146, EnergyControl.MODID + ":textures/gui/gui_text_card.png");
		this.reader = new ItemCardReader(card);
		this.stack = card;
		this.panel = panel;
		parentGui = gui;
		this.slot = slot;
	}

	@Override
	public void init() {
		super.init();
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> { actionPerformed(1); }).bounds(guiLeft + xSize - 60 - 8, guiTop + 120, 60, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Style"), (button) -> { actionPerformed(2); }).bounds(guiLeft + 8, guiTop + 120, 60, 20).build());
		textArea = new GuiTextArea(font, guiLeft + 8, guiTop + 5, xSize - 16, ySize - 35, lineCount);
		addWidget(textArea);
		setFocused(textArea);
		String[] data = textArea.getText();
		for (int i = 0; i < lineCount; i++)
			data[i] = reader.getString("line_" + i);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphicsExtractor context, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(context, partialTicks, mouseX, mouseY);
		textArea.extractRenderState(context, mouseX, mouseY, partialTicks);
	}

	@Override
	public void tick() {
		super.tick();
		textArea.updateCursorCounter();
	}

	private void actionPerformed(int id) {
		switch (id) {
		case 1:
			if (textArea != null) {
				String[] lines = textArea.getText();
				if (lines != null)
					for (int i = 0; i < lines.length; i++)
						reader.setString("line_" + i, lines[i]);
			}
			reader.updateServer(stack, panel, slot);
			minecraft.gui.setScreen(parentGui);
			break;
		case 2:
			textArea.writeText("@");
			break;
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		GuiEventListener control = getFocused();
		if (control instanceof GuiTextArea) {
			boolean result = super.mouseClicked(event, doubleClick);
			setFocused(control);
			return result;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		int keyCode = event.key();
		if (keyCode == InputConstants.KEY_ESCAPE) {
			actionPerformed(1);
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public void onClose() {
		actionPerformed(1);
		super.onClose();
	}
}
