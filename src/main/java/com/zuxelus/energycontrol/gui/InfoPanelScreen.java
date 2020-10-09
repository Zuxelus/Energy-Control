package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ICardGui;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.containers.InfoPanelContainer;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelShowLabels;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardSettingsReader;
import com.zuxelus.energycontrol.items.cards.MainCardItem;
import com.zuxelus.energycontrol.items.cards.TextItemCard;
import com.zuxelus.energycontrol.network.NetworkHelper;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class InfoPanelScreen extends ContainerScreen<InfoPanelContainer> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_info_panel.png");

	public ItemStack prevCard;
	protected TextFieldWidget textboxTitle;
	protected byte activeTab;
	protected boolean modified;
	public boolean isColored;

	public InfoPanelScreen(InfoPanelContainer container, PlayerInventory playerInventory) {
		super(container, playerInventory, new TranslatableText(ModItems.INFO_PANEL_TRANSLATION_KEY));
		containerHeight = 201;
		modified = false;
		isColored = !container.be.getColored();
		prevCard = ItemStack.EMPTY;
		activeTab = 0;
	}

	protected void initControls() {
		ItemStack stack = container.be.getCards().get(0);
		if (stack.equals(prevCard) && container.be.getColored() == isColored)
			return;

		buttons.clear();
		children.clear();
		prevCard = stack;
		isColored = container.be.getColored();
		addButton(new GuiInfoPanelShowLabels(x + containerWidth - 25, y + 42, container.be));
		int delta = 0;
		if (isColored) {
			addButton(new CompactButton(x + containerWidth - 25, y + 55, 18, 12, "T", (buttonWidget) -> { actionPerformed(112); }));
			delta = 15;
		}
		if (!stack.isEmpty() && stack.getItem() instanceof MainCardItem) {
			int slot = container.be.getCardSlot(stack);
			if (stack.getItem() instanceof TextItemCard)
				addButton(new CompactButton(x + containerWidth - 25, y + 55 + delta, 18, 12, "...", (buttonWidget) -> { actionPerformed(111); }));
			List<PanelSetting> settingsList = ((MainCardItem) stack.getItem()).getSettingsList();

			int hy = font.fontHeight + 1;
			int yy = 1;
			int xx = x + 24;
			if (settingsList != null)
				for (PanelSetting panelSetting : settingsList) {
					addButton(new GuiInfoPanelCheckBox(xx + 4, y + 28 + hy * yy, panelSetting, container.be, slot, font));
					yy++;
				}
			if (!modified) {
				textboxTitle = new TextFieldWidget(font, 7, 16, 162, 18, null, "");
				textboxTitle.setSelected(true);
				textboxTitle.setText(new ItemCardReader(stack).getTitle());
				setInitialFocus(textboxTitle);
				children.add(textboxTitle);
			}
		} else {
			modified = false;
			textboxTitle = null;
		}
	}

	@Override
	public void init() {
		super.init();
		initControls();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		super.render(mouseX, mouseY, delta);
		drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		font.draw(title.asFormattedString(), (containerWidth - font.getStringWidth(title.asFormattedString())) / 2, 6, 0x404040);
		if (textboxTitle != null)
			textboxTitle.renderButton(mouseX, mouseY, 0);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int left = (width - containerWidth) / 2;
		int top = (height - containerHeight) / 2;
		blit(left, top, 0, 0, containerWidth, containerHeight);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (textboxTitle != null) {
			textboxTitle.mouseClicked(mouseX - x, mouseY - y, button);
			if (textboxTitle.isFocused()) {
				focusOn(textboxTitle);
				return true;
			}
			focusOn(null);
			updateTitle();
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void tick() {
		super.tick();
		if (textboxTitle != null)
			textboxTitle.tick();
		initControls();
	}

	protected void updateTitle() {
		if (textboxTitle == null)
			return;
		if (container.be.getWorld().isClient) {
			CompoundTag tag = new CompoundTag();
			tag.putInt("type", 4);
			tag.putInt("slot", activeTab);
			tag.putString("title", textboxTitle.getText());
			NetworkHelper.updateSeverTileEntity(container.be.getPos(), tag);
			ItemStack card = container.be.getInvStack(activeTab);
			if (!card.isEmpty() && card.getItem() instanceof MainCardItem)
				new ItemCardReader(card).setTitle(textboxTitle.getText());
		}
	}

	@Override
	public void removed() {
		updateTitle();
		super.removed();
	}

	protected void actionPerformed(int id) {
		if (id == 112) { // color upgrade
			Screen colorGui = new ColorGuiScreen(this, container.be);
			minecraft.openScreen(colorGui);
		} else if (id == 111) {
			ItemStack card = container.be.getCards().get(0);
			if (card.isEmpty())
				return;
			if (card.getItem() instanceof TextItemCard) {
				ItemCardReader reader = new ItemCardReader(card);
				ICardGui guiObject = ((TextItemCard) card.getItem()).getSettingsScreen(reader);
				if (!(guiObject instanceof Screen)) {
					EnergyControl.LOGGER.warn("Invalid card, getSettingsScreen method should return GuiScreen object");
					return;
				}
				Screen gui = (Screen) guiObject;
				ItemCardSettingsReader wrapper = new ItemCardSettingsReader(card, container.be, this, (byte) 0);
				((ICardGui) gui).setCardSettingsHelper(wrapper);
				minecraft.openScreen(gui);
			}
		}
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		if (keyCode == 13) {
			updateTitle();
			return true;
		}
		return super.charTyped(chr, keyCode);
	}

	/*@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (textboxTitle != null && textboxTitle.isFocused()) {
			if (keyCode == 256) {
				this.minecraft.player.closeContainer();
				return true;
			}
			if (keyCode == 257) {
				updateTitle();
				return true;
			}
			modified = true;
			if (textboxTitle.keyPressed(keyCode, scanCode, modifiers))
				return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}*/
}
