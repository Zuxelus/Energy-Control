package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.SeedAnalyzer;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import ic2.api.crops.ICropSeed;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TileEntitySeedAnalyzer extends TileEntityInventory implements ITickable, ITilePacketHandler, ISlotItemFilter, IEnergySink {
	public static final byte SLOT_IN = 0;
	public static final byte SLOT_OUT = 1;
	public static final byte SLOT_DISCHARGER = 2;

	private static final double CONSUMPTION = 5;
	public static final int TIER = 1;
	public static final int CAPACITY = 10000;
	public static final int[] COST_TO_UPGRADE = {10, 90, 900, 9000};

	private double energy;
	private double production;
	private int productionMax;
	private boolean addedToEnet;
	private boolean active;

	public TileEntitySeedAnalyzer() {
		super("tile.seed_analyzer.name");
		addedToEnet = false;
		active = false;
		energy = 0;
		production = 0;
		productionMax = 0;
	}

	public double getEnergy() {
		return energy;
	}

	public int getEnergyFactor() {
		return (int) Math.round(energy / CAPACITY * 14);
	}

	public double getProduction() {
		return production;
	}

	public int getProductionMax() {
		return productionMax;
	}

	public int getProductionFactor() {
		return (int) Math.round(production / productionMax * 24);
	}

	public boolean getActive() {
		return active;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) { }

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("energy") && tag.hasKey("production") && tag.hasKey("productionMax")) {
				energy = tag.getDouble("energy");
				production = tag.getDouble("production");
				productionMax = tag.getInteger("productionMax");
			}
			break;
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		tag.setBoolean("active", active);
		return new SPacketUpdateTileEntity(pos, 0, tag);
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
		if (tag.hasKey("production"))
			production = tag.getDouble("production");
		if (tag.hasKey("productionMax"))
			productionMax = tag.getInteger("productionMax");
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
		tag.setDouble("production", production);
		tag.setInteger("productionMax", productionMax);
		return tag;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

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
		ItemStack stack = getStackInSlot(SLOT_DISCHARGER);
		if (!stack.isEmpty() && energy < CAPACITY && stack.getItem() instanceof IElectricItem) {
			IElectricItem ielectricitem = (IElectricItem) stack.getItem();
			double old = energy;
			if (ielectricitem.canProvideEnergy(stack))
				energy += ElectricItem.manager.discharge(stack, CAPACITY - energy, TIER, false, false, false);
			if (old == 0 && energy >= CONSUMPTION)
				updateState();
		}
		if (!active)
			return;
		if (energy < CONSUMPTION) {
			energy = 0;
			production = 0;
			updateState();
			return;
		}
		if (productionMax > 0) {
			energy -= CONSUMPTION;
			production += CONSUMPTION;
			if (production >= productionMax) {
				ItemStack seed = getStackInSlot(SLOT_IN);
				((ICropSeed) seed.getItem()).incrementScannedFromStack(seed);
				setInventorySlotContents(SLOT_OUT, seed);
				setInventorySlotContents(SLOT_IN, ItemStack.EMPTY);
				production = 0;
				productionMax = 0;
				updateState();
			}
		}
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
		productionMax = 0;
		if (energy < CONSUMPTION)
			return;
		ItemStack out = getStackInSlot(SLOT_OUT);
		if (!out.isEmpty())
			return;
		ItemStack stack = getStackInSlot(SLOT_IN);
		if (stack.isEmpty() || stack.getItem() != CrossModLoader.ic2.getItem("seed"))
			return;
		int level = ((ICropSeed) stack.getItem()).getScannedFromStack(stack);
		if (level == 4) {
			setInventorySlotContents(SLOT_OUT, getStackInSlot(SLOT_IN));
			setInventorySlotContents(SLOT_IN, ItemStack.EMPTY);

			IBlockState iblockstate = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
			return;
		}
		productionMax = COST_TO_UPGRADE[level];
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
		if (!(block instanceof SeedAnalyzer) || iblockstate.getValue(SeedAnalyzer.ACTIVE) == active)
			return;
		IBlockState newState = block.getDefaultState()
				.withProperty(SeedAnalyzer.FACING, iblockstate.getValue(SeedAnalyzer.FACING))
				.withProperty(SeedAnalyzer.ACTIVE, active);
		world.setBlockState(pos, newState, 3);
	}

	// ------- Inventory -------
	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_IN:
			return stack.getItem() == CrossModLoader.ic2.getItem("seed");
		case SLOT_DISCHARGER:
			return stack.getItem() instanceof IElectricItem && ((IElectricItem) stack.getItem()).getTier(stack) <= TIER;
		case SLOT_OUT:
		default:
			return false;
		}
	}

	// ISidedInventory
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] {0, 1, 2};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
		if (slot != SLOT_IN || side != EnumFacing.UP)
			return false;
		return stack.getItem() == CrossModLoader.ic2.getItem("seed");
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
		if (slot != SLOT_OUT || side == EnumFacing.UP)
			return false;
		ItemStack crop = getStackInSlot(slot);
		if (crop.isEmpty() || crop.getItem() != CrossModLoader.ic2.getItem("seed"))
			return false;
		ICropSeed seed = (ICropSeed) crop.getItem();
		if (side == EnumFacing.DOWN)
			return seed.getScannedFromStack(crop) == 4;
		if (side != EnumFacing.DOWN)
			return seed.getScannedFromStack(crop) < 4;
		return true;
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
		return TIER;
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		double old = energy;
		energy += amount;
		double left = 0.0;

		if (energy > CAPACITY) {
			left = energy - CAPACITY;
			energy = CAPACITY;
		}
		if (energy >= CONSUMPTION && old < CONSUMPTION && !world.isRemote)
			updateState();
		return left;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
}
