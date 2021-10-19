package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.computercraft.CrossComputerCraft;
import com.zuxelus.energycontrol.crossmod.opencomputers.CrossOpenComputers;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class CrossModLoader {
	private static final Map<String, CrossModBase> CROSS_MODS = new HashMap<>();

	public static void preInit() {
		CROSS_MODS.put(ModIDs.IC2, Loader.isModLoaded("ic2-classic-spmod") ? new CrossModBase() /*CrossIC2Classic*/ : Loader.isModLoaded(ModIDs.IC2) ? new CrossIC2Exp() : new CrossModBase());
		loadCrossMod(ModIDs.TECH_REBORN, CrossTechReborn::new);
		loadCrossMod(ModIDs.APPLIED_ENERGISTICS, CrossAppEng::new);
		loadCrossMod(ModIDs.BIG_REACTORS, CrossBigReactors::new);
		loadCrossMod(ModIDs.BUILDCRAFT, CrossBuildCraft::new);
		loadCrossMod(ModIDs.DRACONIC_EVOLUTION, CrossDraconicEvolution::new);
		loadCrossModSafely(ModIDs.ENDER_IO, () -> CrossEnderIO::new);
		loadCrossMod(ModIDs.GALACTICRAFT_PLANETS, CrossGalacticraft::new);
		loadCrossMod(ModIDs.MEKANISM, CrossMekanism::new);
		loadCrossMod(ModIDs.MEKANISM_GENERATORS, CrossMekanismGenerators::new);
		loadCrossMod(ModIDs.NUCLEAR_CRAFT, CrossNuclearCraft::new);
		loadCrossModSafely(ModIDs.COMPUTER_CRAFT, () -> CrossComputerCraft::new);
		loadCrossMod(ModIDs.PNEUMATICCRAFT, CrossPneumaticCraft::new);
		loadCrossMod(ModIDs.THERMAL_EXPANSION, CrossThermalExpansion::new);
	}

	private static void loadCrossMod(String modid, Supplier<? extends CrossModBase> factory) {
		CROSS_MODS.put(modid, Loader.isModLoaded(modid) ? factory.get() : new CrossModBase());
	}

	private static void loadCrossModSafely(String modid, Supplier<Supplier<? extends CrossModBase>> factory) {
		CROSS_MODS.put(modid, Loader.isModLoaded(modid) ? factory.get().get() : new CrossModBase());
	}

	public static void init() {
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
		IEnergyStorage storage = te.getCapability(CapabilityEnergy.ENERGY, null);
		if (storage != null) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", "FE");
			tag.setDouble("storage", storage.getEnergyStored());
			tag.setDouble("maxStorage", storage.getMaxEnergyStored());
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

	public static List<FluidInfo> getAllTanks(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null)
			return null;
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			List<FluidInfo> list = crossMod.getAllTanks(te);
			if (list != null)
				return list;
		}
		IFluidHandler fluid = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
		if (fluid != null) {
			IFluidTankProperties[] tanks = fluid.getTankProperties();
			List<FluidInfo> result = new ArrayList<>();
			for (IFluidTankProperties tank : tanks)
				result.add(new FluidInfo(tank.getContents(), tank.getCapacity()));
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

	public static NBTTagCompound getInventoryData(TileEntity te) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			NBTTagCompound tag = crossMod.getInventoryData(te);
			if (tag != null)
				return tag;
		}
		IItemHandler storage = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (storage == null && !(te instanceof IInventory))
			return null;
		NBTTagCompound tag = new NBTTagCompound();
		if (storage != null) {
			int inUse = 0;
			int items = 0;
			tag.setInteger("size", storage.getSlots());
			for (int i = 0; i < Math.min(6, storage.getSlots()); i++) {
				if (storage.getStackInSlot(i) != ItemStack.EMPTY) {
					inUse++;
					items += storage.getStackInSlot(i).getCount();
				}
				tag.setTag("slot" + Integer.toString(i), storage.getStackInSlot(i).writeToNBT(new NBTTagCompound()));
			}
			tag.setInteger("used", inUse);
			tag.setInteger("items", items);
		}
		if (te instanceof IInventory) {
			IInventory inv = (IInventory) te;
			tag.setString("name", inv.getName());
			tag.setBoolean("sided", inv instanceof ISidedInventory);
			if (storage == null) {
				int inUse = 0;
				int items = 0;
				tag.setInteger("size", inv.getSizeInventory());
				for (int i = 0; i < Math.min(6, inv.getSizeInventory()); i++) {
					if (inv.getStackInSlot(i) != ItemStack.EMPTY) {
						inUse++;
						items += inv.getStackInSlot(i).getCount();
					}
					tag.setTag("slot" + Integer.toString(i), inv.getStackInSlot(i).writeToNBT(new NBTTagCompound()));
				}
				tag.setInteger("used", inUse);
				tag.setInteger("items", items);
			}
		}
		return tag;
	}
}
