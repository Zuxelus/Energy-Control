package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardText;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiContainerBase;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiPanelBase<T extends Container> extends GuiContainerBase<T> implements IContainerListener {
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

	public GuiPanelBase(T container, PlayerInventory inv, ITextComponent name, ResourceLocation texture) {
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
		container.removeListener(this);
		container.addListener(this);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(title.getString(), xSize, 6);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (textboxTitle != null) {
			textboxTitle.mouseReleased(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			if (textboxTitle.isFocused())
				return true;
			func_212932_b(null);
			updateTitle();
		}
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void tick() {
		super.tick();
		if (textboxTitle != null)
			textboxTitle.tick();
	}

	@SuppressWarnings("resource")
	protected void updateTitle() {
		if (textboxTitle == null)
			return;
		if (panel.getWorld().isRemote) {
			CompoundNBT tag = new CompoundNBT();
			tag.putInt("type", 4);
			tag.putInt("slot", activeTab);
			tag.putString("title", textboxTitle.getText());
			NetworkHelper.updateSeverTileEntity(panel.getPos(), tag);
			ItemStack card = panel.getStackInSlot(activeTab);
			if (!card.isEmpty() && card.getItem() instanceof ItemCardMain)
				new ItemCardReader(card).setTitle(textboxTitle.getText());
		}
	}

	@Override
	public void onClose() {
		updateTitle();
		super.onClose();
		container.removeListener(this);
	}

	protected void actionPerformed(Button button, int id) {
		switch (id) {
		case ID_LABELS:
			boolean checked = !panel.getShowLabels();
			((GuiButtonGeneral) button).setTextureTop(checked ? 15 : 31);
			NetworkHelper.updateSeverTileEntity(panel.getPos(), 3, checked ? 1 : 0);
			panel.setShowLabels(checked);
			break;
		case ID_COLORS:
			Screen colorGui = new GuiScreenColor(this, panel);
			minecraft.displayGuiScreen(colorGui);
			break;
		case ID_TEXT:
			oldStack = ItemStack.EMPTY;
			openTextGui();
			break;
		case ID_TICKRATE:
			GuiHorizontalSlider slider = new GuiHorizontalSlider(this, panel);
			minecraft.displayGuiScreen(slider);
			break;
		}
	}

	protected void openTextGui() {
		ItemStack card = panel.getCards().get(activeTab);
		if (!card.isEmpty() && card.getItem() instanceof ItemCardText)
			minecraft.displayGuiScreen(new GuiCardText(card, panel, this, activeTab));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 69)
			return true;
		return super.keyPressed(keyCode, scanCode, modifiers);
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
}
