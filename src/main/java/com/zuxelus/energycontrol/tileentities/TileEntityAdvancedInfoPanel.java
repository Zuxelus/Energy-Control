package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.containers.ContainerAdvancedInfoPanel;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
//import com.zuxelus.energycontrol.items.cards.ItemCardAppEngInv;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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

	public byte powerMode;
	public byte thickness;
	public byte rotateHor;
	public byte rotateVert;

	public TileEntityAdvancedInfoPanel(TileEntityType<?> type) {
		super(type);
		colorBackground = 6;
		colored = true;
		thickness = 16;
	}

	public TileEntityAdvancedInfoPanel() {
		this(ModTileEntityTypes.info_panel_advanced.get());
	}

	public byte getPowerMode() {
		return powerMode;
	}

	public void setPowerMode(byte mode) {
		powerMode = mode;
		if (world != null && !world.isRemote)
			calcPowered();
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

	@Override
	protected void calcPowered() { //server
		boolean newPowered = world.isBlockPowered(pos);
		switch (powerMode) {
		case POWER_ON:
			newPowered = true;
			break;
		case POWER_OFF:
			newPowered = false;
			break;
		case POWER_REDSTONE:
			break;
		case POWER_INVERTED:
			newPowered = !newPowered;
			break;
		}
		if (newPowered != powered) {
			powered = newPowered;
			if (screen != null)
				screen.turnPower(powered, world);
		}
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
	public void onServerMessageReceived(CompoundNBT tag) {
		if (!tag.contains("type"))
			return;
		int type = tag.getInt("type");
		if (type < 10) {
			super.onServerMessageReceived(tag);
			return;
		}
		switch (type) {
		case 10:
			setValues(tag.getInt("value"));
			break;
		case 11:
			setPowerMode((byte) tag.getInt("value"));
			break;
		}
	}

	@Override
	protected void deserializeDisplaySettings(CompoundNBT tag) {
		deserializeSlotSettings(tag, "dSettings1", SLOT_CARD1);
		deserializeSlotSettings(tag, "dSettings2", SLOT_CARD2);
		deserializeSlotSettings(tag, "dSettings3", SLOT_CARD3);
	}

	@Override
	protected void readProperties(CompoundNBT tag) {
		super.readProperties(tag);
		if (tag.contains("powerMode"))
			setPowerMode(tag.getByte("powerMode"));
		if (tag.contains("thickness"))
			thickness = tag.getByte("thickness");
		if (tag.contains("rotateHor"))
			rotateHor = tag.getByte("rotateHor");
		if (tag.contains("rotateVert"))
			rotateVert = tag.getByte("rotateVert");
	}

	@Override
	protected void serializeDisplaySettings(CompoundNBT tag) {
		tag.put("dSettings1", serializeSlotSettings(SLOT_CARD1));
		tag.put("dSettings2", serializeSlotSettings(SLOT_CARD2));
		tag.put("dSettings3", serializeSlotSettings(SLOT_CARD3));
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);
		tag.putByte("powerMode", powerMode);
		tag.putByte("thickness", thickness);
		tag.putByte("rotateHor", rotateHor);
		tag.putByte("rotateVert", rotateVert);
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
			return stack.getItem().equals(ModItems.upgrade_range.get());
		default:
			return false;
		}
	}

	@Override
	public boolean runTouchAction(ItemStack stack, BlockPos pos, Vec3d hit) {
		if (world.isRemote)
			return false;
		ItemStack card = getStackInSlot(SLOT_CARD1);
		runTouchAction(this, card, stack, SLOT_CARD1, false);
		return true;
	}

	// INamedContainerProvider
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerAdvancedInfoPanel(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModItems.info_panel_advanced.get().getTranslationKey());
	}
}
