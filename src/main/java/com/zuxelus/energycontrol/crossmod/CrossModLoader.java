package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.inventory.Inventory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

public class CrossModLoader {
	private static final Map<String, CrossModBase> CROSS_MODS = new HashMap<>();

	public static void init() {
		loadCrossMod(ModIDs.TECH_REBORN, CrossTechReborn::new);
	}

	private static void loadCrossMod(String modid, Supplier<? extends CrossModBase> factory) {
		CROSS_MODS.put(modid, FabricLoader.getInstance().getModContainer(modid).isPresent() ? factory.get() : new CrossModBase());
	}

	@SuppressWarnings("unused")
	private static void loadCrossModSafely(String modid, Supplier<Supplier<? extends CrossModBase>> factory) {
		CROSS_MODS.put(modid, FabricLoader.getInstance().getModContainer(modid).isPresent() ? factory.get().get() : new CrossModBase());
	}

	public static CrossModBase getCrossMod(String modid) {
		return CROSS_MODS.get(modid);
	}

	public static ItemStack getEnergyCard(World world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te == null)
			return ItemStack.EMPTY;
		NbtCompound data = getEnergyData(te);
		if (data != null) {
			ItemStack card = new ItemStack(ModItems.card_energy);
			ItemStackHelper.setCoordinates(card, pos);
			return card;
		}
		return ItemStack.EMPTY;
	}

	public static NbtCompound getEnergyData(BlockEntity te) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			NbtCompound tag = crossMod.getEnergyData(te);
			if (tag != null)
				return tag;
		}
		// any block exposing Team Reborn Energy, e.g. the kit assembler
		EnergyStorage storage = findEnergyStorage(te);
		if (storage != null) {
			NbtCompound tag = new NbtCompound();
			tag.putString("euType", "E");
			tag.putDouble("storage", storage.getAmount());
			tag.putDouble("maxStorage", storage.getCapacity());
			return tag;
		}
		return null;
	}

	private static EnergyStorage findEnergyStorage(BlockEntity te) {
		World world = te.getWorld();
		if (world == null)
			return null;
		BlockPos pos = te.getPos();
		BlockState state = te.getCachedState();
		EnergyStorage storage = EnergyStorage.SIDED.find(world, pos, state, te, null);
		if (storage != null)
			return storage;
		// some providers only answer for a real side
		for (Direction side : Direction.values()) {
			storage = EnergyStorage.SIDED.find(world, pos, state, te, side);
			if (storage != null)
				return storage;
		}
		return null;
	}

	public static List<FluidInfo> getAllTanks(World world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te == null)
			return null;
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			List<FluidInfo> list = crossMod.getAllTanks(te);
			if (list != null)
				return list;
		}
		// any block exposing the Fabric Transfer API, e.g. vanilla cauldrons and most tech mods
		Storage<FluidVariant> storage = findFluidStorage(te);
		if (storage != null) {
			List<FluidInfo> result = new ArrayList<>();
			for (StorageView<FluidVariant> view : storage)
				if (view.getCapacity() > 0)
					result.add(new FluidInfo(view));
			if (!result.isEmpty())
				return result;
		}
		return null;
	}

	private static Storage<FluidVariant> findFluidStorage(BlockEntity te) {
		World world = te.getWorld();
		if (world == null)
			return null;
		BlockPos pos = te.getPos();
		BlockState state = te.getCachedState();
		Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos, state, te, null);
		if (storage != null)
			return storage;
		// some providers only answer for a real side
		for (Direction side : Direction.values()) {
			storage = FluidStorage.SIDED.find(world, pos, state, te, side);
			if (storage != null)
				return storage;
		}
		return null;
	}

	public static FluidInfo getTankAt(World world, BlockPos pos) {
		List<FluidInfo> tanks = getAllTanks(world, pos);
		return tanks != null && tanks.size() > 0 ? tanks.get(0) : null;
	}

	public static int getReactorHeat(World world, BlockPos pos) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			int heat = crossMod.getReactorHeat(world, pos);
			if (heat != -1)
				return heat;
		}
		return -1;
	}

	public static NbtCompound getInventoryData(BlockEntity te) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			NbtCompound tag = crossMod.getInventoryData(te);
			if (tag != null)
				return tag;
		}
		NbtCompound tag = new NbtCompound();
		if (te instanceof Inventory) {
			Inventory inv = (Inventory) te;
			/*if (te instanceof BaseContainerBlockEntity)
				tag.putString("name", ((BaseContainerBlockEntity) te).getDisplayName().getString());
			tag.putBoolean("sided", inv instanceof WorldlyContainer);*/
			int inUse = 0;
			int items = 0;
			tag.putInt("size", inv.size());
			for (int i = 0; i < Math.min(6, inv.size()); i++) {
				if (inv.getStack(i) != ItemStack.EMPTY) {
					inUse++;
					items += inv.getStack(i).getCount();
				}
				tag.put("slot" + Integer.toString(i), inv.getStack(i).encodeAllowEmpty(te.getWorld().getRegistryManager()));
			}
			tag.putInt("used", inUse);
			tag.putInt("items", items);
		}
		return tag;
	}
}
