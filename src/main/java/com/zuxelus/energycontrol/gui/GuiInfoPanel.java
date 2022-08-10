package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.containers.ContainerInfoPanel;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardText;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiInfoPanel extends GuiPanelBase<ContainerInfoPanel> { 
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_info_panel.png");

	public GuiInfoPanel(ContainerInfoPanel container, Inventory inventory, Component title) {
		super(container, inventory, title, TEXTURE);
		imageHeight = 201;
		panel = (TileEntityInfoPanel) container.te;
		name = I18n.get("block.energycontrol.info_panel");
	}

	protected void initButtons() {
		addRenderableWidget(new GuiButtonGeneral(leftPos + imageWidth - 24, topPos + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31, (button) -> { actionPerformed(button, ID_LABELS); }).setGradient());
		if (panel.isColoredEval())
			addRenderableWidget(new GuiButtonGeneral(leftPos + imageWidth - 24, topPos + 42 + 17, 16, 16, TEXTURE, 192, 0, (button) -> { actionPerformed(button, ID_COLORS); }).setGradient().setScale(2));
		addRenderableWidget(new GuiButtonGeneral(leftPos + imageWidth - 24, topPos + 42 + 17 * 3, 16, 16, new TextComponent(Integer.toString(panel.getTickRate())), (button) -> { actionPerformed(button, ID_TICKRATE); }).setGradient());
	}

	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);
		if (ItemStack.isSame(stack, oldStack))
			return;
		if (!oldStack.isEmpty() && stack.isEmpty())
			updateTitle();
		oldStack = stack.copy();
		clearWidgets();
		initButtons();
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			int slot = panel.getCardSlot(stack);
			if (stack.getItem() instanceof ItemCardText)
				addRenderableWidget(new GuiButtonGeneral(leftPos + imageWidth - 24, topPos + 42 + 17 * 2, 16, 16, new TextComponent("txt"), (button) -> { actionPerformed(button, ID_TEXT); }).setGradient());
			List<PanelSetting> settingsList = ((ItemCardMain) stack.getItem()).getSettingsList();

			int hy = font.lineHeight + 1;
			int y = 1;
			if (settingsList != null)
				for (PanelSetting panelSetting : settingsList) {
					addRenderableWidget(new GuiInfoPanelCheckBox(leftPos + 28, topPos + 28 + hy * y, panelSetting, panel, slot, font));
					y++;
				}
			if (!modified) {
				textboxTitle = new EditBox(font, leftPos + 7, topPos + 16, 162, 18, null, TextComponent.EMPTY);
				textboxTitle.changeFocus(true);
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
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
		if (textboxTitle != null)
			textboxTitle.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}
}
