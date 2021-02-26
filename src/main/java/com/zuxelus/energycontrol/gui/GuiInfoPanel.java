package com.zuxelus.energycontrol.gui;

import java.io.IOException;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.gui.GuiContainerBase;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInfoPanel extends GuiContainerBase implements IContainerListener {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_info_panel.png");

	protected static final int ID_LABELS = 1;
	protected static final int ID_SLOPE = 2;
	protected static final int ID_COLORS = 3;
	protected static final int ID_POWER = 4;
	protected static final int ID_TEXT = 5;
	protected static final int ID_TICKRATE = 6;

	protected TileEntityInfoPanel panel;
	protected GuiTextField textboxTitle;
	protected byte activeTab;
	protected boolean modified;

	@SuppressWarnings("rawtypes")
	public GuiInfoPanel(ContainerBase container) {
		super(container, "tile.info_panel.name", TEXTURE);
		ySize = 201;
		panel = (TileEntityInfoPanel)container.te;
		modified = false;
		activeTab = 0;
	}

	@SuppressWarnings("rawtypes")
	public GuiInfoPanel(ContainerBase container, String name, ResourceLocation texture) {
		super(container, name, texture);
		panel = (TileEntityInfoPanel)container.te;
		modified = false;
		activeTab = 0;
	}

	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);
		buttonList.clear();
		addButton(new GuiButtonGeneral(ID_LABELS, guiLeft + xSize - 24, guiTop + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31).setGradient());
		if (panel.getColored())
			addButton(new GuiButtonGeneral(ID_COLORS, guiLeft + xSize - 24, guiTop + 42 + 17, 16, 16, TEXTURE, 192, 0).setGradient().setScale(2));
		addButton(new GuiButtonGeneral(ID_TICKRATE, guiLeft + xSize - 24, guiTop + 42 + 17 * 3, 16, 16, Integer.toString(panel.getTickRate())).setGradient());
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			int slot = panel.getCardSlot(stack);
			if (stack.getItemDamage() == ItemCardType.CARD_TEXT)
				addButton(new GuiButtonGeneral(ID_TEXT, guiLeft + xSize - 24, guiTop + 42 + 17 * 2, 16, 16, "txt").setGradient());
			List<PanelSetting> settingsList = ItemCardMain.getSettingsList(stack);

			int hy = fontRenderer.FONT_HEIGHT + 1;
			int y = 1;
			int x = guiLeft + 24;
			if (settingsList != null)
				for (PanelSetting panelSetting : settingsList) {
					addButton(new GuiInfoPanelCheckBox(0, x + 4, guiTop + 28 + hy * y, panelSetting, panel, slot, fontRenderer));
					y++;
				}
			if (!modified) {
				textboxTitle = new GuiTextField(0, fontRenderer, 7, 16, 162, 18);
				textboxTitle.setFocused(true);
				textboxTitle.setText(new ItemCardReader(stack).getTitle());
			}
		} else {
			modified = false;
			textboxTitle = null;
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		initControls();
		inventorySlots.removeListener(this);
		inventorySlots.addListener(this);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(name, xSize, 6);
		if (textboxTitle != null)
			textboxTitle.drawTextBox();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (textboxTitle != null) {
			boolean focused = textboxTitle.isFocused();
			textboxTitle.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			if (textboxTitle.isFocused() != focused)
				updateTitle();
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (textboxTitle != null)
			textboxTitle.updateCursorCounter();
	}

	protected void updateTitle() {
		if (textboxTitle == null)
			return;
		if (panel.getWorld().isRemote) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 4);
			tag.setInteger("slot", activeTab);
			tag.setString("title", textboxTitle.getText());
			NetworkHelper.updateSeverTileEntity(panel.getPos(), tag);
			ItemStack card = panel.getStackInSlot(activeTab);
			if (!card.isEmpty() && card.getItem() instanceof ItemCardMain)
				new ItemCardReader(card).setTitle(textboxTitle.getText());
		}
	}

	@Override
	public void onGuiClosed() {
		updateTitle();
		super.onGuiClosed();
		inventorySlots.removeListener(this);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case ID_LABELS:
			boolean checked = !panel.getShowLabels();
			((GuiButtonGeneral) button).setTextureTop(checked ? 15 : 31);
			NetworkHelper.updateSeverTileEntity(panel.getPos(), 3, checked ? 1 : 0);
			panel.setShowLabels(checked);
			break;
		case ID_COLORS:
			GuiScreen colorGui = new GuiScreenColor(this, panel);
			mc.displayGuiScreen(colorGui);
			break;
		case ID_TEXT:
			openTextGui();
			break;
		case ID_TICKRATE:
			GuiHorizontalSlider slider = new GuiHorizontalSlider(this, panel);
			mc.displayGuiScreen(slider);
			break;
		}
	}

	protected void openTextGui() {
		ItemStack card = panel.getCards().get(activeTab);
		if (!card.isEmpty() && card.getItem() instanceof ItemCardMain && card.getItemDamage() == ItemCardType.CARD_TEXT)
			mc.displayGuiScreen(new GuiCardText(card, panel, this, activeTab));
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (textboxTitle != null && textboxTitle.isFocused())
			if (keyCode == 1)
				mc.player.closeScreen();
			else if (typedChar == 13)
				updateTitle();
			else {
				modified = true;
				textboxTitle.textboxKeyTyped(typedChar, keyCode);
			}
		else
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void sendAllContents(Container container, NonNullList<ItemStack> itemsList) {
		sendSlotContents(container, 0, container.getSlot(0).getStack());
	}

	@Override
	public void sendSlotContents(Container container, int slotInd, ItemStack stack) {
		initControls();
	}

	@Override
	public void sendWindowProperty(Container container, int varToUpdate, int newValue) { }

	@Override
	public void sendAllWindowProperties(Container container, IInventory inventory) { }
}
