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

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.ModList;

public class CrossModLoader {
	private static final Map<String, CrossModBase> CROSS_MODS = new HashMap<>();

	public static void init() {
		loadCrossMod(ModIDs.BIG_REACTORS, CrossBigReactors::new);
		//loadCrossMod(ModIDs.BIGGER_REACTORS, CrossBiggerReactors::new);
		loadCrossModSafely(ModIDs.COMPUTER_CRAFT, () -> CrossComputerCraft::new);
		/*loadCrossMod(ModIDs.MEKANISM, CrossMekanism::new);
		loadCrossMod(ModIDs.MEKANISM_GENERATORS, CrossMekanismGenerators::new);
		loadCrossMod(ModIDs.IMMERSIVE_ENGINEERING, CrossImmersiveEngineering::new);
		loadCrossMod(ModIDs.THERMAL_EXPANSION, CrossThermalExpansion::new);*/
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
		Optional<IEnergyStorage> cap = te.getCapability(CapabilityEnergy.ENERGY).resolve();
		if (cap.isPresent()) {
			IEnergyStorage handler = cap.get();
			CompoundTag tag = new CompoundTag();
			tag.putString("euType", "FE");
			tag.putDouble("storage", handler.getEnergyStored());
			tag.putDouble("maxStorage", handler.getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	public static List<FluidInfo> getAllTanks(Level world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te != null) {
			Optional<IFluidHandler> fluid = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).resolve();
			if (fluid.isPresent()) {
				IFluidHandler handler = fluid.get();
				List<FluidInfo> result = new ArrayList<>();
				for (int i = 0; i < handler.getTanks(); i++) {
					FluidTank tank = new FluidTank(handler.getTankCapacity(i));
					tank.setFluid(handler.getFluidInTank(i));
					result.add(new FluidInfo(tank));
				}
				return result;
			}

			for (CrossModBase crossMod : CROSS_MODS.values()) {
				List<FluidInfo> list = crossMod.getAllTanks(te);
				if (list != null)
					return list;
			}
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
}
