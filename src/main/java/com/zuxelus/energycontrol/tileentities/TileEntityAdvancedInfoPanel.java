package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.zlib.blocks.FacingBlockActive;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class TileEntityAdvancedInfoPanel extends TileEntityInfoPanel {
	public static final String NAME = "info_panel_advanced";
	private static final byte SLOT_CARD1 = 0;
	private static final byte SLOT_CARD2 = 1;
	private static final byte SLOT_CARD3 = 2;
	private static final byte SLOT_UPGRADE_RANGE = 3;
	
	public static final int POWER_REDSTONE = 0;
	public static final int POWER_INVERTED = 1;
	public static final int POWER_ON = 2;
	public static final int POWER_OFF = 3;

	public static final int OFFSET_THICKNESS = 100;
	public static final int OFFSET_ROTATE_HOR = 200;
	public static final int OFFSET_ROTATE_VERT = 300;

	public static final int DEFAULT_TEXT = -1;
	public static final int DEFAULT_BACKGROUND = 8;

	public byte powerMode;
	public byte thickness;
	public byte rotateHor;
	public byte rotateVert;

	public TileEntityAdvancedInfoPanel() {
		super();
		customName = "tile." + NAME + ".name";
		colorText = DEFAULT_TEXT;
		colorBackground = DEFAULT_BACKGROUND;
		colored = true;
		thickness = 16;
	}

	public byte getPowerMode() {
		return powerMode;
	}

	public void setPowerMode(byte mode) {
		powerMode = mode;
		if (world != null && !world.isRemote)
			updateBlockState(world.getBlockState(pos));
	}

	public byte getNextPowerMode() {
		switch (powerMode) {
		case POWER_REDSTONE:
			return POWER_INVERTED;
		case POWER_INVERTED:
			return POWER_ON;
		case POWER_ON:
			return POWER_OFF;
		case POWER_OFF:
			return POWER_REDSTONE;
		}
		return POWER_REDSTONE;
	}

	public void setValues(int i) {
		if (i >= 0 && i < 100) {
			switch (i) {
			case POWER_ON:
			case POWER_OFF:
			case POWER_REDSTONE:
			case POWER_INVERTED:
				powerMode = (byte) i;
			}
		} else if (i >= OFFSET_THICKNESS && i < OFFSET_THICKNESS + 100) {
			i -= OFFSET_THICKNESS;
			thickness = (byte) i;
		} else if (i >= OFFSET_ROTATE_HOR && i < OFFSET_ROTATE_HOR + 100) {
			i -= OFFSET_ROTATE_HOR + 8;
			i = -(i * 7);
			rotateHor = (byte) i;
		} else if (i >= OFFSET_ROTATE_VERT && i < OFFSET_ROTATE_VERT + 100) {
			i -= OFFSET_ROTATE_VERT + 8;
			i = -(i * 7);
			rotateVert = (byte) i;
		}
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		int type = tag.getInteger("type");
		if (type < 10) {
			super.onServerMessageReceived(tag);
			return;
		}
		switch (type) {
		case 10:
			setValues(tag.getInteger("value"));
			break;
		case 11:
			setPowerMode((byte) tag.getInteger("value"));
			break;
		}
	}

	@Override
	protected void deserializeDisplaySettings(NBTTagCompound tag) {
		deserializeSlotSettings(tag, "dSettings1", SLOT_CARD1);
		deserializeSlotSettings(tag, "dSettings2", SLOT_CARD2);
		deserializeSlotSettings(tag, "dSettings3", SLOT_CARD3);
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		if (tag.hasKey("powerMode"))
			setPowerMode(tag.getByte("powerMode"));
		if (tag.hasKey("thickness"))
			thickness = tag.getByte("thickness");
		if (tag.hasKey("rotateHor"))
			rotateHor = tag.getByte("rotateHor");
		if (tag.hasKey("rotateVert"))
			rotateVert = tag.getByte("rotateVert");
	}

	@Override
	protected void serializeDisplaySettings(NBTTagCompound tag) {
		tag.setTag("dSettings1", serializeSlotSettings(SLOT_CARD1));
		tag.setTag("dSettings2", serializeSlotSettings(SLOT_CARD2));
		tag.setTag("dSettings3", serializeSlotSettings(SLOT_CARD3));
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setByte("powerMode", powerMode);
		tag.setByte("thickness", thickness);
		tag.setByte("rotateHor", rotateHor);
		tag.setByte("rotateVert", rotateVert);
		return tag;
	}

	@Override
	public NonNullList<ItemStack> getCards() {
		NonNullList<ItemStack> data = NonNullList.create();
		data.add(getStackInSlot(SLOT_CARD1));
		data.add(getStackInSlot(SLOT_CARD2));
		data.add(getStackInSlot(SLOT_CARD3));
		return data;
	}

	@Override
	public boolean isColoredEval() {
		return true;
	}

	@Override
	public byte getSlotUpgradeRange() {
		return SLOT_UPGRADE_RANGE;
	}

	@Override
	public boolean isCardSlot(int slot) {
		return slot == SLOT_CARD1 || slot == SLOT_CARD2 || slot == SLOT_CARD3;
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD1:
		case SLOT_CARD2:
		case SLOT_CARD3:
			return ItemCardMain.isCard(stack);
		case SLOT_UPGRADE_RANGE:
			return stack.getItem() instanceof ItemUpgrade && stack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE;
		default:
			return false;
		}
	}

	@Override
	public void updateBlockState(IBlockState state) {
		boolean flag = world.isBlockPowered(pos);
		switch (powerMode) {
		case POWER_ON:
			flag = true;
			break;
		case POWER_OFF:
			flag = false;
			break;
		case POWER_REDSTONE:
			break;
		case POWER_INVERTED:
			flag = !flag;
			break;
		}

		powered = flag;
		if (flag == state.getValue(FacingBlockActive.ACTIVE))
			return;

		if (flag)
			world.scheduleUpdate(pos, state.getBlock(), 4);
		else {
			world.setBlockState(pos, state.cycleProperty(FacingBlockActive.ACTIVE), 2);
			updateExtenders(true);
		}
	}

	@Override
	public boolean runTouchAction(ItemStack stack, BlockPos pos, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return false;
		ItemStack card = getStackInSlot(SLOT_CARD1);
		runTouchAction(this, card, stack, SLOT_CARD1, false);
		return true;
	}
}
