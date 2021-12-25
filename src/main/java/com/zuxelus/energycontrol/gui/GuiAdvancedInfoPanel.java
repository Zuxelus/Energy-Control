package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiAdvancedInfoPanel extends GuiPanelBase<ContainerAdvancedInfoPanel> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_advanced_info_panel.png");

	public GuiAdvancedInfoPanel(ContainerAdvancedInfoPanel container, PlayerInventory inventory, Text title) {
		super(container, inventory, title, TEXTURE);
		backgroundHeight = 223;
		panel = (TileEntityAdvancedInfoPanel) container.te;
		name = I18n.translate("block.energycontrol.info_panel_advanced");
	}

	@Override
	protected void initButtons() {
		addDrawableChild(new GuiButtonGeneral(x + 83, y + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31, (button) -> { actionPerformed(button, ID_LABELS); }).setGradient());
		addDrawableChild(new GuiButtonGeneral(x + 83 + 17 * 1, y + 42, 16, 16, TEXTURE, 192, 15, (button) -> { actionPerformed(button, ID_SLOPE); }).setGradient());
		addDrawableChild(new GuiButtonGeneral(x + 83 + 17 * 2, y + 42, 16, 16, TEXTURE, 192, 28, (button) -> { actionPerformed(button, ID_COLORS); }).setGradient().setScale(2));
		addDrawableChild(new GuiButtonGeneral(x + 83 + 17 * 3, y + 42, 16, 16, TEXTURE, 192 - 16, getIconPowerTopOffset(((TileEntityAdvancedInfoPanel) panel).getPowerMode()), (button) -> { actionPerformed(button, ID_POWER); }).setGradient());
		addDrawableChild(new GuiButtonGeneral(x + 83 + 17 * 4, y + 42 + 17, 16, 16, new LiteralText(Integer.toString(panel.getTickRate())), (button) -> { actionPerformed(button, ID_TICKRATE); }).setGradient());
	}

	@Override
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
				addDrawableChild(new GuiButtonGeneral(x + 83 + 17 * 4, y + 42, 16, 16, new LiteralText("txt"), (button) -> { actionPerformed(button, ID_TEXT); }).setGradient());
			List<PanelSetting> settingsList = ((ItemCardMain) stack.getItem()).getSettingsList();

			int hy = textRenderer.fontHeight + 1;
			int yy = 1;
			if (settingsList != null)
				for (PanelSetting panelSetting : settingsList) {
					addDrawableChild(new GuiInfoPanelCheckBox(x + 28, y + 51 + hy * yy, panelSetting, panel, slot, textRenderer));
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
	protected void drawBackground(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexture(matrixStack, x, y, 0, 0, backgroundWidth, backgroundHeight);
		drawTexture(matrixStack, x + 24, y + 62 + activeTab * 14, 182, 0, 1, 15);
		if (textboxTitle != null)
			textboxTitle.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (mouseX >= x + 7 && mouseX <= x + 24 && mouseY >= y + 62 && mouseY <= y + 104) {
			byte newTab = (byte) ((mouseY - y - 62) / 14);
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
	protected void actionPerformed(ButtonWidget button, int id) {
		switch (id) {
		case ID_POWER:
			byte mode = ((TileEntityAdvancedInfoPanel) panel).getNextPowerMode();
			((GuiButtonGeneral) button).setTextureTop(getIconPowerTopOffset(mode));
			NetworkHelper.updateSeverTileEntity(panel.getPos(), 11, mode);
			((TileEntityAdvancedInfoPanel) panel).powerMode = mode;
			return;
		case ID_SLOPE:
			client.setScreen(new GuiPanelSlope(this, ((TileEntityAdvancedInfoPanel) panel)));
			return;
		}
		super.actionPerformed(button, id);
	}
}
