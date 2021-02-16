package com.zuxelus.energycontrol.gui;

import java.io.IOException;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.controls.GuiTextArea;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCardText extends GuiScreen {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_text_card.png");

	private ICardReader reader;
	private ItemStack stack;
	private TileEntityInfoPanel panel;
	private GuiInfoPanel parentGui;
	private int slot;
	private GuiTextArea textArea;

	protected int xSize = 226;
	protected int ySize = 146;
	protected int guiLeft;
	protected int guiTop;

	private static final int lineCount = 10;

	public GuiCardText(ItemStack card, TileEntityInfoPanel panel, GuiInfoPanel gui, int slot) {
		this.reader = new ItemCardReader(card);
		this.stack = card;
		this.panel = panel;
		parentGui = gui;
		this.slot = slot;
		
	}

	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

	private void initControls() {
		buttonList.clear();
		addButton(new GuiButton(1, guiLeft + xSize - 60 - 8, guiTop + 120, 60, 20, "Ok"));
		textArea = new GuiTextArea(fontRenderer, guiLeft + 8, guiTop + 5, xSize - 16, ySize - 35, lineCount);
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
	protected void actionPerformed(GuiButton button) {
		if (textArea != null) {
			String[] lines = textArea.getText();
			if (lines != null)
				for (int i = 0; i < lines.length; i++)
					reader.setString("line_" + i, lines[i]);
		}
		reader.updateServer(stack, panel, slot);
		FMLClientHandler.instance().getClient().displayGuiScreen(parentGui);
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
