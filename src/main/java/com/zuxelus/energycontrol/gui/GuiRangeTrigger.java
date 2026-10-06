package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerRangeTrigger;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiRangeTriggerInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class GuiRangeTrigger extends GuiContainerBase<ContainerRangeTrigger> {
	private static final Identifier TEXTURE = Identifier.parse(EnergyControl.MODID + ":textures/gui/gui_range_trigger.png");

	private ContainerRangeTrigger container;
	private ItemStack prevCard;

	public GuiRangeTrigger(ContainerRangeTrigger container, Inventory inventory, Component title) {
		super(container, inventory, title, TEXTURE, DEFAULT_IMAGE_WIDTH, 190);
		this.container = container;
	}

	private void initControls() {
		ItemStack card = container.getSlot(TileEntityRangeTrigger.SLOT_CARD).getItem();
		if (!card.isEmpty() && card.equals(prevCard))
			return;
		clearWidgets();
		prevCard = card;
		// ten digits, up to 10 billions
		for (int i = 0; i < 10; i++) {
			addRenderableWidget(new CompactButton(i * 10, leftPos + 30 + i * 12 + (i + 2) / 3 * 6, topPos + 20, 12, 12, Component.literal("-"), (button) -> { actionPerformed(button); }));
			addRenderableWidget(new CompactButton(i * 10 + 1, leftPos + 30 + i * 12 + (i + 2) / 3 * 6, topPos + 42, 12, 12, Component.literal("+"), (button) -> { actionPerformed(button); }));
		}
		for (int i = 0; i < 10; i++) {
			addRenderableWidget(new CompactButton(100 + i * 10, leftPos + 30 + i * 12 + (i + 2) / 3 * 6, topPos + 57, 12, 12, Component.literal("-"), (button) -> { actionPerformed(button); }));
			addRenderableWidget(new CompactButton(100 + i * 10 + 1, leftPos + 30 + i * 12 + (i + 2) / 3 * 6, topPos + 79, 12, 12, Component.literal("+"), (button) -> { actionPerformed(button); }));
		}
		addRenderableWidget(new GuiRangeTriggerInvertRedstone(leftPos + 8, topPos + 62, container.te));
	}

	@Override
	public void init() {
		super.init();
		initControls();
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(context, mouseX, mouseY, partialTicks);
		extractTooltip(context, mouseX, mouseY);
	}

	private void renderValue(GuiGraphicsExtractor context, double value, int x, int y) {
		x += 114;
		for (int i = 0; i < 10; i++) {
			byte digit = (byte) (value % 10);
			String str = Byte.toString(digit);
			context.text(font, str, x - 12 * i - font.width("0") / 2 + (9 - i + 2) / 3 * 6, y, 0xFF404040, false);
			value /= 10;
		}
	}

	protected void actionPerformed(Button button) {
		int id = ((CompactButton) button).getId();
		boolean isPlus = id % 2 == 1;
		id /= 10;
		int power = 9 - (id % 10);
		id /= 10;
		boolean isEnd = id % 2 == 1;
		double initValue = isEnd ? container.te.levelEnd : container.te.levelStart;
		double newValue = initValue;
		double delta = (long) Math.pow(10, power);
		double digit = (initValue / delta) % 10;

		if (isPlus && digit < 9)
			newValue += delta;
		else if (!isPlus && digit > 0)
			newValue -= delta;

		if (newValue != initValue) {
			TileEntityRangeTrigger trigger = container.te;

			CompoundTag tag = new CompoundTag();
			tag.putDouble("value", newValue);
			if (isEnd) {
				tag.putInt("type", 3);
				NetworkHelper.updateSeverTileEntity(trigger.getBlockPos(), tag);
				trigger.setLevelEnd(newValue);
			} else {
				tag.putInt("type", 1);
				NetworkHelper.updateSeverTileEntity(trigger.getBlockPos(), tag);
				trigger.setLevelStart(newValue);
			}
		}
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor context, int mouseX, int mouseY) {
		drawCenteredText(context, title, imageWidth, 6);
		drawLeftAlignedText(context, I18n.get("container.inventory"), 8, (imageHeight - 96) + 2);

		renderValue(context, container.te.levelStart, 30, 33);
		renderValue(context, container.te.levelEnd, 30, 70);
	}
}
