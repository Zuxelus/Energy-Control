package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.api.IItemCard;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.containers.ContainerInfoPanel;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardText;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class GuiInfoPanel extends GuiPanelBase<ContainerInfoPanel> { 
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/gui/gui_info_panel.png");
	private GuiButtonGeneral colorButton;

	public GuiInfoPanel(ContainerInfoPanel container, Inventory inventory, Component title) {
		super(container, inventory, title, TEXTURE, 201);
		panel = (TileEntityInfoPanel) container.te;
		name = I18n.get("block.energycontrol.info_panel");
	}

	protected void initButtons() {
		addRenderableWidget(new GuiButtonGeneral(leftPos + imageWidth - 24, topPos + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31, (button) -> { actionPerformed(button, ID_LABELS); }).setGradient());
		colorButton = null;
		updateColorButton();
		addRenderableWidget(new GuiButtonGeneral(leftPos + imageWidth - 24, topPos + 42 + 17 * 3, 16, 16, Component.literal(Integer.toString(panel.getTickRate())), (button) -> { actionPerformed(button, ID_TICKRATE); }).setGradient());
	}

	// the color upgrade slot can change while the GUI is open
	private void updateColorButton() {
		boolean colored = panel.isColoredEval();
		if (colored && colorButton == null)
			colorButton = addRenderableWidget(new GuiButtonGeneral(leftPos + imageWidth - 24, topPos + 42 + 17, 16, 16, TEXTURE, 192, 0, (button) -> { actionPerformed(button, ID_COLORS); }).setGradient().setScale(2));
		else if (!colored && colorButton != null) {
			removeWidget(colorButton);
			colorButton = null;
		}
	}

	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);
		if (ItemStack.isSameItem(stack, oldStack)) {
			updateColorButton();
			return;
		}
		if (!oldStack.isEmpty() && stack.isEmpty())
			updateTitle();
		oldStack = stack.copy();
		clearWidgets();
		initButtons();
		if (ItemCardMain.isCard(stack)) {
			int slot = panel.getCardSlot(stack);
			if (stack.getItem() instanceof ItemCardText)
				addRenderableWidget(new GuiButtonGeneral(leftPos + imageWidth - 24, topPos + 42 + 17 * 2, 16, 16, Component.literal("txt"), (button) -> { actionPerformed(button, ID_TEXT); }).setGradient());
			List<PanelSetting> settingsList = ((IItemCard) stack.getItem()).getSettingsList();

			int hy = font.lineHeight + 1;
			int yy = 1;
			if (settingsList != null)
				for (PanelSetting panelSetting : settingsList) {
					addRenderableWidget(new GuiInfoPanelCheckBox(leftPos + 28, topPos + 28 + hy * yy, panelSetting, panel, slot, font));
					yy++;
				}
			if (!modified) {
				textboxTitle = new EditBox(font, leftPos + 7, topPos + 16, 162, 18, null, CommonComponents.EMPTY);
				textboxTitle.setValue(new ItemCardReader(stack).getTitle());
				addWidget(textboxTitle);
				setInitialFocus(textboxTitle);
			}
		} else {
			modified = false;
			textboxTitle = null;
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(context, mouseX, mouseY, partialTicks);
		if (textboxTitle != null)
			textboxTitle.extractRenderState(context, mouseX, mouseY, partialTicks);
	}
}
