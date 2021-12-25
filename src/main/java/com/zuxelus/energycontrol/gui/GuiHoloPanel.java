package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.containers.ContainerHoloPanel;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardText;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiHoloPanel extends GuiPanelBase<ContainerHoloPanel> { 
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_holo_panel.png");

	public GuiHoloPanel(ContainerHoloPanel container, PlayerInventory inventory, Text title) {
		super(container, inventory, title, TEXTURE);
		backgroundHeight = 201;
		panel = (TileEntityInfoPanel) container.te;
		name = I18n.translate("block.energycontrol.holo_panel");
	}

	protected void initButtons() {
		addDrawableChild(new GuiButtonGeneral(x + backgroundWidth - 24, y + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31, (button) -> { actionPerformed(button, ID_LABELS); }).setGradient());
		if (panel.isColoredEval())
			addDrawableChild(new GuiButtonGeneral(x + backgroundWidth - 24, y + 42 + 17, 16, 16, TEXTURE, 192, 0, (button) -> { actionPerformed(button, ID_COLORS); }).setGradient().setScale(2));
		addDrawableChild(new GuiButtonGeneral(x + backgroundWidth - 24, y + 42 + 17 * 3, 16, 16, new LiteralText(Integer.toString(panel.getTickRate())), (button) -> { actionPerformed(button, ID_TICKRATE); }).setGradient());
	}

	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);
		if (ItemStack.areItemsEqualIgnoreDamage(stack, oldStack))
			return;
		if (!oldStack.isEmpty() && stack.isEmpty())
			updateTitle();
		oldStack = stack.copy();
		clearChildren();
		initButtons();
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			int slot = panel.getCardSlot(stack);
			if (stack.getItem() instanceof ItemCardText)
				addDrawableChild(new GuiButtonGeneral(x + backgroundWidth - 24, y + 42 + 17 * 2, 16, 16, new LiteralText("txt"), (button) -> { actionPerformed(button, ID_TEXT); }).setGradient());
			List<PanelSetting> settingsList = ((ItemCardMain) stack.getItem()).getSettingsList();

			int hy = textRenderer.fontHeight + 1;
			int yy = 1;
			if (settingsList != null)
				for (PanelSetting panelSetting : settingsList) {
					addDrawableChild(new GuiInfoPanelCheckBox(x + 28, y + 28 + hy * yy, panelSetting, panel, slot, textRenderer));
					yy++;
				}
			if (!modified) {
				textboxTitle = new TextFieldWidget(textRenderer, x + 7, y + 16, 162, 18, null, LiteralText.EMPTY);
				textboxTitle.changeFocus(true);
				textboxTitle.setText(new ItemCardReader(stack).getTitle());
				addSelectableChild(textboxTitle);
				setInitialFocus(textboxTitle);
			}
		} else {
			modified = false;
			textboxTitle = null;
		}
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackground(matrixStack, partialTicks, mouseX, mouseY);
		if (textboxTitle != null)
			textboxTitle.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}
}
