package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.IBlockHorizontal;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.info.Info;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityKitAssembler extends TileEntityInventory implements ITilePacketHandler, ISlotItemFilter, IEnergySink, IBlockHorizontal {
	public static final byte SLOT_INFO = 0;
	public static final byte SLOT_CARD1 = 1;
	public static final byte SLOT_ITEM = 2;
	public static final byte SLOT_CARD2 = 3;
	public static final byte SLOT_KIT = 4;
	private static final double CONSUMPTION = 1;
	private static final double PRODUCTION_TIME = 60;
	private double maxStorage;
	private double energy;
	private double production;
	private boolean addedToEnet;
	private boolean active;

	public TileEntityKitAssembler() {
		super("tile.kit_assembler.name");
		addedToEnet = false;
		active = false;
		maxStorage = 1000;
		energy = 0;
		production = 0;
	}

	public double getEnergy() {
		return energy;
	}

	public int getEnergyFactor() {
		return (int) Math.round(energy / maxStorage * 14);
	}

	public double getProduction() {
		return production;
	}

	public int getProductionFactor() {
		return (int) Math.round(production / PRODUCTION_TIME * 24);
	}

	public boolean getActive() {
		return active;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 4:
			if (tag.hasKey("slot") && tag.hasKey("title")) {
				ItemStack itemStack = getStackInSlot(tag.getInteger("slot"));
				if (itemStack != null && itemStack.getItem() instanceof ItemCardMain) {
					new ItemCardReader(itemStack).setTitle(tag.getString("title"));
				}
			}
			break;
		}
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("energy") && tag.hasKey("production")) {
				energy = tag.getDouble("energy");
				production = tag.getDouble("production");
			}
			break;
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		tag.setBoolean("active", active);
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
		if (tag.hasKey("energy"))
			energy = tag.getDouble("energy");
		if (tag.hasKey("production"))
			production = tag.getDouble("production");
		if (tag.hasKey("active")) {
			boolean old = active;
			active = tag.getBoolean("active");
			if (worldObj.isRemote && active != old)
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
		tag.setDouble("energy", energy);
		tag.setDouble("production", production);
		return tag;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeProperties(tag);
	}

	public void onLoad() {
		if (!addedToEnet && !worldObj.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToEnet = true;
			updateActive();
		}
	}

	@Override
	public void invalidate() {
		onChunkUnload();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		if (addedToEnet && !worldObj.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnet = false;
		}
	}

	@Override
	public void updateEntity() {
		if (!addedToEnet)
			onLoad();
		if (worldObj.isRemote || !active)
			return;
		if (energy >= CONSUMPTION) {
			energy -= CONSUMPTION;
			production += 1;
			if (production >= PRODUCTION_TIME) {
				ItemStack stack1 = getStackInSlot(SLOT_CARD1);
				ItemStack stack2 = getStackInSlot(SLOT_CARD2);
				ItemStack kit = getStackInSlot(SLOT_KIT);
				if (stack1 != null && stack1.getItem() instanceof ItemCardMain && stack2 != null
						&& stack2.getItem() instanceof ItemCardMain && stack1.getItemDamage() == stack2.getItemDamage()
						&& kit == null) {
					int kit_damage = ItemCardMain.getKitFromCard(stack1.getItemDamage());
					getStackInSlotOnClosing(SLOT_CARD1);
					getStackInSlotOnClosing(SLOT_CARD2);
					if (kit_damage != -1)
						setInventorySlotContents(SLOT_KIT, new ItemStack(ModItems.itemKit, 2, kit_damage));
				}
				production = 0;
				updateState();
			}
		} else {
			energy = 0;
			production = 0;
			updateState();
		}
	}

	public void notifyBlockUpdate() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (worldObj == null || worldObj.isRemote)
			return;
		updateState();
	}

	private void updateActive() {
		active = false;
		if (energy < CONSUMPTION)
			return;
		ItemStack stack1 = getStackInSlot(SLOT_CARD1);
		if (stack1 == null || !(stack1.getItem() instanceof ItemCardMain))
			return;
		ItemStack stack2 = getStackInSlot(SLOT_CARD2);
		if (stack2 == null || !(stack2.getItem() instanceof ItemCardMain)
				|| stack1.getItemDamage() != stack2.getItemDamage()
				|| ItemCardMain.getKitFromCard(stack1.getItemDamage()) == -1)
			return;
		ItemStack kit = getStackInSlot(SLOT_KIT);
		if (kit != null)
			return;
		active = true;
	}

	private void updateState() {
		boolean old = active;
		updateActive();
		if (active == old)
			return;

		production = 0;
		notifyBlockUpdate();
	}

	// ------- Inventory -------
	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD1:
		case SLOT_CARD2:
			return stack.getItem() instanceof ItemCardMain && ItemCardMain.getKitFromCard(stack.getItemDamage()) != -1;
		case SLOT_INFO:
			return stack.getItem() instanceof ItemCardMain;
		case SLOT_ITEM:
		case SLOT_KIT:
		default:
			return false;
		}
	}

	// IEnergySink
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return true;
	}

	@Override
	public double getDemandedEnergy() {
		return Math.max(0, maxStorage - energy);
	}

	@Override
	public int getSinkTier() {
		return 1;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		double old = energy;
		energy += amount;
		double left = 0.0;

		if (energy > maxStorage) {
			left = energy - maxStorage;
			energy = maxStorage;
		}
		if (energy > 0 && old == 0 && !worldObj.isRemote)
			updateState();
		return left;
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return oldBlock != newBlock;
	}
}
