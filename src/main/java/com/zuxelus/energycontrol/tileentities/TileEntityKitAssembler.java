package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.KitAssembler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;
import com.zuxelus.zlib.containers.EnergyStorage;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import api.hbm.energy.IEnergyUser;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.info.Info;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

@Optional.InterfaceList({
	@Optional.Interface(modid = ModIDs.IC2, iface = "ic2.api.energy.tile.IEnergySink"),
	@Optional.Interface(modid = ModIDs.HBM, iface = "api.hbm.energy.IEnergyUser")
})
public class TileEntityKitAssembler extends TileEntityInventory implements ITilePacketHandler, ISlotItemFilter, IEnergySink, IEnergyUser {
	public static final byte SLOT_INFO = 0;
	public static final byte SLOT_CARD1 = 1;
	public static final byte SLOT_ITEM = 2;
	public static final byte SLOT_CARD2 = 3;
	public static final byte SLOT_RESULT = 4;
	public static final byte SLOT_DISCHARGER = 5;
	private EnergyStorage storage;
	private int buffer;
	private static final int CONSUMPTION = 5;
	private KitAssemblerRecipe recipe;
	private int recipeTime; // client Only
	public static final int CAPACITY = 2000;
	public static final int OUTPUT = 32;
	private double production;
	private boolean addedToEnet;
	private boolean active;
	boolean isLoaded = true; // HBM only

	public TileEntityKitAssembler() {
		super("tile.kit_assembler.name");
		storage = new EnergyStorage(CAPACITY, OUTPUT, OUTPUT, 0);
		addedToEnet = false;
		active = false;
		production = 0;
	}

	public double getEnergy() {
		return storage.getEnergyStored();
	}

	public int getEnergyFactor() {
		return (int) Math.round(storage.getEnergyStored() * 52.0F / CAPACITY);
	}

	public double getProduction() {
		return production;
	}

	public int getProductionFactor() {
		if (recipeTime == 0)
			return 0;
		return (int) Math.round(production * 24.0F / recipeTime);
	}

