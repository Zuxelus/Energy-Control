package com.zuxelus.energycontrol.tileentities;

import java.util.HashMap;
import java.util.Vector;

import com.zuxelus.energycontrol.utils.SeedLibraryFilter;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.network.NetworkHelper;
import com.zuxelus.zlib.tileentities.IBlockHorizontal;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import cpw.mods.fml.common.FMLCommonHandler;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.core.Ic2Items;
import ic2.core.item.ItemCropSeed;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntitySeedLibrary extends TileEntityInventory implements ITilePacketHandler, ISlotItemFilter, IEnergySink, IBlockHorizontal {
	public static final byte SLOT_DISCHARGER = 0;
	public static final byte FAKE_SLOT = 9;

	private static final double CONSUMPTION = 1;
	public static final int TIER = 1;
	public static final int CAPACITY = 1000;
	private double energy;
	private boolean addedToEnet;
	private boolean active;

	protected SeedLibraryFilter[] filters = new SeedLibraryFilter[7];
	protected HashMap<String, ItemStack> deepContents = new HashMap<String, ItemStack>();
	protected Vector<ItemStack> unresearched = new Vector<ItemStack>();
	// The number of seeds that match the GUI filter.
	public int seeds_available = 0;

	public TileEntitySeedLibrary() {
		super("tile.seed_library.name");
		addedToEnet = false;
		active = false;
		energy = 0;
		
		for (int i = 0; i < filters.length - 1; i++)
			filters[i] = new SeedLibraryFilter(null);
		// The GUI filter gets a reference to the library, so that it can announce when its count changes.
		filters[filters.length - 1] = new SeedLibraryFilter(this);
	}

	public double getEnergy() {
		return energy;
	}

	public boolean getActive() {
		return active;
	}

	public SeedLibraryFilter getGUIFilter()
	{
		return filters[6];
	}

	public int getSliderValue(int slider) {
		SeedLibraryFilter filter = getGUIFilter();
		int bar = slider / 2;
		int arrow = slider % 2;
		if (bar == 0) {
			if (arrow == 0)
				return filter.min_growth;
			return filter.max_growth;
		} else if (bar == 1) {
			if (arrow == 0)
				return filter.min_gain;
			return filter.max_gain;
		} else if (bar == 2) {
			if (arrow == 0)
				return filter.min_resistance;
			return filter.max_resistance;
		} else { // if (bar == 3)
			if (arrow == 0)
				return filter.min_total / 3;
			return filter.max_total / 3;
		}
	}

	public void setSliderValue(int slider, int value)
	{
		SeedLibraryFilter filter = getGUIFilter();
		int bar = slider / 2;
		int arrow = slider % 2;
		if (bar == 0)
		{
			if (arrow == 0)
				filter.min_growth = value;
			else
				filter.max_growth = value;
		}
		else if (bar == 1)
		{
			if (arrow == 0)
				filter.min_gain = value;
			else
				filter.max_gain = value;
		}
		else if (bar == 2)
		{
			if (arrow == 0)
				filter.min_resistance = value;
			else
				filter.max_resistance = value;
		}
		else
		{ // if (bar == 3)
			if (arrow == 0)
				filter.min_total = value * 3;
			else
				filter.max_total = value * 3;
		}
		filter.settingsChanged();
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) { 
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("button") && tag.hasKey("click"))
				actionPerformed(tag.getInteger("button"), tag.getBoolean("click"));
			break;
		case 2:
			if (tag.hasKey("slider") && tag.hasKey("value"))
				setSliderValue(tag.getInteger("slider"), tag.getInteger("value"));
			break;
		}
	}

	private void actionPerformed(int button, boolean rightClick)
	{
		if (button == 0)
			importFromInventory();
		else if (button == 1)
			exportToInventory();
		else if (button == 2) {
			SeedLibraryFilter filter = getGUIFilter();
			filter.unknown_type = (filter.unknown_type + 1) % 3;
			filter.settingsChanged();
		} else if (button == 3) {
			SeedLibraryFilter filter = getGUIFilter();
			filter.unknown_ggr = (filter.unknown_ggr + 1) % 3;
			filter.settingsChanged();
		} else if (button < 10) {
			int dir = button - 4;
			if (rightClick) {
				filters[dir].copyFrom(filters[6]);
				markDirty();
			} else
				filters[6].copyFrom(filters[dir]);
		}
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			getGUIFilter().loadFromNBT(tag);
			energy = tag.getDouble("energy"); 
			break;
		case 2:
			if (tag.hasKey("value"))
				seeds_available = tag.getInteger("value");
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

		deepContents.clear();
		unresearched.clear();

		NBTTagList filterList = tag.getTagList("Filters", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < 7; i++)
			filters[i].loadFromNBT(filterList.getCompoundTagAt(i));
		NBTTagList bufferList = tag.getTagList("Items_", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < bufferList.tagCount(); i++) {
			NBTTagCompound slot = bufferList.getCompoundTagAt(i);
			//int j = slot.getByte("Slot");
			ItemStack stack = ItemStack.loadItemStackFromNBT(slot);
			loadSeed(stack);
		}
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setDouble("energy", energy);
		return tag;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList inventoryTag = new NBTTagList();
		for (ItemStack seed : deepContents.values()) {
			NBTTagCompound seedtag = new NBTTagCompound();
			seedtag.setByte("Slot", (byte) -1);
			seed.writeToNBT(seedtag);
			inventoryTag.appendTag(seedtag);
		}
		tag.setTag("Items_", inventoryTag);

		NBTTagList filterList = new NBTTagList();
		for (int i = 0; i < 7; i++) {
			NBTTagCompound filtertag = new NBTTagCompound();
			filters[i].writeToNBT(filtertag);
			filterList.appendTag(filtertag);
		}
		tag.setTag("Filters", filterList);
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
		if (worldObj.isRemote)
			return;
		ItemStack stack = getStackInSlot(SLOT_DISCHARGER);
		if (stack != null && energy < CAPACITY && stack.getItem() instanceof IElectricItem) {
			IElectricItem ielectricitem = (IElectricItem) stack.getItem();
			double old = energy;
			if (ielectricitem.canProvideEnergy(stack))
				energy += ElectricItem.manager.discharge(stack, CAPACITY - energy, TIER, false, false, false);
			if (old == 0 && energy >= CONSUMPTION)
				updateState();
		}
		if (energy >= CONSUMPTION)
			energy -= CONSUMPTION;
		else {
			energy = 0;
			updateState();
		}
	}

	private void updateActive() {
		active = energy >= CONSUMPTION;
	}

	private void updateState() {
		boolean old = active;
		updateActive();
		if (active == old)
			return;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	// ------- Inventory -------
	@Override
	public int getSizeInventory() {
		return 10;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_DISCHARGER:
			return stack.getItem() instanceof IElectricItem;
		case FAKE_SLOT:
			return false;
		default:
			return stack.getItem() == Ic2Items.cropSeed.getItem();
		}
	}

	// ISidedInventory
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] {1, 2, 3, 4, 5, 6, 7, 8};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		if (slot < 1 || slot > 8)
			return false;
		return stack.getItem() == Ic2Items.cropSeed.getItem();
	}

	public String getKey(ItemStack seed) {
		CropCard card = Crops.instance.getCropCard(seed);
		int id = card.getId();
		byte growth = ItemCropSeed.getGrowthFromStack(seed);
		byte gain = ItemCropSeed.getGainFromStack(seed);
		byte resistance = ItemCropSeed.getResistanceFromStack(seed);
		byte scan = ItemCropSeed.getScannedFromStack(seed);
		return id + ":" + growth + ":" + gain + ":" + resistance + ":" + scan;
	}

	private void loadSeed(ItemStack seed) {
		String key = getKey(seed);
		ItemStack stored = deepContents.get(key);
		if (stored != null) {
			// Found a pre-existing stack. Using it will update everything...
			stored.stackSize += seed.stackSize;
			// ...except the GUI's seed count, so update that now.
			updateCountIfMatch(stored);
		} else {
			// No pre-existing stack. Make a new one.
			stored = seed.copy();
			// If it's not fully scanned, prep it for analysis.
			if (ItemCropSeed.getScannedFromStack(stored) < 4)
				unresearched.add(stored);
			// Add it to the main storage bank.
			deepContents.put(key, stored);
			// Inform filters of the new seed.
			for (SeedLibraryFilter filter : filters)
				filter.newSeed(stored);
		}
	}

	private void updateCountIfMatch(ItemStack seed) {
		SeedLibraryFilter filter = getGUIFilter();
		if (!filter.bulk_mode && filter.isMatch(seed))
			updateSeedCount();
	}

	public void updateSeedCount() {
		seeds_available = getGUIFilter().getCount(deepContents.values());

		// We only need to do the rest on the server side.
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) // world is empty
			return;

		// Notify all nearby players that the seed count has changed.
		if (seeds_available > 65535)
			seeds_available = 65535;

		if (worldObj != null) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 2);
			tag.setInteger("value", seeds_available);
			NetworkHelper.updateClientTileEntity(worldObj, xCoord, yCoord, zCoord, tag);
		}
	}

	private void unloadSeed(ItemStack seed) {
		String key = getKey(seed);
		ItemStack stored = deepContents.get(key);
		if (stored == null)
			return;
		// Found a pre-existing stack, so we can reduce it.
		stored.stackSize -= seed.stackSize;
		if (stored.stackSize <= 0) {
			// None left.
			// If it's not fully scanned, remove it from the analyser menu.
			if (ItemCropSeed.getScannedFromStack(stored) < 4)
				unresearched.remove(stored);
			// Remove it from main storage.
			deepContents.remove(getKey(stored));
			// Inform filters that the seed isn't available anymore.
			for (SeedLibraryFilter filter : filters)
				filter.lostSeed(stored);
		} else
			// All we need to do is update the GUI count.
			updateCountIfMatch(stored);
	}

	public void importFromInventory()
	{
		getGUIFilter().bulk_mode = true;
		for (int i = 1; i < 9; i++) {
			ItemStack stack = getStackInSlot(i);
			if (stack != null && stack.getItem() == IC2Items.getItem("cropSeed").getItem()) {
				loadSeed(stack);
				inventory[i] = null;
			}
		}
		getGUIFilter().bulk_mode = false;
		markDirty();
		updateSeedCount();
	}

	public void exportToInventory()
	{
		getGUIFilter().bulk_mode = true;
		for (int i = 1; i < 9; i++)
			if (getStackInSlot(i) == null) {
				// Get a seed from the active filter.
				ItemStack seed = filters[6].getSeed(deepContents.values());
				if (seed == null)
					break; // No seeds left; stop exporting.
				// Add one of the seed to the inventory.
				ItemStack stack = seed.copy();
				stack.stackSize = 1;
				inventory[i] = stack;
				// And remove the seed from main storage.
				unloadSeed(inventory[i]);
			}
		getGUIFilter().bulk_mode = false;
		markDirty();
		updateSeedCount();
	}

	public void updateGUIFilter()
	{
		// We only need to do this on the server side.
		if (worldObj == null || worldObj.isRemote)
			return;

		markDirty();

		// Notify all nearby players that the GUI filter has changed.
		NBTTagCompound tag = new NBTTagCompound();
		getGUIFilter().writeToNBT(tag);
		tag.setInteger("type", 1);
		tag.setDouble("energy", energy);
		NetworkHelper.updateClientTileEntity(worldObj, xCoord, yCoord, zCoord, tag);
	}

	// IEnergySink
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
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
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		double old = energy;
		energy += amount;
		double left = 0.0;

		if (energy > CAPACITY) {
			left = energy - CAPACITY;
			energy = CAPACITY;
		}
		if (energy >= CONSUMPTION && old == 0 && !worldObj.isRemote)
			updateState();
		return left;
	}
}
