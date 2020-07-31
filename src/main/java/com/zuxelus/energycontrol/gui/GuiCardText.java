package com.zuxelus.energycontrol.gui;

import java.io.IOException;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ICardGui;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.gui.controls.GuiTextArea;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardSettingsReader;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiCardText extends GuiScreen implements ICardGui {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_text_card.png");

	private ItemCardSettingsReader wrapper;
	private ICardReader reader;
	private GuiTextArea textArea;

	protected int xSize = 226;
	protected int ySize = 146;
	protected int guiLeft;
	protected int guiTop;

	private static final int lineCount = 10;

	public GuiCardText(ICardReader helper) {
		this.reader = helper;
	}

	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

	@Override
	public void setCardSettingsHelper(ItemCardSettingsReader wrapper) {
		this.wrapper = wrapper;
	}

	private void initControls() {
		buttonList.clear();
		addButton(new GuiButton(1, guiLeft + xSize - 60 - 8, guiTop + 120, 60, 20, "Ok"));
		textArea = new GuiTextArea(fontRendererObj, guiLeft + 8, guiTop + 5, xSize - 16, ySize - 35, lineCount);
		textArea.setFocused(true);
		String[] data = textArea.getText();
		for (int i = 0; i < lineCount; i++)
			data[i] = reader.getString("line_" + i);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (textArea != null)
			textArea.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
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
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		mc.getTextureManager().bindTexture(TEXTURE_LOCATION);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		if (textArea != null)
			textArea.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1 || (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode() && (textArea == null || !textArea.isFocused())))
			actionPerformed(null);
		else if (textArea != null && textArea.isFocused())
			textArea.textAreaKeyTyped(typedChar, keyCode);
		else
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		initControls();
	}
}
