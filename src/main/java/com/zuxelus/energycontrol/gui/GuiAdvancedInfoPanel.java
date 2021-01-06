package com.zuxelus.energycontrol.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.containers.ContainerAdvancedInfoPanel;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiAdvancedInfoPanel extends GuiInfoPanel {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_advanced_info_panel.png");
	private TileEntityAdvancedInfoPanel panel;

	public GuiAdvancedInfoPanel(ContainerAdvancedInfoPanel container) {
		super(container);
		ySize = 223;
		panel = container.te;
		name = I18n.format("tile.info_panel_advanced.name");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);
		buttonList.clear();

		buttonList.add(new GuiButtonGeneral(ID_LABELS, guiLeft + 83, guiTop + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31).setGradient());
		buttonList.add(new GuiButtonGeneral(ID_SLOPE, guiLeft + 83 + 17 * 1, guiTop + 42, 16, 16, TEXTURE, 192, 15).setGradient());
		buttonList.add(new GuiButtonGeneral(ID_COLORS, guiLeft + 83 + 17 * 2, guiTop + 42, 16, 16, TEXTURE, 192, 28).setGradient().setScale(2));
		buttonList.add(new GuiButtonGeneral(ID_POWER, guiLeft + 83 + 17 * 3, guiTop + 42, 16, 16, TEXTURE, 192 - 16, getIconPowerTopOffset(panel.getPowerMode())).setGradient());
		buttonList.add(new GuiButtonGeneral(ID_TICKRATE, guiLeft + 83 + 17 * 4, guiTop + 42 + 17, 16, 16, Integer.toString(panel.getTickRate())).setGradient());

		if (stack != null && stack.getItem() instanceof ItemCardMain) {
			int slot = panel.getCardSlot(stack);
			if (stack.getItemDamage() == ItemCardType.CARD_TEXT)
				buttonList.add(new GuiButtonGeneral(ID_TEXT, guiLeft + 83 + 17 * 4, guiTop + 42, 16, 16, "txt").setGradient());
			List<PanelSetting> settingsList = ItemCardMain.getSettingsList(stack);

			int hy = fontRendererObj.FONT_HEIGHT + 1;
			int y = 1;
			int x = guiLeft + 24;
			if (settingsList != null)
				for (PanelSetting panelSetting : settingsList) {
					buttonList.add(new GuiInfoPanelCheckBox(0, x + 4, guiTop + 51 + hy * y, panelSetting, panel, slot, fontRendererObj));
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
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		drawTexturedModalRect(left + 24, top + 62 + activeTab * 14, 182, 0, 1, 15);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseX >= guiLeft + 7 && mouseX <= guiLeft + 24 && mouseY >= guiTop + 62 && mouseY <= guiTop + 104) {
			byte newTab = (byte) ((mouseY - guiTop - 62) / 14);
			if (newTab > 2)
				newTab = 2;
			if (newTab != activeTab && modified) {
				updateTitle();
				modified = false;
			}
			activeTab = newTab;
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case ID_POWER:
			byte mode = panel.getNextPowerMode();
			((GuiButtonGeneral) button).setTextureTop(getIconPowerTopOffset(mode));
			NetworkHelper.updateSeverTileEntity(panel.xCoord, panel.yCoord, panel.zCoord, 11, mode);
			panel.powerMode = mode;
			return;
		case ID_SLOPE:
			GuiPanelSlope slopeGui = new GuiPanelSlope(this, panel);
			mc.displayGuiScreen(slopeGui);
			return;
		}
		super.actionPerformed(button);
	}
}
