package com.zuxelus.energycontrol.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ICardGui;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardSettingsReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;
import com.zuxelus.zlib.network.NetworkHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiInfoPanel extends GuiContainer implements ICrafting {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_info_panel.png");
	protected static final int ID_LABELS = 1;
	protected static final int ID_SLOPE = 2;
	protected static final int ID_COLORS = 3;
	protected static final int ID_POWER = 4;
	protected static final int ID_TEXT = 5;
	protected static final int ID_TICKRATE = 6;

	protected String name;
	private TileEntityInfoPanel panel;
	protected GuiTextField textboxTitle;
	protected byte activeTab;
	protected boolean modified;

	public GuiInfoPanel(ContainerBase container) {
		super(container);
		ySize = 201;
		panel = (TileEntityInfoPanel)container.te;
		name = I18n.format("tile.info_panel.name");
		modified = false;
		// inverted value on start to force initControls
		activeTab = 0;
	}

	@SuppressWarnings("unchecked")
	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);
		buttonList.clear();
		buttonList.add(new GuiButtonGeneral(ID_LABELS, guiLeft + xSize - 24, guiTop + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31).setGradient());
		if (panel.getColored())
			buttonList.add(new GuiButtonGeneral(ID_COLORS, guiLeft + xSize - 24, guiTop + 42 + 17, 16, 16, TEXTURE, 192, 0).setGradient().setScale(2));
		buttonList.add(new GuiButtonGeneral(ID_TICKRATE, guiLeft + xSize - 24, guiTop + 42 + 17 * 3, 16, 16, Integer.toString(panel.getTickRate())).setGradient());
		if (stack != null && stack.getItem() instanceof ItemCardMain) {
			int slot = panel.getCardSlot(stack);
			if (stack.getItemDamage() == ItemCardType.CARD_TEXT)
				buttonList.add(new GuiButtonGeneral(ID_TEXT, guiLeft + xSize - 24, guiTop + 42 + 17 * 2, 16, 16, "txt").setGradient());
			List<PanelSetting> settingsList = ItemCardMain.getSettingsList(stack);

			int hy = fontRendererObj.FONT_HEIGHT + 1;
			int y = 1;
			int x = guiLeft + 24;
			if (settingsList != null)
				for (PanelSetting panelSetting : settingsList) {
					buttonList.add(new GuiInfoPanelCheckBox(0, x + 4, guiTop + 28 + hy * y, panelSetting, panel, slot, fontRendererObj));
					y++;
				}
			if (!modified) {
				textboxTitle = new GuiTextField(fontRendererObj, 7, 16, 162, 18);
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
		inventorySlots.removeCraftingFromCrafters(this);
		inventorySlots.addCraftingToCrafters(this);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(name, (xSize - fontRendererObj.getStringWidth(name)) / 2, 6, 0x404040);
		if (textboxTitle != null)
			textboxTitle.drawTextBox();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
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
		if (panel.getWorldObj().isRemote) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 4);
			tag.setInteger("slot", activeTab);
			tag.setString("title", textboxTitle.getText());
			NetworkHelper.updateSeverTileEntity(panel.xCoord, panel.yCoord, panel.zCoord, tag);
			ItemStack card = panel.getStackInSlot(activeTab);
			if (card != null && card.getItem() instanceof ItemCardMain)
				new ItemCardReader(card).setTitle(textboxTitle.getText());
		}
	}

	@Override
	public void onGuiClosed() {
		updateTitle();
		super.onGuiClosed();
		inventorySlots.removeCraftingFromCrafters(this);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case ID_LABELS:
			boolean checked = !panel.getShowLabels();
			((GuiButtonGeneral) button).setTextureTop(checked ? 15 : 31);
			NetworkHelper.updateSeverTileEntity(panel.xCoord, panel.yCoord, panel.zCoord, 3, checked ? 1 : 0);
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
		if (card != null && card.getItem() instanceof ItemCardMain && card.getItemDamage() == ItemCardType.CARD_TEXT) {
			ItemCardReader reader = new ItemCardReader(card);
			ICardGui guiObject = ItemCardMain.getSettingsScreen(reader);
			if (!(guiObject instanceof GuiScreen)) {
				EnergyControl.logger.warn("Invalid card, getSettingsScreen method should return GuiScreen object");
				return;
			}
			GuiScreen gui = (GuiScreen) guiObject;
			ItemCardSettingsReader wrapper = new ItemCardSettingsReader(card, panel, this, (byte) activeTab);
			((ICardGui) gui).setCardSettingsHelper(wrapper);
			mc.displayGuiScreen(gui);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (textboxTitle != null && textboxTitle.isFocused())
			if (keyCode == 1)
				mc.thePlayer.closeScreen();
			else if (typedChar == 13)
				updateTitle();
			else {
				modified = true;
				textboxTitle.textboxKeyTyped(typedChar, keyCode);
			}
		else
			super.keyTyped(typedChar, keyCode);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void sendContainerAndContentsToPlayer(Container container, List list) {
		this.sendSlotContents(container, 0, container.getSlot(0).getStack());
	}

	@Override
	public void sendSlotContents(Container container, int i, ItemStack stack) {
		initControls();
	}

	@Override
	public void sendProgressBarUpdate(Container container, int i, int j) { }
}
