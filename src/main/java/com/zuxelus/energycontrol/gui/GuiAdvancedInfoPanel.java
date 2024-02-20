package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.containers.ContainerAdvancedInfoPanel;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardText;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiAdvancedInfoPanel extends GuiPanelBase<ContainerAdvancedInfoPanel> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_advanced_info_panel.png");

	public GuiAdvancedInfoPanel(ContainerAdvancedInfoPanel container, Inventory inventory, Component title) {
		super(container, inventory, title, TEXTURE);
		imageHeight = 223;
		panel = (TileEntityAdvancedInfoPanel) container.te;
		name = I18n.get("block.energycontrol.info_panel_advanced");
	}

	@Override
	protected void initButtons() {
		addRenderableWidget(new GuiButtonGeneral(leftPos + 83, topPos + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31, (button) -> { actionPerformed(button, ID_LABELS); }).setGradient());
		addRenderableWidget(new GuiButtonGeneral(leftPos + 83 + 17 * 1, topPos + 42, 16, 16, TEXTURE, 192, 15, (button) -> { actionPerformed(button, ID_SLOPE); }).setGradient());
		addRenderableWidget(new GuiButtonGeneral(leftPos + 83 + 17 * 2, topPos + 42, 16, 16, TEXTURE, 192, 28, (button) -> { actionPerformed(button, ID_COLORS); }).setGradient().setScale(2));
		addRenderableWidget(new GuiButtonGeneral(leftPos + 83 + 17 * 3, topPos + 42, 16, 16, TEXTURE, 192 - 16, getIconPowerTopOffset(((TileEntityAdvancedInfoPanel) panel).getPowerMode()), (button) -> { actionPerformed(button, ID_POWER); }).setGradient());
		addRenderableWidget(new GuiButtonGeneral(leftPos + 83 + 17 * 4, topPos + 42 + 17, 16, 16, Component.literal(Integer.toString(panel.getTickRate())), (button) -> { actionPerformed(button, ID_TICKRATE); }).setGradient());
	}

	@Override
	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);
		if (ItemStack.isSameItem(stack, oldStack))
			return;
		if (!oldStack.isEmpty() && stack.isEmpty())
			updateTitle();
		oldStack = stack.copy();
		clearWidgets();
		initButtons();
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			int slot = panel.getCardSlot(stack);
			if (stack.getItem() instanceof ItemCardText)
				addRenderableWidget(new GuiButtonGeneral(leftPos + 83 + 17 * 4, topPos + 42, 16, 16, Component.literal("txt"), (button) -> { actionPerformed(button, ID_TEXT); }).setGradient());
			List<PanelSetting> settingsList = ((ItemCardMain) stack.getItem()).getSettingsList();

			int hy = font.lineHeight + 1;
			int y = 1;
			if (settingsList != null)
				for (PanelSetting panelSetting : settingsList) {
					addRenderableWidget(new GuiInfoPanelCheckBox(leftPos + 28, topPos + 51 + hy * y, panelSetting, panel, slot, font));
					y++;
				}
			if (!modified) {
				textboxTitle = new EditBox(font, leftPos + 7, topPos + 16, 162, 18, null, CommonComponents.EMPTY);
				//textboxTitle.changeFocus(true);
				textboxTitle.setValue(new ItemCardReader(stack).getTitle());
				addWidget(textboxTitle);
				setInitialFocus(textboxTitle);
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
	protected void renderBg(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY) {
		matrixStack.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		matrixStack.blit(TEXTURE, leftPos + 24, topPos + 62 + activeTab * 14, 182, 0, 1, 15);
		if (textboxTitle != null)
			textboxTitle.renderWidget(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (mouseX >= leftPos + 7 && mouseX <= leftPos + 24 && mouseY >= topPos + 62 && mouseY <= topPos + 104) {
			byte newTab = (byte) ((mouseY - topPos - 62) / 14);
			if (newTab > 2)
				newTab = 2;
			if (newTab != activeTab && modified) {
				updateTitle();
				modified = false;
			}
			if (activeTab != newTab) {
				activeTab = newTab;
				initControls();
			}
		}
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(Button button, int id) {
		switch (id) {
		case ID_POWER:
			byte mode = ((TileEntityAdvancedInfoPanel) panel).getNextPowerMode();
			((GuiButtonGeneral) button).setTextureTop(getIconPowerTopOffset(mode));
			NetworkHelper.updateSeverTileEntity(panel.getBlockPos(), 11, mode);
			((TileEntityAdvancedInfoPanel) panel).powerMode = mode;
			return;
		case ID_SLOPE:
			minecraft.setScreen(new GuiPanelSlope(this, ((TileEntityAdvancedInfoPanel) panel)));
			return;
		}
		super.actionPerformed(button, id);
	}
}
