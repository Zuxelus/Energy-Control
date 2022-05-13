package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.IItemCard;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiInfoPanel extends GuiPanelBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_info_panel.png");
	private boolean isColored;

	public GuiInfoPanel(ContainerBase container) {
		super(container, "tile.info_panel.name", TEXTURE);
		ySize = 201;
		panel = (TileEntityInfoPanel) container.te;
	}

	@Override
	protected void initButtons() {
		buttonList.add(new GuiButtonGeneral(ID_LABELS, guiLeft + xSize - 24, guiTop + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31).setGradient());
		isColored = panel.isColoredEval();
		if (isColored)
			buttonList.add(new GuiButtonGeneral(ID_COLORS, guiLeft + xSize - 24, guiTop + 42 + 17, 16, 16, TEXTURE, 192, 0).setGradient().setScale(2));
		buttonList.add(new GuiButtonGeneral(ID_TICKRATE, guiLeft + xSize - 24, guiTop + 42 + 17 * 3, 16, 16, Integer.toString(panel.getTickRate())).setGradient());
	}

	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);
		if (ItemStack.areItemStacksEqual(stack, oldStack) && panel.isColoredEval() == isColored)
			return;
		if (oldStack != null && stack == null)
			updateTitle();
		if (stack == null) // for 1.10 and less
			oldStack = null;
		else
			oldStack = stack.copy();
		buttonList.clear();
		initButtons();
		if (ItemCardMain.isCard(stack)) {
			int slot = panel.getCardSlot(stack);
			if (stack.getItemDamage() == ItemCardType.CARD_TEXT)
				buttonList.add(new GuiButtonGeneral(ID_TEXT, guiLeft + xSize - 24, guiTop + 42 + 17 * 2, 16, 16, "txt").setGradient());
			List<PanelSetting> settingsList = ((IItemCard) stack.getItem()).getSettingsList(stack);

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
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if (textboxTitle != null)
			textboxTitle.drawTextBox();
	}
}
