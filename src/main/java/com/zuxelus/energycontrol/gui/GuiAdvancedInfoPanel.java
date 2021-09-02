package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
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

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiAdvancedInfoPanel extends GuiPanelBase<ContainerAdvancedInfoPanel> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_advanced_info_panel.png");

	public GuiAdvancedInfoPanel(ContainerAdvancedInfoPanel container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title, TEXTURE);
		ySize = 223;
		panel = (TileEntityAdvancedInfoPanel) container.te;
		name = I18n.format("block.energycontrol.info_panel_advanced");
	}

	@Override
	protected void initButtons() {
		addButton(new GuiButtonGeneral(guiLeft + 83, guiTop + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31, (button) -> { actionPerformed(button, ID_LABELS); }).setGradient());
		addButton(new GuiButtonGeneral(guiLeft + 83 + 17 * 1, guiTop + 42, 16, 16, TEXTURE, 192, 15, (button) -> { actionPerformed(button, ID_SLOPE); }).setGradient());
		addButton(new GuiButtonGeneral(guiLeft + 83 + 17 * 2, guiTop + 42, 16, 16, TEXTURE, 192, 28, (button) -> { actionPerformed(button, ID_COLORS); }).setGradient().setScale(2));
		addButton(new GuiButtonGeneral(guiLeft + 83 + 17 * 3, guiTop + 42, 16, 16, TEXTURE, 192 - 16, getIconPowerTopOffset(((TileEntityAdvancedInfoPanel) panel).getPowerMode()), (button) -> { actionPerformed(button, ID_POWER); }).setGradient());
		addButton(new GuiButtonGeneral(guiLeft + 83 + 17 * 4, guiTop + 42 + 17, 16, 16, new StringTextComponent(Integer.toString(panel.getTickRate())), (button) -> { actionPerformed(button, ID_TICKRATE); }).setGradient());
	}

	@Override
	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);
		if (!ItemStack.areItemsEqual(stack, oldStack)) {
			if (!oldStack.isEmpty() && stack.isEmpty())
				updateTitle();
			oldStack = stack.copy();
			buttons.clear();
			children.clear();
			initButtons();
			if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
				int slot = panel.getCardSlot(stack);
				if (stack.getItem() instanceof ItemCardText)
					addButton(new GuiButtonGeneral(guiLeft + 83 + 17 * 4, guiTop + 42, 16, 16, new StringTextComponent("txt"), (button) -> { actionPerformed(button, ID_TEXT); }).setGradient());
				List<PanelSetting> settingsList = ((ItemCardMain) stack.getItem()).getSettingsList();
	
				int hy = font.FONT_HEIGHT + 1;
				int y = 1;
				int x = guiLeft + 24;
				if (settingsList != null)
					for (PanelSetting panelSetting : settingsList) {
						addButton(new GuiInfoPanelCheckBox(x + 4, guiTop + 51 + hy * y, panelSetting, panel, slot, font));
						y++;
					}
				if (!modified) {
					textboxTitle = new TextFieldWidget(font, guiLeft + 7, guiTop + 16, 162, 18, null, StringTextComponent.EMPTY);
					textboxTitle.changeFocus(true);
					textboxTitle.setText(new ItemCardReader(stack).getTitle());
					children.add(textboxTitle);
					setFocusedDefault(textboxTitle);
				}
			} else {
				modified = false;
				textboxTitle = null;
			}
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
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		getMinecraft().getTextureManager().bindTexture(TEXTURE);
		blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize);
		blit(matrixStack, guiLeft + 24, guiTop + 62 + activeTab * 14, 182, 0, 1, 15);
		if (textboxTitle != null)
			textboxTitle.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (mouseX >= guiLeft + 7 && mouseX <= guiLeft + 24 && mouseY >= guiTop + 62 && mouseY <= guiTop + 104) {
			byte newTab = (byte) ((mouseY - guiTop - 62) / 14);
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
			NetworkHelper.updateSeverTileEntity(panel.getPos(), 11, mode);
			((TileEntityAdvancedInfoPanel) panel).powerMode = mode;
			return;
		case ID_SLOPE:
			minecraft.displayGuiScreen(new GuiPanelSlope(this, ((TileEntityAdvancedInfoPanel) panel)));
			return;
		}
		super.actionPerformed(button, id);
	}
}
