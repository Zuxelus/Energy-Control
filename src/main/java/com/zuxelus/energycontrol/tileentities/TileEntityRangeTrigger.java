package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControlConfig;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.blocks.RangeTrigger;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityRangeTrigger extends TileEntityInventory implements ITickable, ISlotItemFilter, ITilePacketHandler {

	public static final int SLOT_CARD = 0;
	public static final int SLOT_UPGRADE = 1;

	private static final int STATE_UNKNOWN = 0;
	private static final int STATE_PASSIVE = 1;
	private static final int STATE_ACTIVE = 2;

	protected int updateTicker;
	protected int tickRate;
	protected boolean init;

	private int status;
	private boolean poweredBlock;
	private boolean invertRedstone;
	public double levelStart;
	public double levelEnd;

	public TileEntityRangeTrigger() {
		super("tile.range_trigger.name");
		init = false;
		tickRate = EnergyControlConfig.rangeTriggerRefreshPeriod;
		updateTicker = tickRate;
		status = -1;
		invertRedstone = false;
		levelStart = 0;
		levelEnd = 40000;
	}

	public boolean getInvertRedstone() {
		return invertRedstone;
	}

	public void setInvertRedstone(boolean value) {
		boolean old = invertRedstone;
		invertRedstone = value;
		if (!world.isRemote && invertRedstone != old)
			notifyBlockUpdate();
	}

	public void setStatus(int value) {
		int old = status;
		status = value;
		if (!world.isRemote && status != old) {
			IBlockState iblockstate = world.getBlockState(pos);
			Block block = iblockstate.getBlock();
			if (block instanceof RangeTrigger) {
				IBlockState newState = block.getDefaultState()
						.withProperty(BlockHorizontal.FACING, iblockstate.getValue(BlockHorizontal.FACING))
						.withProperty(RangeTrigger.STATE, RangeTrigger.EnumState.getState(status));
				world.setBlockState(pos, newState, 3);
			}
			notifyBlockUpdate();
		}
	}

	public void setLevelStart(double start) {
		if (!world.isRemote && levelStart != start)
			notifyBlockUpdate();
		levelStart = start;
	}

	public void setLevelEnd(double end) {
		if (!world.isRemote && levelEnd != end)
			notifyBlockUpdate();
		levelEnd = end;
	}

	public int getStatus() {
		return status;
	}

	public boolean getPowered() {
		return poweredBlock;
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			if (updateTicker-- > 0)
				return;
			updateTicker = tickRate;
			markDirty();
		}
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("value"))
				setLevelStart(tag.getDouble("value"));
			break;
		case 2:
			if (tag.hasKey("value"))
				setInvertRedstone(tag.getInteger("value") == 1);
			break;
		case 3:
			if (tag.hasKey("value"))
				setLevelEnd(tag.getDouble("value"));
			break;
		}
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) { }

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		tag.setBoolean("poweredBlock", poweredBlock);
		return new SPacketUpdateTileEntity(getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readProperties(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag = writeProperties(tag);
		tag.setBoolean("poweredBlock", poweredBlock);
		return tag;
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		invertRedstone = tag.getBoolean("invert");
		levelStart = tag.getDouble("levelStart");
		levelEnd = tag.getDouble("levelEnd");
		if (tag.hasKey("poweredBlock"))
			poweredBlock = tag.getBoolean("poweredBlock");
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setBoolean("invert", invertRedstone);
		tag.setDouble("levelStart", levelStart);
		tag.setDouble("levelEnd", levelEnd);
		return tag;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (world == null || world.isRemote)
			return;

		int status = STATE_UNKNOWN;
		ItemStack card = getStackInSlot(SLOT_CARD);
		if (ItemCardMain.isCard(card)) {
			ItemCardReader reader = new ItemCardReader(card);
			CardState state = ItemCardMain.updateCardNBT(card, world, pos, reader, getStackInSlot(SLOT_UPGRADE));
			if (state == CardState.OK) {
				double min = Math.min(levelStart, levelEnd);
				double max = Math.max(levelStart, levelEnd);
				double cur = reader.getDouble("storage");

				if (cur > max) {
					status = STATE_ACTIVE;
				} else if (cur < min) {
					status = STATE_PASSIVE;
				} else if (status == STATE_UNKNOWN) {
					status = STATE_PASSIVE;
				} else
					status = STATE_PASSIVE;
			} else
				status = STATE_UNKNOWN;
		}
		setStatus(status);
	}

	public void notifyBlockUpdate() {
		IBlockState iblockstate = world.getBlockState(pos);
		Block block = iblockstate.getBlock();
		if (!(block instanceof RangeTrigger))
			return;
		boolean newValue = status >= 1 && (status == 1 != invertRedstone);
		if (poweredBlock != newValue) {
			poweredBlock = newValue;
			world.notifyNeighborsOfStateChange(pos, block, false);
		}
		world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
	}

	// Inventory
	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int slotIndex, ItemStack stack) { // ISlotItemFilter
		if (slotIndex == SLOT_CARD) {
			return stack.getItem() instanceof ItemCardMain && (stack.getItemDamage() == ItemCardType.CARD_ENERGY
					|| stack.getItemDamage() == ItemCardType.CARD_ENERGY_ARRAY);
		}
		return stack.getItem() instanceof ItemUpgrade && stack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE;
	}
}