	public int getRecipeTime() {
		if (recipe == null)
			return 0;
		return recipe.time;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 4:
			if (tag.hasKey("slot") && tag.hasKey("title")) {
				ItemStack stack = getStackInSlot(tag.getInteger("slot"));
				if (ItemCardMain.isCard(stack))
					new ItemCardReader(stack).setTitle(tag.getString("title"));
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
				storage.setEnergy(tag.getInteger("energy"));
				production = tag.getDouble("production");
			}
			if (tag.hasKey("time"))
				recipeTime = tag.getInteger("time");
			else
				recipeTime = 0;
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
			storage.setEnergy(tag.getLong("energy"));
		if (tag.hasKey("buffer"))
			buffer = tag.getInteger("buffer");
		if (tag.hasKey("production"))
			production = tag.getDouble("production");
		if (tag.hasKey("active")) {
			//boolean old = active;
			active = tag.getBoolean("active");
			//if (worldObj.isRemote && active != old)
			//	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		tag.setLong("energy", storage.getEnergyStored());
		tag.setInteger("buffer", buffer);
		tag.setDouble("production", production);
		return tag;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeProperties(tag);
	}

	@Optional.Method(modid = ModIDs.IC2)
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
	@Optional.Method(modid = ModIDs.IC2)
	public void onChunkUnload() {
		if (addedToEnet && !worldObj.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnet = false;
		}
		isLoaded = false; // HBM only
	}

	@Override
	public void updateEntity() {
		if (!addedToEnet)
			onLoad();
		if (worldObj.isRemote)
			return;
		handleDischarger(SLOT_DISCHARGER);
		if (Loader.isModLoaded(ModIDs.HBM))
			updateStandardConnections(worldObj, xCoord, yCoord, zCoord);
		if (!active)
			return;
		if (storage.getEnergyStored() >= CONSUMPTION) {
			storage.extractEnergy(CONSUMPTION, false);
			production += 1;
			if (recipe != null && production >= recipe.time) {
				ItemStack stack1 = getStackInSlot(SLOT_CARD1);
				ItemStack stack2 = getStackInSlot(SLOT_ITEM);
				ItemStack stack3 = getStackInSlot(SLOT_CARD2);
				ItemStack result = getStackInSlot(SLOT_RESULT);
				stack1.stackSize -= recipe.input1.stackSize;
				stack2.stackSize -= recipe.input2.stackSize;
				stack3.stackSize -= recipe.input3.stackSize;
				if (result == null)
					setInventorySlotContents(SLOT_RESULT, recipe.output.copy());
				else
					result.stackSize += recipe.output.stackSize;
				production = 0;
				updateState();
			}
		} else {
			storage.setEnergy(0);
			production = 0;
			updateState();
		}
	}

	private void handleDischarger(int slot) {
		long needed = Math.min(OUTPUT, storage.getMaxEnergyStored() - storage.getEnergyStored());
		if (needed <= 0)
			return;
		if (buffer > 0)
			buffer -= storage.receiveEnergy(buffer, false);
		needed = Math.min(OUTPUT, storage.getMaxEnergyStored() - storage.getEnergyStored());
		ItemStack stack = getStackInSlot(slot);
		if (stack != null && needed > 0) {
			if (stack.getItem().equals(Items.lava_bucket)) {
				buffer += 2000;
				buffer -= storage.receiveEnergy(buffer, false);
				setInventorySlotContents(slot, new ItemStack(Items.bucket));
				return;
			}
			if (CrossModLoader.isElectricItem(stack)) {
				double old = storage.getEnergyStored();
				storage.receiveEnergy((int) CrossModLoader.dischargeItem(stack, needed), false);
				if (!active && storage.getEnergyStored() > old)
					markDirty();
			}
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
		if (storage.getEnergyStored() < CONSUMPTION)
			return;
		KitAssemblerRecipe newRecipe;
		if (recipe == null) {
			newRecipe = KitAssemblerRecipe.findRecipe(this);
			if (newRecipe == null)
				return;
			recipe = newRecipe;
		} else if (!recipe.isSuitable(this)) {
			newRecipe = KitAssemblerRecipe.findRecipe(this);
			if (newRecipe == null) {
				recipe = null;
				return;
			}
			recipe = newRecipe;
		}
		active = true;
	}

	private void updateState() {
		boolean old = active;
		updateActive();
		if (active == old)
			return;

		production = 0;

		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
		if (!(block instanceof KitAssembler) || meta > 5 == active)
			return;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, active ? meta + 6 : meta - 6, 3);
	}

	// ------- Inventory -------
	@Override
	public int getSizeInventory() {
		return 6;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return isItemValid(slot, stack);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) { // ISlotItemFilter
		switch (slot) {
		case SLOT_CARD1:
		case SLOT_CARD2:
		case SLOT_ITEM:
			return true;
		case SLOT_INFO:
			return ItemCardMain.isCard(stack);
		case SLOT_DISCHARGER:
			return CrossModLoader.isElectricItem(stack) || stack.getItem().equals(Items.lava_bucket);
		case SLOT_RESULT:
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
		return Math.max(0, storage.getMaxEnergyStored() - storage.getEnergyStored());
	}

	@Override
	public int getSinkTier() {
		return 1;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		double left = 0.0;
		if (amount > 32) {
			left = amount - 32;
			amount = 32;
		}
		left += amount - storage.receiveEnergy((int) amount, false);
		return left;
	}

	// ISidedInventory
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if (side == ForgeDirection.UP.ordinal())
			return new int[] { SLOT_CARD1, SLOT_ITEM, SLOT_CARD2 };
		if (side == ForgeDirection.DOWN.ordinal())
			return new int[] { SLOT_RESULT };
		return super.getAccessibleSlotsFromSide(side);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return side == ForgeDirection.UP.ordinal() && (slot == SLOT_CARD1 || slot == SLOT_ITEM || slot == SLOT_CARD2);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return side == ForgeDirection.DOWN.ordinal() && slot == SLOT_RESULT;
	}

	// IWrenchable
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player) {
		return new ItemStack(ModItems.blockKitAssembler);
	}

	// IEnergyUser
	@Override
	@Optional.Method(modid = ModIDs.HBM)
	public long getPower() {
		return  storage.getEnergyStored();
	}

	@Override
	@Optional.Method(modid = ModIDs.HBM)
	public long getMaxPower() {
		return storage.getMaxEnergyStored();
	}

	@Override
	@Optional.Method(modid = ModIDs.HBM)
	public void setPower(long power) {
		storage.setEnergy(power);
	}

	@Override
	public boolean isLoaded() {
		return isLoaded;
	}
}
