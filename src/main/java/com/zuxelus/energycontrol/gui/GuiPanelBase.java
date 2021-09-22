package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardText;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiContainerBase;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiPanelBase<T extends AbstractContainerMenu> extends GuiContainerBase<T> implements ContainerListener {
	protected static final int ID_LABELS = 1;
	protected static final int ID_SLOPE = 2;
	protected static final int ID_COLORS = 3;
	protected static final int ID_POWER = 4;
	protected static final int ID_TEXT = 5;
	protected static final int ID_TICKRATE = 6;

	protected String name;
	protected TileEntityInfoPanel panel;
	protected EditBox textboxTitle;
	protected byte activeTab;
	protected boolean modified;
	protected ItemStack oldStack = ItemStack.EMPTY;

	public GuiPanelBase(T container, Inventory inv, Component name, ResourceLocation texture) {
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
		menu.removeSlotListener(this);
		menu.addSlotListener(this);
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, imageWidth, 6);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (textboxTitle != null) {
			textboxTitle.mouseReleased(mouseX - leftPos, mouseY - topPos, mouseButton);
			if (textboxTitle.isFocused())
				return true;
			magicalSpecialHackyFocus(null);
			updateTitle();
		}
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		if (textboxTitle != null)
			textboxTitle.tick();
	}

	@SuppressWarnings("resource")
	protected void updateTitle() {
		if (textboxTitle == null)
			return;
		if (panel.getLevel().isClientSide) {
			CompoundTag tag = new CompoundTag();
			tag.putInt("type", 4);
			tag.putInt("slot", activeTab);
			tag.putString("title", textboxTitle.getValue());
			NetworkHelper.updateSeverTileEntity(panel.getBlockPos(), tag);
			ItemStack card = panel.getItem(activeTab);
			if (!card.isEmpty() && card.getItem() instanceof ItemCardMain)
				new ItemCardReader(card).setTitle(textboxTitle.getValue());
		}
	}

	@Override
	public void onClose() {
		updateTitle();
		super.onClose();
		menu.removeSlotListener(this);
	}

	protected void actionPerformed(Button button, int id) {
		switch (id) {
		case ID_LABELS:
			boolean checked = !panel.getShowLabels();
			((GuiButtonGeneral) button).setTextureTop(checked ? 15 : 31);
			NetworkHelper.updateSeverTileEntity(panel.getBlockPos(), 3, checked ? 1 : 0);
			panel.setShowLabels(checked);
			break;
		case ID_COLORS:
			Screen colorGui = new GuiScreenColor(this, panel);
			minecraft.setScreen(colorGui);
			break;
		case ID_TEXT:
			openTextGui();
			break;
		case ID_TICKRATE:
			GuiHorizontalSlider slider = new GuiHorizontalSlider(this, panel);
			minecraft.setScreen(slider);
			break;
		}
	}

	protected void openTextGui() {
		ItemStack card = panel.getCards().get(activeTab);
		if (!card.isEmpty() && card.getItem() instanceof ItemCardText)
			minecraft.setScreen(new GuiCardText(card, panel, this, activeTab));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 69)
			return true;
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void slotChanged(AbstractContainerMenu container, int slot, ItemStack stack) {
		initControls();
	}

	@Override
	public void dataChanged(AbstractContainerMenu container, int varToUpdate, int newValue) {}
}
