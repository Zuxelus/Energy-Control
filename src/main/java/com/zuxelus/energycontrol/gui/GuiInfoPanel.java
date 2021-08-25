package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardText;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.gui.GuiContainerBase;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiInfoPanel extends GuiContainerBase<ContainerBase<TileEntityInfoPanel>> implements IContainerListener {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_info_panel.png");

	protected static final int ID_LABELS = 1;
	protected static final int ID_SLOPE = 2;
	protected static final int ID_COLORS = 3;
	protected static final int ID_POWER = 4;
	protected static final int ID_TEXT = 5;
	protected static final int ID_TICKRATE = 6;

	protected String name;
	private TileEntityInfoPanel panel;
	protected TextFieldWidget textboxTitle;
	protected byte activeTab;
	protected boolean modified;

	public GuiInfoPanel(ContainerBase<TileEntityInfoPanel> container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title, TEXTURE);
		ySize = 201;
		panel = (TileEntityInfoPanel)container.te;
		name = I18n.format("block.energycontrol.info_panel");
		modified = false;
		activeTab = 0;
	}

	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);

		buttons.clear();
		children.clear();
		addButton(new GuiButtonGeneral(guiLeft + xSize - 24, guiTop + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31, (button) -> { actionPerformed(button, ID_LABELS); }).setGradient());
		if (panel.isColoredEval())
			addButton(new GuiButtonGeneral(guiLeft + xSize - 24, guiTop + 42 + 17, 16, 16, TEXTURE, 192, 0, (button) -> { actionPerformed(button, ID_COLORS); }).setGradient().setScale(2));
		addButton(new GuiButtonGeneral(guiLeft + xSize - 24, guiTop + 42 + 17 * 3, 16, 16, new StringTextComponent(Integer.toString(panel.getTickRate())), (button) -> { actionPerformed(button, ID_TICKRATE); }).setGradient());
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			int slot = panel.getCardSlot(stack);
			if (stack.getItem() instanceof ItemCardText)
				addButton(new GuiButtonGeneral(guiLeft + xSize - 24, guiTop + 42 + 17 * 2, 16, 16, new StringTextComponent("txt"), (button) -> { actionPerformed(button, ID_TEXT); }).setGradient());
			List<PanelSetting> settingsList = ((ItemCardMain) stack.getItem()).getSettingsList();

			int hy = font.FONT_HEIGHT + 1;
			int y = 1;
			int x = guiLeft + 24;
			if (settingsList != null)
				for (PanelSetting panelSetting : settingsList) {
					addButton(new GuiInfoPanelCheckBox(x + 4, guiTop + 28 + hy * y, panelSetting, panel, slot, font));
					y++;
				}
			if (!modified) {
				textboxTitle = new TextFieldWidget(font, guiLeft + 7, guiTop + 16, 162, 18, null, StringTextComponent.EMPTY);
				textboxTitle.changeFocus(true);
				textboxTitle.setText(new ItemCardReader(stack).getTitle());
				children.add(textboxTitle);
				setFocusedDefault(textboxTitle);
			}
		} else {
			modified = false;
			textboxTitle = null;
		}
	}

	@Override
	public void init() {
		super.init();
		initControls();
		container.removeListener(this);
		container.addListener(this);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		if (textboxTitle != null)
			textboxTitle.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, xSize, 6);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (textboxTitle != null) {
			textboxTitle.mouseReleased(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			if (textboxTitle.isFocused())
				return true;
			setListenerDefault(null);
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
