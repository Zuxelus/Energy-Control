package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.computercraft.CrossComputerCraft;
import com.zuxelus.energycontrol.crossmod.opencomputers.CrossOpenComputers;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CrossModLoader {
	private static final Map<String, CrossModBase> CROSS_MODS = new HashMap<>();

	public static void init() {
		CROSS_MODS.put(ModIDs.IC2, Loader.isModLoaded("ic2-classic-spmod") ? new CrossIC2Classic() : Loader.isModLoaded(ModIDs.IC2) ? new CrossIC2Exp() : new CrossModBase());
		loadCrossMod(ModIDs.TECH_REBORN, CrossTechReborn::new);
		loadCrossMod(ModIDs.APPLIED_ENERGISTICS, CrossAppEng::new);
		loadCrossMod(ModIDs.BIG_REACTORS, CrossBigReactors::new);
		loadCrossMod(ModIDs.BUILDCRAFT, CrossBuildCraft::new);
		loadCrossMod(ModIDs.DRACONIC_EVOLUTION, CrossDraconicEvolution::new);
		loadCrossMod(ModIDs.GALACTICRAFT_PLANETS, CrossGalacticraft::new);
		loadCrossMod(ModIDs.MEKANISM_GENERATORS, CrossMekanism::new);
		loadCrossMod(ModIDs.NUCLEAR_CRAFT, CrossNuclearCraft::new);
		loadCrossModSafely(ModIDs.COMPUTER_CRAFT, () -> CrossComputerCraft::new);
		loadCrossMod(ModIDs.THERMAL_EXPANSION, CrossThermalExpansion::new);
	}
	
	private static void loadCrossMod(String modid, Supplier<? extends CrossModBase> factory) {
		CROSS_MODS.put(modid, Loader.isModLoaded(modid) ? factory.get() : new CrossModBase());
	}
	
	private static void loadCrossModSafely(String modid, Supplier<Supplier<? extends CrossModBase>> factory) {
		CROSS_MODS.put(modid, Loader.isModLoaded(modid) ? factory.get().get() : new CrossModBase());
	}

	public static void postInit() {
		loadCrossMod(ModIDs.OPEN_COMPUTERS, CrossOpenComputers::new);
	}
	
	public static CrossModBase getCrossMod(String modid) {
		return CROSS_MODS.get(modid);
	}

	public static ItemStack getEnergyCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null)
			return ItemStack.EMPTY;
		NBTTagCompound data = getEnergyData(te);
		if (data != null) {
			ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_ENERGY);
			ItemStackHelper.setCoordinates(card, pos);
			return card;
		}
		return ItemStack.EMPTY;
	}

	public static NBTTagCompound getEnergyData(TileEntity te) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			NBTTagCompound tag = crossMod.getEnergyData(te);
			if (tag != null) 
				return tag;
		}
		return null;
	}

	public static ItemStack getGeneratorCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null) {
			for (CrossModBase crossMod : CROSS_MODS.values()) {
				ItemStack card = crossMod.getGeneratorCard(te);
				if (!card.isEmpty())
					return card;
			}
		}
		return ItemStack.EMPTY;
	}

	public static List<IFluidTank> getAllTanks(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null) {
			if (te instanceof IFluidHandler) {
				IFluidTankProperties[] tanks = ((IFluidHandler) te).getTankProperties();
				List<IFluidTank> result = new ArrayList<>();
				for (IFluidTankProperties tank : tanks)
					result.add(new FluidTank(tank.getContents(), tank.getCapacity()));
				return result;
			}
					
			for (CrossModBase crossMod : CROSS_MODS.values()) {
				List<IFluidTank> list = crossMod.getAllTanks(te);
				if (list != null)
					return list;
			}
		}

		return null;
	}

	public static IFluidTank getTankAt(World world, BlockPos pos) {
		List<IFluidTank> tanks = getAllTanks(world, pos);
		return tanks != null && tanks.size() > 0 ? tanks.get(0) : null;
	}
}
