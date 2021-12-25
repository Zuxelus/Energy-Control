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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiRangeTrigger extends GuiContainerBase<ContainerRangeTrigger> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID + ":textures/gui/gui_range_trigger.png");

	private ContainerRangeTrigger container;
	private ItemStack prevCard;

	public GuiRangeTrigger(ContainerRangeTrigger container, PlayerInventory inventory, Text title) {
		super(container, inventory, title, TEXTURE);
		this.container = container;
		backgroundHeight = 190;
	}

	private void initControls() {
		ItemStack card = container.getSlot(TileEntityRangeTrigger.SLOT_CARD).getStack();
		if (!card.isEmpty() && card.equals(prevCard))
			return;
		clearChildren();
		prevCard = card;
		// ten digits, up to 10 billions
		for (int i = 0; i < 10; i++) {
			addDrawableChild(new CompactButton(i * 10, x + 30 + i * 12 + (i + 2) / 3 * 6, y + 20, 12, 12, new LiteralText("-"), (button) -> { actionPerformed(button); }));
			addDrawableChild(new CompactButton(i * 10 + 1, x + 30 + i * 12 + (i + 2) / 3 * 6, y + 42, 12, 12, new LiteralText("+"), (button) -> { actionPerformed(button); }));
		}
		for (int i = 0; i < 10; i++) {
			addDrawableChild(new CompactButton(100 + i * 10, x + 30 + i * 12 + (i + 2) / 3 * 6, y + 57, 12, 12, new LiteralText("-"), (button) -> { actionPerformed(button); }));
			addDrawableChild(new CompactButton(100 + i * 10 + 1, x + 30 + i * 12 + (i + 2) / 3 * 6, y + 79, 12, 12, new LiteralText("+"), (button) -> { actionPerformed(button); }));
		}
		addDrawableChild(new GuiRangeTriggerInvertRedstone(x + 8, y + 62, container.te));
	}

	@Override
	public void init() {
		super.init();
		initControls();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		drawMouseoverTooltip(matrixStack, mouseX, mouseY);
	}

	private void renderValue(MatrixStack matrixStack, double value, int x, int y) {
		x += 114;
		for (int i = 0; i < 10; i++) {
			byte digit = (byte) (value % 10);
			String str = Byte.toString(digit);
			textRenderer.draw(matrixStack, str, x - 12 * i - textRenderer.getWidth("0") / 2 + (9 - i + 2) / 3 * 6, y, 0x404040);
			value /= 10;
		}
	}

	protected void actionPerformed(ButtonWidget button) {
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

			NbtCompound tag = new NbtCompound();
			tag.putDouble("value", newValue);
			if (isEnd) {
				tag.putInt("type", 3);
				NetworkHelper.updateSeverTileEntity(trigger.getPos(), tag);
				trigger.setLevelEnd(newValue);
			} else {
				tag.putInt("type", 1);
				NetworkHelper.updateSeverTileEntity(trigger.getPos(), tag);
				trigger.setLevelStart(newValue);
			}
		}
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, backgroundWidth, 6);
		drawLeftAlignedText(matrixStack, I18n.translate("container.inventory"), 8, (backgroundHeight - 96) + 2);

		renderValue(matrixStack, container.te.levelStart, 30, 33);
		renderValue(matrixStack, container.te.levelEnd, 30, 70);
	}
}
