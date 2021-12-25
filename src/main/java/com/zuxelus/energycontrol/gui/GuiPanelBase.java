package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardText;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiContainerBase;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class GuiPanelBase<T extends ScreenHandler> extends GuiContainerBase<T> implements ScreenHandlerListener {
	protected static final int ID_LABELS = 1;
	protected static final int ID_SLOPE = 2;
	protected static final int ID_COLORS = 3;
	protected static final int ID_POWER = 4;
	protected static final int ID_TEXT = 5;
	protected static final int ID_TICKRATE = 6;

	protected String name;
	protected TileEntityInfoPanel panel;
	protected TextFieldWidget textboxTitle;
	protected byte activeTab;
	protected boolean modified;
	protected ItemStack oldStack = ItemStack.EMPTY;

	public GuiPanelBase(T container, PlayerInventory inv, Text name, Identifier texture) {
		super(container, inv, name, texture);
		activeTab = 0;
		modified = false;
	}

	protected abstract void initButtons();

	protected abstract void initControls();

	@Override
	public void init() {
		super.init();
		initButtons();
		initControls();
		handler.removeListener(this);
		handler.addListener(this);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		drawMouseoverTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, backgroundWidth, 6);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (textboxTitle != null) {
			textboxTitle.mouseReleased(mouseX - x, mouseY - y, mouseButton);
			if (textboxTitle.isFocused())
				return true;
			focusOn(null);
			updateTitle();
		}
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handledScreenTick() {
		super.handledScreenTick();
		if (textboxTitle != null)
			textboxTitle.tick();
	}

	@SuppressWarnings("resource")
	protected void updateTitle() {
		if (textboxTitle == null)
			return;
		if (panel.getWorld().isClient) {
			NbtCompound tag = new NbtCompound();
			tag.putInt("type", 4);
			tag.putInt("slot", activeTab);
			tag.putString("title", textboxTitle.getText());
			NetworkHelper.updateSeverTileEntity(panel.getPos(), tag);
			ItemStack card = panel.getStack(activeTab);
			if (!card.isEmpty() && card.getItem() instanceof ItemCardMain)
				new ItemCardReader(card).setTitle(textboxTitle.getText());
		}
	}

	@Override
	public void onClose() {
		updateTitle();
		super.onClose();
		handler.removeListener(this);
	}

	protected void actionPerformed(ButtonWidget button, int id) {
		switch (id) {
		case ID_LABELS:
			boolean checked = !panel.getShowLabels();
			((GuiButtonGeneral) button).setTextureTop(checked ? 15 : 31);
			NetworkHelper.updateSeverTileEntity(panel.getPos(), 3, checked ? 1 : 0);
			panel.setShowLabels(checked);
			break;
		case ID_COLORS:
			Screen colorGui = new GuiScreenColor(this, panel);
			client.setScreen(colorGui);
			break;
		case ID_TEXT:
			oldStack = ItemStack.EMPTY;
			openTextGui();
			break;
		case ID_TICKRATE:
			GuiHorizontalSlider slider = new GuiHorizontalSlider(this, panel);
			client.setScreen(slider);
			break;
		}
	}

	protected void openTextGui() {
		ItemStack card = panel.getCards().get(activeTab);
		if (!card.isEmpty() && card.getItem() instanceof ItemCardText)
			client.setScreen(new GuiCardText(card, panel, this, activeTab));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 69)
			return true;
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void onSlotUpdate(ScreenHandler container, int slot, ItemStack stack) {
		initControls();
	}

	@Override
	public void onPropertyUpdate(ScreenHandler container, int varToUpdate, int newValue) {}
}
