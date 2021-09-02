package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.ModList;

public class CrossModLoader {
	private static final Map<String, CrossModBase> CROSS_MODS = new HashMap<>();

	public static void init() {
		loadCrossMod(ModIDs.MEKANISM, CrossMekanism::new);
		loadCrossMod(ModIDs.MEKANISM_GENERATORS, CrossMekanismGenerators::new);
	}

	private static void loadCrossMod(String modid, Supplier<? extends CrossModBase> factory) {
		CROSS_MODS.put(modid, ModList.get().isLoaded(modid) ? factory.get() : new CrossModBase());
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
		return null;
	}

	/*
	 * public static ItemStack getGeneratorCard(World world, BlockPos pos) {
	 * TileEntity te = world.getTileEntity(pos); if (te != null) { for (CrossModBase
	 * crossMod : CROSS_MODS.values()) { ItemStack card =
	 * crossMod.getGeneratorCard(te); if (!card.isEmpty()) return card; } } return
	 * ItemStack.EMPTY; }
	 */

	public static List<FluidInfo> getAllTanks(World world, BlockPos pos) {
		TileEntity te = world.getBlockEntity(pos);
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
}
