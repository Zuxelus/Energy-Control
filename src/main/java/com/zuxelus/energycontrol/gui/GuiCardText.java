package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;
import com.zuxelus.zlib.gui.controls.GuiTextArea;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class GuiCardText extends GuiBase {
	private ICardReader reader;
	private ItemStack stack;
	private TileEntityInfoPanel panel;
	private GuiPanelBase parentGui;
	private int slot;
	private GuiTextArea textArea;

	private static final int lineCount = 10;

	public GuiCardText(ItemStack card, TileEntityInfoPanel panel, GuiPanelBase gui, int slot) {
		super("", 256, 146, EnergyControl.MODID + ":textures/gui/gui_text_card.png");
		this.reader = new ItemCardReader(card);
		this.stack = card;
		this.panel = panel;
		parentGui = gui;
		this.slot = slot;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButton(1, guiLeft + xSize - 60 - 8, guiTop + 120, 60, 20, "Ok"));
		buttonList.add(new GuiButton(2, guiLeft + 8, guiTop + 120, 60, 20, "Style"));
		textArea = new GuiTextArea(fontRendererObj, guiLeft + 8, guiTop + 5, xSize - 16, ySize - 35, lineCount);
		textArea.setFocused(true);
		String[] data = textArea.getText();
		for (int i = 0; i < lineCount; i++)
			data[i] = reader.getString("line_" + i);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		if (textArea != null)
			textArea.drawTextBox();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (textArea != null)
			textArea.updateCursorCounter();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 1:
			updateTextArea();
			break;
		case 2:
			textArea.writeText("@");
			break;
		}
	}

	private void updateTextArea() {
		if (textArea != null) {
			String[] lines = textArea.getText();
			if (lines != null)
				for (int i = 0; i < lines.length; i++)
					reader.setString("line_" + i, lines[i]);
		}
		reader.updateServer(stack, panel, slot);
		mc.displayGuiScreen(parentGui);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (textArea != null) {
			textArea.mouseClicked(mouseX, mouseY, mouseButton);
			textArea.setFocused(true);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (keyCode == 1 || (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode() && (textArea == null || !textArea.isFocused())))
			updateTextArea();
		else if (textArea != null && textArea.isFocused())
			textArea.textAreaKeyTyped(typedChar, keyCode);
		else
			super.keyTyped(typedChar, keyCode);
	}
}
