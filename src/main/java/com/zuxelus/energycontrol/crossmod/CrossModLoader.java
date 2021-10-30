package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.computercraft.CrossComputerCraft;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class CrossModLoader {
	private static final Map<String, CrossModBase> CROSS_MODS = new HashMap<>();

	public static void init() {
		loadCrossMod(ModIDs.BIG_REACTORS, CrossBigReactors::new);
		loadCrossMod(ModIDs.BIGGER_REACTORS, CrossBiggerReactors::new);
		loadCrossMod(ModIDs.BOTANIA, CrossBotania::new);
		loadCrossModSafely(ModIDs.COMPUTER_CRAFT, () -> CrossComputerCraft::new);
		loadCrossModSafely(ModIDs.MEKANISM, () -> CrossMekanism::new);
		loadCrossModSafely(ModIDs.MEKANISM_GENERATORS, () -> CrossMekanismGenerators::new);
		loadCrossMod(ModIDs.IMMERSIVE_ENGINEERING, CrossImmersiveEngineering::new);
		loadCrossMod(ModIDs.THERMAL_EXPANSION, CrossThermalExpansion::new);
	}

	private static void loadCrossMod(String modid, Supplier<? extends CrossModBase> factory) {
		CROSS_MODS.put(modid, ModList.get().isLoaded(modid) ? factory.get() : new CrossModBase());
	}

	private static void loadCrossModSafely(String modid, Supplier<Supplier<? extends CrossModBase>> factory) {
		CROSS_MODS.put(modid, ModList.get().isLoaded(modid) ? factory.get().get() : new CrossModBase());
	}

	public static CrossModBase getCrossMod(String modid) {
		return CROSS_MODS.get(modid);
	}

	public static ItemStack getEnergyCard(World world, BlockPos pos) {
		TileEntity te = world.getBlockEntity(pos);
		if (te == null)
			return ItemStack.EMPTY;
		CompoundNBT data = getEnergyData(te);
		if (data != null) {
			ItemStack card = new ItemStack(ModItems.card_energy.get());
			ItemStackHelper.setCoordinates(card, pos);
			return card;
		}
		return ItemStack.EMPTY;
	}

	public static CompoundNBT getEnergyData(TileEntity te) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			CompoundNBT tag = crossMod.getEnergyData(te);
			if (tag != null)
				return tag;
		}
		Optional<IEnergyStorage> cap = te.getCapability(CapabilityEnergy.ENERGY, null).resolve();
		if (cap.isPresent()) {
			IEnergyStorage handler = cap.get();
			CompoundNBT tag = new CompoundNBT();
			tag.putString("euType", "FE");
			tag.putDouble("storage", handler.getEnergyStored());
			tag.putDouble("maxStorage", handler.getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	public static List<FluidInfo> getAllTanks(World world, BlockPos pos) {
		TileEntity te = world.getBlockEntity(pos);
		if (te == null)
			return null;
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			List<FluidInfo> list = crossMod.getAllTanks(te);
			if (list != null)
				return list;
		}
		Optional<IFluidHandler> fluid = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).resolve();
		if (fluid.isPresent()) {
			IFluidHandler handler = fluid.get();
			List<FluidInfo> result = new ArrayList<>();
			for (int i = 0; i < handler.getTanks(); i++)
				result.add(new FluidInfo(handler.getFluidInTank(i), handler.getTankCapacity(i)));
			return result;
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

	public static CompoundNBT getInventoryData(TileEntity te) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			CompoundNBT tag = crossMod.getInventoryData(te);
			if (tag != null)
				return tag;
		}
		Optional<IItemHandler> handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).resolve();
		if (!handler.isPresent() && !(te instanceof IInventory))
			return null;
		CompoundNBT tag = new CompoundNBT();
		if (handler.isPresent()) {
			IItemHandler storage = handler.get();
			int inUse = 0;
			int items = 0;
			tag.putInt("size", storage.getSlots());
			for (int i = 0; i < Math.min(6, storage.getSlots()); i++) {
				if (storage.getStackInSlot(i) != ItemStack.EMPTY) {
					inUse++;
					items += storage.getStackInSlot(i).getCount();
				}
				tag.put("slot" + Integer.toString(i), storage.getStackInSlot(i).save(new CompoundNBT()));
			}
			tag.putInt("used", inUse);
			tag.putInt("items", items);
		}
		if (te instanceof IInventory) {
			IInventory inv = (IInventory) te;
			tag.putBoolean("sided", inv instanceof ISidedInventory);
			if (!handler.isPresent()) {
				int inUse = 0;
				int items = 0;
				tag.putInt("size", inv.getContainerSize());
				for (int i = 0; i < Math.min(6, inv.getContainerSize()); i++) {
					if (inv.getItem(i) != ItemStack.EMPTY) {
						inUse++;
						items += inv.getItem(i).getCount();
					}
					tag.put("slot" + Integer.toString(i), inv.getItem(i).save(new CompoundNBT()));
				}
				tag.putInt("used", inUse);
				tag.putInt("items", items);
			}
		}
		return tag;
	}

	public static IFluidTank getPipeTank(TileEntity te) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			IFluidTank tank = crossMod.getPipeTank(te);
			if (tank != null)
				return tank;
		}
		return null;
	}
}
