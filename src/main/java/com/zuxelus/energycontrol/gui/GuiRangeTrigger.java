package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerRangeTrigger;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiRangeTriggerInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiRangeTrigger extends GuiContainerBase<ContainerRangeTrigger> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/gui/gui_range_trigger.png");

	private ContainerRangeTrigger container;
	private ItemStack prevCard;

	public GuiRangeTrigger(ContainerRangeTrigger container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title, TEXTURE);
		this.container = container;
		imageHeight = 190;
	}

	private void initControls() {
		ItemStack card = container.getSlot(TileEntityRangeTrigger.SLOT_CARD).getItem();
		if (!card.isEmpty() && card.equals(prevCard))
			return;
		buttons.clear();
		prevCard = card;
		// ten digits, up to 10 billions
		for (int i = 0; i < 10; i++) {
			addButton(new CompactButton(i * 10, leftPos + 30 + i * 12 + (i + 2) / 3 * 6, topPos + 20, 12, 12, new StringTextComponent("-"), (button) -> { actionPerformed(button); }));
			addButton(new CompactButton(i * 10 + 1, leftPos + 30 + i * 12 + (i + 2) / 3 * 6, topPos + 42, 12, 12, new StringTextComponent("+"), (button) -> { actionPerformed(button); }));
		}
		for (int i = 0; i < 10; i++) {
			addButton(new CompactButton(100 + i * 10, leftPos + 30 + i * 12 + (i + 2) / 3 * 6, topPos + 57, 12, 12, new StringTextComponent("-"), (button) -> { actionPerformed(button); }));
			addButton(new CompactButton(100 + i * 10 + 1, leftPos + 30 + i * 12 + (i + 2) / 3 * 6, topPos + 79, 12, 12, new StringTextComponent("+"), (button) -> { actionPerformed(button); }));
		}
		addButton(new GuiRangeTriggerInvertRedstone(leftPos + 8, topPos + 62, container.te));
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
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	private void renderValue(MatrixStack matrixStack, double value, int x, int y) {
		x += 114;
		for (int i = 0; i < 10; i++) {
			byte digit = (byte) (value % 10);
			String str = Byte.toString(digit);
			font.draw(matrixStack, str, x - 12 * i - font.width("0") / 2 + (9 - i + 2) / 3 * 6, y, 0x404040);
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

			CompoundNBT tag = new CompoundNBT();
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
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, imageWidth, 6);
		drawLeftAlignedText(matrixStack, I18n.get("container.inventory"), 8, (imageHeight - 96) + 2);

		renderValue(matrixStack, container.te.levelStart, 30, 33);
		renderValue(matrixStack, container.te.levelEnd, 30, 70);
	}
}
