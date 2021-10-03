package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.api.IItemCard;
import com.zuxelus.energycontrol.blocks.KitAssembler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.crossmod.jei.KitAssemblerRecipeWrapper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.info.Info;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(modid = ModIDs.IC2, iface = "ic2.api.energy.tile.IEnergySink")
public class TileEntityKitAssembler extends TileEntityInventory implements ITickable, ITilePacketHandler, ISlotItemFilter, IEnergySink, IEnergyStorage {
	public static final byte SLOT_INFO = 0;
	public static final byte SLOT_CARD1 = 1;
	public static final byte SLOT_ITEM = 2;
	public static final byte SLOT_CARD2 = 3;
	public static final byte SLOT_RESULT = 4;
	public static final byte SLOT_DISCHARGER = 5;
	private double energy;
	private double buffer;
	private static final int CONSUMPTION = 5;
	private KitAssemblerRecipe recipe;
	private int recipeTime; // client Only
	public static final int CAPACITY = 2000;
	private double production;
	private boolean addedToEnet;
	private boolean active;

	public TileEntityKitAssembler() {
		super("tile.kit_assembler.name");
		addedToEnet = false;
		active = false;
		production = 0;
	}

	public double getEnergy() {
		return energy;
	}

	public int getEnergyFactor() {
		return (int) Math.round(energy * 52.0F / CAPACITY);
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
		if (tag.getInteger("type") == 4) {
			if (tag.hasKey("slot") && tag.hasKey("title")) {
				ItemStack stack = getStackInSlot(tag.getInteger("slot"));
				if (ItemCardMain.isCard(stack))
					new ItemCardReader(stack).setTitle(tag.getString("title"));
			}
		}
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		if (tag.getInteger("type") == 1) {
			if (tag.hasKey("energy") && tag.hasKey("production")) {
				energy = tag.getDouble("energy");
				production = tag.getDouble("production");
			}
			if (tag.hasKey("time"))
				recipeTime = tag.getInteger("time");
			else
				recipeTime = 0;
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		tag.setBoolean("active", active);
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
		updateActive();
		tag.setBoolean("active", active);
		return tag;
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		if (tag.hasKey("energy"))
			energy = tag.getDouble("energy");
		if (tag.hasKey("buffer"))
			buffer = tag.getDouble("buffer");
		if (tag.hasKey("production"))
			production = tag.getDouble("production");
		if (tag.hasKey("active"))
			active = tag.getBoolean("active");
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
		tag.setDouble("buffer", buffer);
		tag.setDouble("production", production);
		return tag;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

	@Override
	@Optional.Method(modid = ModIDs.IC2)
	public void onLoad() {
		if (!addedToEnet && !world.isRemote && Info.isIc2Available()) {
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
		if (addedToEnet && !world.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnet = false;
		}
	}

	@Override
	public void update() {
		if (!addedToEnet)
			onLoad();
		if (world.isRemote)
			return;
		handleDischarger(SLOT_DISCHARGER);
		if (!active)
			return;
		if (energy >= CONSUMPTION) {
			energy -= CONSUMPTION;
			production += 1;
			if (recipe != null && production >= recipe.time) {
				ItemStack stack1 = getStackInSlot(SLOT_CARD1);
				ItemStack stack2 = getStackInSlot(SLOT_ITEM);
				ItemStack stack3 = getStackInSlot(SLOT_CARD2);
				ItemStack result = getStackInSlot(SLOT_RESULT);
				stack1.shrink(recipe.count1);
				stack2.shrink(recipe.count2);
				stack3.shrink(recipe.count3);
				if (result.isEmpty())
					setInventorySlotContents(SLOT_RESULT, recipe.output.copy());
				else
					result.grow(recipe.output.getCount());
				production = 0;
				updateState();
			}
		} else {
			energy = 0;
			production = 0;
			updateState();
		}
	}

	private void handleDischarger(int slot) {
		double needed = Math.min(32, getDemandedEnergy());
		if (needed <= 0)
			return;
		if (buffer > 0)
			buffer = injectEnergy(null, buffer, 0); 
		needed = Math.min(32, getDemandedEnergy());
		ItemStack stack = getStackInSlot(slot);
		if (!stack.isEmpty() && needed > 0) {
			if (stack.getItem().equals(Items.LAVA_BUCKET)) {
				buffer += 2000;
				buffer = injectEnergy(null, buffer, 0);
				setInventorySlotContents(slot, new ItemStack(Items.BUCKET));
				return;
			}
			IEnergyStorage stackStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
			if (stackStorage != null) {
				if (receiveEnergy(stackStorage.extractEnergy((int) Math.floor(needed), false), false) > 0)
					active = true;
			} else if (CrossModLoader.getCrossMod(ModIDs.IC2).isElectricItem(stack)) {
				double old = energy;
				energy += CrossModLoader.getCrossMod(ModIDs.IC2).dischargeItem(stack, getDemandedEnergy());
				if (!active && energy > old)
					markDirty();
			}
		}
	}
	public void notifyBlockUpdate() {
		IBlockState iblockstate = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (world == null || world.isRemote)
			return;
		updateState();
	}

	private void updateActive() {
		active = false;
		if (energy < CONSUMPTION)
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

		IBlockState iblockstate = world.getBlockState(pos);
		Block block = iblockstate.getBlock();
		if (!(block instanceof KitAssembler) || iblockstate.getValue(KitAssembler.ACTIVE) == active)
			return;
		IBlockState newState = block.getDefaultState()
				.withProperty(KitAssembler.FACING, iblockstate.getValue(KitAssembler.FACING))
				.withProperty(KitAssembler.ACTIVE, active);
		world.setBlockState(pos, newState, 3);
	}

	// ------- Inventory -------
	@Override
	public int getSizeInventory() {
		return 6;
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
		case SLOT_ITEM:
			return true;
		case SLOT_INFO:
			return ItemCardMain.isCard(stack);
		case SLOT_DISCHARGER:
			return stack.getCapability(CapabilityEnergy.ENERGY, null) != null ||
				CrossModLoader.getCrossMod(ModIDs.IC2).isElectricItem(stack) ||
				stack.getItem().equals(Items.LAVA_BUCKET);
		case SLOT_RESULT:
		default:
			return false;
		}
	}

	// IEnergySink
	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
		return true;
	}

	@Override
	public double getDemandedEnergy() {
		return Math.max(0, CAPACITY - energy);
	}

	@Override
	public int getSinkTier() {
		return 1;
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		double left = 0.0;
		if (amount > 32) {
			left = amount - 32;
			amount = 32;
		}
		energy += amount;

		if (energy > CAPACITY) {
			left += energy - CAPACITY;
			energy = CAPACITY;
		}
		return left;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY)
			return (T) this;
		return null;
	}

	// IEnergyStorage
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int energyReceived = Math.min(CAPACITY - getEnergyStored(), Math.min(32, maxReceive));
		if (!simulate)
			energy += energyReceived;
		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return (int) Math.ceil(energy);
	}

	@Override
	public int getMaxEnergyStored() {
		return CAPACITY;
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	// ISidedInventory
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (side == EnumFacing.UP)
			return new int[] { SLOT_CARD1, SLOT_ITEM, SLOT_CARD2 };
		if (side == EnumFacing.DOWN)
			return new int[] { SLOT_RESULT };
		return super.getSlotsForFace(side);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
		return side == EnumFacing.UP && (slot == SLOT_CARD1 || slot == SLOT_ITEM || slot == SLOT_CARD2);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
		return side == EnumFacing.DOWN && slot == SLOT_RESULT;
	}
}
