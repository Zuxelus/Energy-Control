package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.tileentities.TileEntitySeedLibrary;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropSeed;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Collection;
import java.util.Vector;

public class SeedLibraryFilter {
	public TileEntitySeedLibrary te;
	public boolean bulk_mode;
	public int unknown_type = 1;
	public int unknown_ggr = 1;
	public CropCard seed_type;
	public int min_growth;
	public int min_gain;
	public int min_resistance;
	public int max_growth = 31;
	public int max_gain = 31;
	public int max_resistance = 31;
	public int min_total;
	public int max_total = 93;
	public SeedLibrarySort sort = SeedLibrarySort.TOTAL_DESC;

	public static final int CACHE_SIZE = 10;
	public Vector<ItemStack> cache = new Vector<>(CACHE_SIZE + 1);
	public boolean cached_nothing;

	public SeedLibraryFilter(TileEntitySeedLibrary owner) {
		te = owner;
	}

	public void copyFrom(SeedLibraryFilter source) {
		unknown_type = source.unknown_type;
		unknown_ggr = source.unknown_ggr;
		seed_type = source.seed_type;
		min_growth = source.min_growth;
		min_gain = source.min_gain;
		min_resistance = source.min_resistance;
		max_growth = source.max_growth;
		max_gain = source.max_gain;
		max_resistance = source.max_resistance;
		min_total = source.min_total;
		max_total = source.max_total;
		sort = source.sort;

		settingsChanged();
	}

	public ItemStack getSeed(Collection<ItemStack> seeds) {
		if (cached_nothing)
			return null;

		if (cache.size() == 0)
			fillCache(seeds);

		if (cache.size() == 0) {
			cached_nothing = true;
			return null;
		}

		return cache.get(0);
	}

	public int getCount(Collection<ItemStack> seeds) {
		int count = 0;
		for (ItemStack seed : seeds)
			if (isMatch(seed))
				count += seed.getCount();

		return count;
	}

	public void newSeed(ItemStack seed) {
		if (isMatch(seed)) {
			addToCache(seed);
			updateSeedCount();
		}
	}

	public void lostSeed(ItemStack seed) {
		if (isMatch(seed)) {
			removeFromCache(seed);
			updateSeedCount();
		}
	}

	public void settingsChanged() {
		cache.clear();
		cached_nothing = false;

		if (te != null && !FMLCommonHandler.instance().getEffectiveSide().isClient()) { // world is empty
			updateSeedCount();
			te.updateGUIFilter();
		}
	}

	public boolean isMatch(ItemStack stack) {
		CropCard card = Crops.instance.getCropCard(stack);
		ICropSeed seed = (ICropSeed) stack.getItem();
		int scan = seed.getScannedFromStack(stack);

		if (scan == 0)
			return (unknown_type > 0) && (unknown_ggr > 0);
		if (unknown_type == 2)
			return false;
		if (seed_type != null && seed_type != card)
			return false;
		if (scan < 4)
			return (unknown_ggr > 0);
		if (unknown_ggr == 2)
			return false;

		int growth = seed.getGrowthFromStack(stack);
		int gain = seed.getGainFromStack(stack);
		int resistance = seed.getResistanceFromStack(stack);

		if (growth < min_growth || growth > max_growth || gain < min_gain || gain > max_gain || resistance < min_resistance || resistance > max_resistance)
			return false;
		int total = growth + gain + resistance;
        return total >= min_total && total <= max_total;
    }

	public String getCropName() {
		if (seed_type == null)
			return "Any";
		/*if (Crops.instance.getCropList()[crop_id] == null)
			return "(Invalid)";*/
		return seed_type.getUnlocalizedName();
	}

	public void setCropFromSeed(ItemStack seed) {
		if (seed.isEmpty()) {
			seed_type = null;
		} else if (!(seed.getItem() instanceof ICropSeed)) {
			seed_type = null;
		} else if (((ICropSeed)seed.getItem()).getScannedFromStack(seed) == 0) {
			seed_type = null;
		} else {
			seed_type = Crops.instance.getCropCard(seed);
		}
		settingsChanged();
	}

	protected void fillCache(Collection<ItemStack> seeds) {
		cache.clear();

		bulk_mode = true;
		for (ItemStack seed : seeds)
			newSeed(seed);
		bulk_mode = false;

		updateSeedCount();
	}

	protected void updateSeedCount() {
		if (!bulk_mode && te != null)
			te.updateSeedCount();
	}

	protected void addToCache(ItemStack seed) {
		cached_nothing = false;

		int pos = 0;
		for (int i = cache.size() - 1; i >= 0; i--) {
			int cmp = sort.compare(seed, cache.get(i));
			if (cmp <= 0) {
				pos = i + 1;
				break;
			}
		}

		if (pos >= CACHE_SIZE)
			return;

		cache.add(pos, seed);

		while (cache.size() > CACHE_SIZE)
			cache.remove(cache.size() - 1);
	}

	protected void removeFromCache(ItemStack seed) {
		cache.remove(seed);
	}

	// Save/load
	public void loadFromNBT(NBTTagCompound input) {
		unknown_type = input.getByte("unknown_type");
		unknown_ggr = input.getByte("unknown_ggr");
		if (input.hasKey("owner") && input.hasKey("id"))
			seed_type = Crops.instance.getCropCard(input.getString("owner"),input.getString("id"));
		min_growth = input.getInteger("min_growth");
		min_gain = input.getInteger("min_gain");
		min_resistance = input.getInteger("min_resistance");
		max_growth = input.getInteger("max_growth");
		max_gain = input.getInteger("max_gain");
		max_resistance = input.getInteger("max_resistance");
		min_total = input.getInteger("min_total");
		max_total = input.getInteger("max_total");

		int sort_type = input.getInteger("sort_type");
		boolean sort_desc = input.getBoolean("sort_desc");
		sort = SeedLibrarySort.getSort(sort_type, sort_desc);

		settingsChanged();
	}

	public void writeToNBT(NBTTagCompound output) {
		output.setByte("unknown_type", (byte) unknown_type);
		output.setByte("unknown_ggr", (byte) unknown_ggr);
		if (seed_type != null) {
			output.setString("owner", seed_type.getOwner()); 
			output.setString("id", seed_type.getId());
		}
		output.setInteger("min_growth", min_growth);
		output.setInteger("min_gain", min_gain);
		output.setInteger("min_resistance", min_resistance);
		output.setInteger("max_growth", max_growth);
		output.setInteger("max_gain", max_gain);
		output.setInteger("max_resistance", max_resistance);
		output.setInteger("min_total", min_total);
		output.setInteger("max_total", max_total);

		output.setInteger("sort_type", sort.sort_type);
		output.setBoolean("sort_desc", sort.descending);
	}
}
