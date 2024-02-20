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
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.RegisterEvent;

public class CrossModLoader {
	private static final Map<String, CrossModBase> CROSS_MODS = new HashMap<>();

	public static void init() {
		//loadCrossMod(ModIDs.ADV_GENERATORS, CrossAdvGenerators::new);
		//loadCrossMod(ModIDs.APPLIED_ENERGISTICS, CrossAppEng::new);
		loadCrossMod(ModIDs.BIG_REACTORS, CrossBigReactors::new);
		//loadCrossMod(ModIDs.BIGGER_REACTORS, CrossBiggerReactors::new);
		loadCrossModSafely(ModIDs.COMPUTER_CRAFT, () -> CrossComputerCraft::new);
		//loadCrossModSafely(ModIDs.IC2, () -> CrossIC2Classic::new);
		loadCrossModSafely(ModIDs.MEKANISM, () -> CrossMekanism::new);
		loadCrossModSafely(ModIDs.MEKANISM_GENERATORS, () -> CrossMekanismGenerators::new);
		//loadCrossMod(ModIDs.IMMERSIVE_ENGINEERING, CrossImmersiveEngineering::new);
		//loadCrossModSafely(ModIDs.INDUSTRIAL_REBORN, () -> CrossIndustrialReborn::new);
		//loadCrossMod(ModIDs.THERMAL_EXPANSION, CrossThermalExpansion::new);
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

	public static ItemStack getEnergyCard(Level world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te == null)
			return ItemStack.EMPTY;
		CompoundTag data = getEnergyData(te);
		if (data != null) {
			ItemStack card = new ItemStack(ModItems.card_energy.get());
			ItemStackHelper.setCoordinates(card, pos);
			return card;
		}
		return ItemStack.EMPTY;
	}

	public static CompoundTag getEnergyData(BlockEntity te) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			CompoundTag tag = crossMod.getEnergyData(te);
			if (tag != null)
				return tag;
		}
		Optional<IEnergyStorage> cap = te.getCapability(ForgeCapabilities.ENERGY).resolve();
		if (cap.isPresent()) {
			IEnergyStorage handler = cap.get();
			CompoundTag tag = new CompoundTag();
			tag.putString(DataHelper.EUTYPE, "FE");
			tag.putDouble(DataHelper.ENERGY, handler.getEnergyStored());
			tag.putDouble(DataHelper.CAPACITY, handler.getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	public static List<FluidInfo> getAllTanks(Level world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te == null)
			return null;
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			List<FluidInfo> list = crossMod.getAllTanks(te);
			if (list != null)
				return list;
		}
		Optional<IFluidHandler> fluid = te.getCapability(ForgeCapabilities.FLUID_HANDLER, null).resolve();
		if (fluid.isPresent()) {
			IFluidHandler handler = fluid.get();
			List<FluidInfo> result = new ArrayList<>();
			for (int i = 0; i < handler.getTanks(); i++)
				result.add(new FluidInfo(handler.getFluidInTank(i), handler.getTankCapacity(i)));
			return result;
		}
		return null;
	}

	public static FluidInfo getTankAt(Level world, BlockPos pos) {
		List<FluidInfo> tanks = getAllTanks(world, pos);
		return tanks != null && tanks.size() > 0 ? tanks.get(0) : null;
	}

	public static int getReactorHeat(Level world, BlockPos pos) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			int heat = crossMod.getReactorHeat(world, pos);
			if (heat != -1)
				return heat;
		}
		return -1;
	}

	public static CompoundTag getInventoryData(BlockEntity te) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			CompoundTag tag = crossMod.getInventoryData(te);
			if (tag != null)
				return tag;
		}
		Optional<IItemHandler> handler = te.getCapability(ForgeCapabilities.ITEM_HANDLER, null).resolve();
		if (!handler.isPresent() && !(te instanceof Container))
			return null;
		CompoundTag tag = new CompoundTag();
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
				tag.put("slot" + Integer.toString(i), storage.getStackInSlot(i).save(new CompoundTag()));
			}
			tag.putInt("used", inUse);
			tag.putInt("items", items);
		}
		if (te instanceof Container) {
			Container inv = (Container) te;
			if (te instanceof BaseContainerBlockEntity)
				tag.putString("name", ((BaseContainerBlockEntity) te).getDisplayName().getString());
			tag.putBoolean("sided", inv instanceof WorldlyContainer);
			if (!handler.isPresent()) {
				int inUse = 0;
				int items = 0;
				tag.putInt("size", inv.getContainerSize());
				for (int i = 0; i < Math.min(6, inv.getContainerSize()); i++) {
					if (inv.getItem(i) != ItemStack.EMPTY) {
						inUse++;
						items += inv.getItem(i).getCount();
					}
					tag.put("slot" + Integer.toString(i), inv.getItem(i).save(new CompoundTag()));
				}
				tag.putInt("used", inUse);
				tag.putInt("items", items);
			}
		}
		return tag;
	}

	public static boolean isElectricItem(ItemStack stack) {
		if (stack.isEmpty())
			return false;

		for (CrossModBase crossMod : CROSS_MODS.values())
			if (crossMod.isElectricItem(stack))
				return true;
		return false;
	}

	public static double dischargeItem(ItemStack stack, int amount, int tier) {
		for (CrossModBase crossMod : CROSS_MODS.values())
			if (crossMod.isElectricItem(stack)) {
				double result = crossMod.dischargeItem(stack, amount, tier);
				if (result > 0)
					return result;
			}
		return 0;
	}

	public static void registerItems(RegisterEvent.RegisterHelper<Item> event) {
		for (CrossModBase crossMod : CROSS_MODS.values())
			crossMod.registerItems(event);
	}
}
