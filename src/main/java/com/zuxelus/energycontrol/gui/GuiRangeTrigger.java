package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerRangeTrigger;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiRangeTriggerInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRangeTrigger extends GuiContainerBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/gui/gui_range_trigger.png");

	private ContainerRangeTrigger container;
	private ItemStack prevCard;

	public GuiRangeTrigger(ContainerRangeTrigger container) {
		super(container, "tile.range_trigger.name", TEXTURE);
		this.container = container;
		ySize = 190;
	}

	private void initControls() {
		ItemStack card = container.getSlot(TileEntityRangeTrigger.SLOT_CARD).getStack();
		if (!card.isEmpty() && card.equals(prevCard))
			return;
		buttonList.clear();
		prevCard = card;
		// ten digits, up to 10 billions
		for (int i = 0; i < 10; i++) {
			addButton(new CompactButton(i * 10, guiLeft + 30 + i * 12 + (i + 2) / 3 * 6, guiTop + 20, 12, 12, "-"));
			addButton(new CompactButton(i * 10 + 1, guiLeft + 30 + i * 12 + (i + 2) / 3 * 6, guiTop + 42, 12, 12, "+"));
		}
		for (int i = 0; i < 10; i++) {
			addButton(new CompactButton(100 + i * 10, guiLeft + 30 + i * 12 + (i + 2) / 3 * 6, guiTop + 57, 12, 12, "-"));
			addButton(new CompactButton(100 + i * 10 + 1, guiLeft + 30 + i * 12 + (i + 2) / 3 * 6, guiTop + 79, 12, 12, "+"));
		}
		addButton(new GuiRangeTriggerInvertRedstone(10, guiLeft + 8, guiTop + 62, container.te));
	}

	@Override
	public void initGui() {
		super.initGui();
		initControls();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	private void renderValue(double value, int x, int y) {
		x += 114;
		for (int i = 0; i < 10; i++) {
			byte digit = (byte) (value % 10);
			String str = Byte.toString(digit);
			fontRenderer.drawString(str, x - 12 * i - fontRenderer.getCharWidth(str.charAt(0)) / 2 + (9 - i + 2) / 3 * 6, y, 0x404040);
			value /= 10;
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		int id = button.id;
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
			
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("value", newValue);
			if (isEnd) {
				tag.setInteger("type", 3);
				NetworkHelper.updateSeverTileEntity(trigger.getPos(), tag);
				trigger.setLevelEnd(newValue);
			} else {
				tag.setInteger("type", 1);
				NetworkHelper.updateSeverTileEntity(trigger.getPos(), tag);
				trigger.setLevelStart(newValue);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(name, xSize, 6);
		drawLeftAlignedText(I18n.format("container.inventory"), 8, (ySize - 96) + 2);

		renderValue(container.te.levelStart, 30, 33);
		renderValue(container.te.levelEnd, 30, 70);
	}
}
