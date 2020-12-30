package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.IBlockHorizontal;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;

public class TileEntityRangeTrigger extends TileEntityInventory implements ISlotItemFilter, ITilePacketHandler, IBlockHorizontal {

	public static final int SLOT_CARD = 0;
	public static final int SLOT_UPGRADE = 1;
	private static final int LOCATION_RANGE = 8;

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
		tickRate = EnergyControl.config.rangeTriggerRefreshPeriod;
		updateTicker = tickRate;
		status = 0;
		invertRedstone = false;
		levelStart = 10000000;
		levelEnd = 9000000;
	}

	public boolean getInvertRedstone() {
		return invertRedstone;
	}

	public void setInvertRedstone(boolean value) {
		if (!worldObj.isRemote && invertRedstone != value)
			notifyBlockUpdate();
		invertRedstone = value;
	}

	public void setStatus(int value) {
		if (!worldObj.isRemote && status != value)
			notifyBlockUpdate();
		status = value;
	}

	public void setLevelStart(double start) {
		if (!worldObj.isRemote && levelStart != start)
			notifyBlockUpdate();
		levelStart = start;
	}

	public void setLevelEnd(double end) {
		if (!worldObj.isRemote && levelEnd != end)
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
	public void updateEntity() {
		if (!worldObj.isRemote) {
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
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		tag.setInteger("status", status);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		if (!worldObj.isRemote)
			return;
		readProperties(pkt.func_148857_g());
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		invertRedstone = tag.getBoolean("invert");
		levelStart = tag.getDouble("levelStart");
		levelEnd = tag.getDouble("levelEnd");
		if (tag.hasKey("status")) {
			int old = status;
			status = tag.getInteger("status");
			if (worldObj.isRemote && status != old)
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
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
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeProperties(tag);
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return oldBlock != newBlock;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (worldObj == null || worldObj.isRemote)
			return;
		
		int status = STATE_UNKNOWN;
		ItemStack card = getStackInSlot(SLOT_CARD);
		if (card != null) {
			Item item = card.getItem();
			if (item instanceof ItemCardMain) {
				ItemCardReader reader = new ItemCardReader(card);
				CardState state = ItemCardMain.updateCardNBT(worldObj, xCoord, yCoord, zCoord, reader, getStackInSlot(SLOT_UPGRADE));
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
		}
		setStatus(status);
	}

	public void notifyBlockUpdate() {
		Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
		boolean newValue = status == 2 ? !invertRedstone : invertRedstone;
		if (poweredBlock != newValue)
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, block);
		poweredBlock = newValue;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
	public boolean isItemValid(int slotIndex, ItemStack itemstack) { // ISlotItemFilter
		switch (slotIndex) {
		case SLOT_CARD:
			return itemstack.getItem() instanceof ItemCardMain && (itemstack.getItemDamage() == ItemCardType.CARD_ENERGY
					|| itemstack.getItemDamage() == ItemCardType.CARD_ENERGY_ARRAY);
		default:
			return itemstack.getItem() instanceof ItemUpgrade && itemstack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE;
		}
	}
}
