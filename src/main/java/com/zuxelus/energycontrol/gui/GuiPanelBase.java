package com.zuxelus.energycontrol.gui;

import java.io.IOException;

import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.gui.GuiContainerBase;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;

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
public abstract class GuiPanelBase extends GuiContainerBase implements IContainerListener {
	protected static final int ID_LABELS = 1;
	protected static final int ID_SLOPE = 2;
	protected static final int ID_COLORS = 3;
	protected static final int ID_POWER = 4;
	protected static final int ID_TEXT = 5;
	protected static final int ID_TICKRATE = 6;

	protected String name;
	protected TileEntityInfoPanel panel;
	protected GuiTextField textboxTitle;
	protected byte activeTab;
	protected boolean modified;
	protected ItemStack oldStack = ItemStack.EMPTY;

	public GuiPanelBase(ContainerBase<?> container, String name, ResourceLocation texture) {
		super(container, name, texture);
		activeTab = 0;
		modified = false;
	}

	protected abstract void initButtons();

	protected abstract void initControls();

	@Override
	public void initGui() {
		super.initGui();
		initButtons();
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
			if (ItemCardMain.isCard(card))
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
			oldStack = ItemStack.EMPTY;
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
		if (ItemCardMain.isCard(card) && card.getItemDamage() == ItemCardType.CARD_TEXT)
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
