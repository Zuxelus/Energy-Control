package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ICardGui;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.gui.controls.GuiTextArea;
import com.zuxelus.energycontrol.items.cards.ItemCardSettingsReader;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.Identifier;

public class CardTextScreen extends Screen implements ICardGui {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_text_card.png");

	private ItemCardSettingsReader wrapper;
	private ICardReader reader;
	private GuiTextArea textArea;

	protected int xSize = 226;
	protected int ySize = 146;
	protected int guiLeft;
	protected int guiTop;

	private static final int lineCount = 10;

	public CardTextScreen(ICardReader helper) {
		super(NarratorManager.EMPTY);
		this.reader = helper;
	}

	@Override
	public boolean isPauseScreen(){
		return false;
	}

	@Override
	public void setCardSettingsHelper(ItemCardSettingsReader wrapper) {
		this.wrapper = wrapper;
	}

	private void initControls() {
		buttons.clear();
		addButton(new ButtonWidget(guiLeft + xSize - 60 - 8, guiTop + 120, 60, 20, "Ok", (buttonWidget) -> { actionPerformed(); }));
		textArea = new GuiTextArea(font, guiLeft + 8, guiTop + 5, xSize - 16, ySize - 35, lineCount);
		textArea.setFocused(true);
		String[] data = textArea.getText();
		for (int i = 0; i < lineCount; i++)
			data[i] = reader.getString("line_" + i);
		setInitialFocus(textArea);
		children.add(textArea);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (textArea != null)
			if (textArea.mouseClicked(mouseX, mouseY, button))
				return true;
		return super.mouseClicked(mouseX, mouseY, button);
	}

	protected void actionPerformed() {
		if (textArea != null && wrapper != null) {
			String[] lines = textArea.getText();
			if (lines != null)
				for (int i = 0; i < lines.length; i++)
					wrapper.setString("line_" + i, lines[i]);
		}
		wrapper.commit();
		wrapper.closeGui();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		blit(left, top, 0, 0, xSize, ySize);
		if (textArea != null)
			textArea.drawTextBox();
		super.render(mouseX, mouseY, delta);
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		if (textArea != null && textArea.isFocused())
			if (textArea.textAreaCharTyped(chr, keyCode))
				return true;
		return super.charTyped(chr, keyCode);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (textArea != null && textArea.isFocused())
			if (textArea.textAreaKeyTyped(keyCode, scanCode, modifiers))
				return true;
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public void onClose() {
		actionPerformed();
		super.onClose();
	}

	@Override
	public void init() {
		super.init();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		initControls();
	}
}
