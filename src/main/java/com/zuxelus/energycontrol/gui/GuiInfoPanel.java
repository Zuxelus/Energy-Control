package com.zuxelus.energycontrol.gui;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ICardGui;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.containers.ContainerBase;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelShowLabels;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardSettingsReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInfoPanel extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_info_panel.png");

	protected String name;
	private TileEntityInfoPanel panel;
	public ItemStack prevCard;
	protected GuiTextField textboxTitle;
	protected byte activeTab;
	protected boolean modified;
	public boolean isColored;

	public GuiInfoPanel(ContainerBase container) {
		super(container);
		ySize = 201;
		panel = (TileEntityInfoPanel)container.te;
		name = I18n.format("tile.info_panel.name");
		modified = false;
		// inverted value on start to force initControls
		isColored = !this.panel.getColored();
		prevCard = ItemStack.EMPTY;
		activeTab = 0;
	}

	protected void initControls() {
		ItemStack stack = panel.getCards().get(0);
		if (stack.equals(prevCard) && panel.getColored() == isColored)
			return;

		buttonList.clear();
		prevCard = stack;
		isColored = panel.getColored();
		addButton(new GuiInfoPanelShowLabels(0, guiLeft + xSize - 25, guiTop + 42, panel));
		int delta = 0;
		if (isColored) {
			addButton(new CompactButton(112, guiLeft + xSize - 25, guiTop + 55, 18, 12, "T"));
			delta = 15;
		}
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			int slot = panel.getCardSlot(stack);
			if (stack.getItemDamage() == ItemCardType.CARD_TEXT)
				addButton(new CompactButton(111, guiLeft + xSize - 25, guiTop + 55 + delta, 18, 12, "..."));
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
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
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
		initControls();
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
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 112) { // color upgrade
			GuiScreen colorGui = new GuiScreenColor(this, panel);
			mc.displayGuiScreen(colorGui);
		} else if (button.id == 111) {
			ItemStack card = panel.getCards().get(0);
			if (card.isEmpty())
				return;
			if (card.getItem() instanceof ItemCardMain && card.getItemDamage() == ItemCardType.CARD_TEXT) {
				ItemCardReader reader = new ItemCardReader(card);
				ICardGui guiObject = ItemCardMain.getSettingsScreen(reader);
				if (!(guiObject instanceof GuiScreen)) {
					EnergyControl.logger.warn("Invalid card, getSettingsScreen method should return GuiScreen object");
					return;
				}
				GuiScreen gui = (GuiScreen) guiObject;
				ItemCardSettingsReader wrapper = new ItemCardSettingsReader(card, panel, this, (byte)0);
				((ICardGui) gui).setCardSettingsHelper(wrapper);
				mc.displayGuiScreen(gui);
			}
		}
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
}
