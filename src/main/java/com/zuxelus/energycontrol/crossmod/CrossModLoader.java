package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
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

public class CrossModLoader {
	public static CrossModBase ic2;
	public static CrossModBase techReborn;
	public static CrossModBase appEng;
	public static CrossModBase bigReactors;
	public static CrossModBase buildCraft;
	public static CrossModBase draconic;
	public static CrossModBase galacticraft;
	public static CrossModBase openComputers;
	public static CrossModBase nuclearCraft;

	public static void init() {
		ic2 = findMod("ic2-classic-spmod", "CrossIC2Classic","ic2", "CrossIC2Exp");
		techReborn = findMod("techreborn", "CrossTechReborn");
		appEng = findMod("appliedenergistics2", "CrossAppEng");
		bigReactors = findMod("bigreactors", "CrossBigReactors");
		buildCraft = findMod("buildcraftcore", "CrossBuildCraft");
		draconic = findMod("draconicevolution", "CrossDraconicEvolution");
		galacticraft = findMod("galacticraftplanets", "CrossGalacticraft");
		nuclearCraft = findMod("nuclearcraft", "CrossNuclearCraft");
		openComputers = findMod("opencomputers",".opencomputers.CrossOpenComputers");
	}

	public static CrossModBase findMod(String modId, String mainClass) {
		return findMod(modId, mainClass, "", "");
	}

	public static CrossModBase findMod(String modId, String mainClass, String modId2, String mainClass2) {
		try {
			if (Loader.isModLoaded(modId)) {
				Class<?> clz = Class.forName("com.zuxelus.energycontrol.crossmod." + mainClass);
				if (clz != null)
					return (CrossModBase) clz.newInstance();
			}
			if (Loader.isModLoaded(modId2)) {
				Class<?> clz = Class.forName("com.zuxelus.energycontrol.crossmod." + mainClass2);
				if (clz != null)
					return (CrossModBase) clz.newInstance();
			}
		} catch (Exception e) { }
		return new CrossModBase();
	}

	public static ItemStack getEnergyCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null)
			return null;
		NBTTagCompound data = getEnergyData(te);
		if (data != null) {
			ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_ENERGY);
			ItemStackHelper.setCoordinates(card, pos);
			return card;
		}
		return null;
	}

	public static NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = ic2.getEnergyData(te);
		if (tag == null)
			tag = techReborn.getEnergyData(te);
		if (tag == null)
			tag = appEng.getEnergyData(te);
		if (tag == null)
			tag = draconic.getEnergyData(te);
		if (tag == null)
			tag = galacticraft.getEnergyData(te);
		return tag;
	}

	public static ItemStack getGeneratorCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null)
			return null;
		ItemStack card = ic2.getGeneratorCard(te);
		if (!card.isEmpty())
			return card;
		card = techReborn.getGeneratorCard(te);
		if (!card.isEmpty())
			return card;
		return buildCraft.getGeneratorCard(te);
	}

	public static List<IFluidTank> getAllTanks(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null)
			return null;

		if (te instanceof IFluidHandler) {
			IFluidTankProperties[] tanks = ((IFluidHandler) te).getTankProperties();
			List<IFluidTank> result = new ArrayList<IFluidTank>();
			for (IFluidTankProperties tank : tanks)
				result.add(new FluidTank(tank.getContents(), tank.getCapacity()));
			return result;
		}

		List<IFluidTank> list = ic2.getAllTanks(te);
		if (list != null)
			return list;
		list = techReborn.getAllTanks(te);
		if (list != null)
			return list;
		list = galacticraft.getAllTanks(te);
		if (list != null)
			return list;
		list = bigReactors.getAllTanks(te);
		if (list != null)
			return list;
		list = nuclearCraft.getAllTanks(te);
		if (list != null)
			return list;
		return buildCraft.getAllTanks(te);
	}

	public static IFluidTank getTankAt(World world, BlockPos pos) {
		List<IFluidTank> tanks = getAllTanks(world, pos);
		if (tanks == null || tanks.size() == 0)
			return null;
		return tanks.iterator().next();
	}
}
