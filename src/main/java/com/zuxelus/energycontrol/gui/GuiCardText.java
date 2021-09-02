package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;
import com.zuxelus.zlib.gui.controls.GuiTextArea;

import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
		addButton(new Button(guiLeft + xSize - 60 - 8, guiTop + 120, 60, 20, DialogTexts.GUI_DONE, (button) -> { actionPerformed(); }));
		textArea = new GuiTextArea(font, guiLeft + 8, guiTop + 5, xSize - 16, ySize - 35, lineCount);
		textArea.changeFocus(true);
		children.add(textArea);
		setInitialFocus(textArea);
		String[] data = textArea.getText();
		for (int i = 0; i < lineCount; i++)
			data[i] = reader.getString("line_" + i);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		textArea.render(matrixStack, mouseY, mouseY, partialTicks);
	}

	private void actionPerformed() {
		if (textArea != null) {
			String[] lines = textArea.getText();
			if (lines != null)
				for (int i = 0; i < lines.length; i++)
					reader.setString("line_" + i, lines[i]);
		}
		reader.updateServer(stack, panel, slot);
		minecraft.setScreen(parentGui);
	}

	@Override
	public void onClose() {
		actionPerformed();
		super.onClose();
	}
}
