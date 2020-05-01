package com.zuxelus.energycontrol.gui;

import java.io.IOException;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ICardGui;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.containers.ContainerAdvancedInfoPanel;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardSettingsReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiAdvancedInfoPanel extends GuiInfoPanel {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_advanced_info_panel.png");
	
	private static final int ID_LABELS = 1;
	private static final int ID_SLOPE = 2;
	private static final int ID_COLORS = 3;
	private static final int ID_POWER = 4;
	private static final int ID_SETTINGS = 5;

	private TileEntityAdvancedInfoPanel panel;
	private byte activeTab;
	private boolean initialized;

	public GuiAdvancedInfoPanel(ContainerAdvancedInfoPanel container) {
		super(container);
		ySize = 212;
		panel = container.te;
		name = I18n.format("tile.info_panel_advanced.name");
		activeTab = 0;
		initialized = false;
	}
	
	@Override
	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);
		if (stack.isEmpty() && stack.equals(prevCard) && initialized)
			return;
		initialized = true;
		buttonList.clear();
		prevCard = stack;

		// labels
		buttonList.add(new IconButton(ID_LABELS, guiLeft + 83, guiTop + 42, 16, 16, TEXTURE, 192 - 16, getIconLabelsTopOffset(panel.getShowLabels())));
		// slope
		buttonList.add(new IconButton(ID_SLOPE, guiLeft + 83 + 17 * 1, guiTop + 42, 16, 16, TEXTURE, 192, 15));
		// colors
		buttonList.add(new IconButton(ID_COLORS, guiLeft + 83 + 17 * 2, guiTop + 42, 16, 16, TEXTURE, 192, 15 + 16));
		// power
		buttonList.add(new IconButton(ID_POWER, guiLeft + 83 + 17 * 3, guiTop + 42, 16, 16, TEXTURE, 192 - 16, getIconPowerTopOffset(panel.getPowerMode())));

		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			int slot = panel.getCardSlot(stack);
			if (stack.getItemDamage() == ItemCardType.CARD_TEXT)
				buttonList.add(new IconButton(ID_SETTINGS, guiLeft + 83 + 17 * 4, guiTop + 42, 16, 16, TEXTURE, 192, 15 + 16 * 2));
			List<PanelSetting> settingsList = ItemCardMain.getSettingsList(stack);

			int hy = fontRenderer.FONT_HEIGHT + 1;
			int y = 1;
			int x = guiLeft + 24;
			if (settingsList != null)
				for (PanelSetting panelSetting : settingsList) {
					if (y <= 6) {
						buttonList.add(new GuiInfoPanelCheckBox(0, x + 4, guiTop + 55 + hy * y, panelSetting, panel, slot, fontRenderer));
					} else if (y >= 7 && y <= 12) {
						buttonList.add(new GuiInfoPanelCheckBox(0, x + 22, guiTop + 55 - 6 * hy + hy * y, panelSetting, panel, slot, fontRenderer));
					} else if (y >= 13 && y <= 18) {
						buttonList.add(new GuiInfoPanelCheckBox(0, x + 44, guiTop + 55 - 12 * hy + hy * y, panelSetting, panel, slot, fontRenderer));
					} else if (y >= 19 && y <= 24) {
						buttonList.add(new GuiInfoPanelCheckBox(0, x + 68, guiTop + 55 - 18 * hy + hy * y, panelSetting, panel, slot, fontRenderer));
					} else if (y >= 25 && y <= 32) {
						buttonList.add(new GuiInfoPanelCheckBox(0, x + 92, guiTop + 55 - 24 * hy + hy * y, panelSetting, panel, slot, fontRenderer));
					} else if (y >= 31 && y <= 38) {
						buttonList.add(new GuiInfoPanelCheckBox(0, x + 114, guiTop + 55 - 32 * hy + hy * y, panelSetting, panel, slot, fontRenderer));
					} else if (y >= 37 && y <= 44) {
						buttonList.add(new GuiInfoPanelCheckBox(0, x + 136, guiTop + 55 - 38 * hy + hy * y, panelSetting, panel, slot, fontRenderer));
					} else
						buttonList.add(new GuiInfoPanelCheckBox(0, x + 158, guiTop + 55 - 44 * hy + hy * y, panelSetting, panel, slot, fontRenderer));
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
	
	private int getIconLabelsTopOffset(boolean checked) {
		return checked ? 15 : 31;
	}

	private int getIconPowerTopOffset(byte mode) {
		switch (mode) {
		case TileEntityAdvancedInfoPanel.POWER_REDSTONE:
			return 15 + 16 * 2;
		case TileEntityAdvancedInfoPanel.POWER_INVERTED:
			return 15 + 16 * 3;
		case TileEntityAdvancedInfoPanel.POWER_ON:
			return 15 + 16 * 4;
		case TileEntityAdvancedInfoPanel.POWER_OFF:
			return 15 + 16 * 5;
		}
		return 15 + 16 * 2;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		drawTexturedModalRect(left + 24, top + 62 + activeTab * 14, 182, 0, 1, 15);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseX >= guiLeft + 7 && mouseX <= guiLeft + 24 && mouseY >= guiTop + 62 && mouseY <= guiTop + 104) {
			byte newTab = (byte) ((mouseY - guiTop - 62) / 14);
			if (newTab > 2)
				newTab = 2;
			if (newTab != activeTab && modified)
				updateTitle();
			activeTab = newTab;
		}
	}
	

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case ID_COLORS:
			GuiScreen colorGui = new GuiScreenColor(this, panel);
			mc.displayGuiScreen(colorGui);
			initialized = false;
			break;
		case ID_SETTINGS:
			ItemStack card = panel.getCards().get(activeTab);
			if (!card.isEmpty() && card.getItem() instanceof ItemCardMain && card.getItemDamage() == ItemCardType.CARD_TEXT) {
				ItemCardReader reader = new ItemCardReader(card);
				ICardGui guiObject = ItemCardMain.getSettingsScreen(reader);
				if (!(guiObject instanceof GuiScreen)) {
					EnergyControl.logger.warn("Invalid card, getSettingsScreen method should return GuiScreen object");
					return;
				}
				GuiScreen gui = (GuiScreen) guiObject;
				ItemCardSettingsReader wrapper = new ItemCardSettingsReader(card, panel, this, (byte) 0);
				((ICardGui) gui).setCardSettingsHelper(wrapper);
				mc.displayGuiScreen(gui);
			}
			break;
		case ID_LABELS:
			boolean checked = !panel.getShowLabels();
			if (button instanceof IconButton){
				IconButton iButton = (IconButton)button;
				iButton.textureTop = getIconLabelsTopOffset(checked);
			}
			NetworkHelper.updateSeverTileEntity(panel.getPos(), 3, checked ? 1 : 0);
			panel.setShowLabels(checked);
			break;
		case ID_POWER:
			byte mode = panel.getNextPowerMode();
			if (button instanceof IconButton) {
				IconButton iButton = (IconButton) button;
				iButton.textureTop = getIconPowerTopOffset(mode);
			}
			NetworkHelper.updateSeverTileEntity(panel.getPos(), 11, mode);
			panel.powerMode = mode;
			break;
		case ID_SLOPE:
			GuiPanelSlope slopeGui = new GuiPanelSlope(this, panel);
			mc.displayGuiScreen(slopeGui);
			initialized = false;
			break;
		}
	}


}
