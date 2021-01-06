package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import cpw.mods.fml.common.Loader;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class CrossModLoader {
	public static CrossModBase ic2;
	public static CrossModBase techReborn;
	public static CrossModBase appEng;
	public static CrossModBase bigReactors;
	public static CrossModBase draconic;
	public static CrossModBase galacticraft;
	public static CrossModBase openComputers;

	public static void init() {
		ic2 = findMod("IC2-Classic-Spmod", "CrossIC2Classic","IC2", "CrossIC2Exp");
		techReborn = findMod("techreborn", "CrossTechReborn");
		appEng = findMod("appliedenergistics2", "CrossAppEng");
		bigReactors = findMod("BigReactors", "CrossBigReactors");
		draconic = findMod("DraconicEvolution", "CrossDraconicEvolution");
		galacticraft = findMod("GalacticraftMars", "CrossGalacticraft");
		openComputers = findMod("OpenComputers",".opencomputers.CrossOpenComputers");
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

	public static ItemStack getEnergyCard(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null)
			return null;
		NBTTagCompound data = getEnergyData(te);
		if (data != null) {
			ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_ENERGY);
			ItemStackHelper.setCoordinates(card, x, y, z);
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

	public static FluidTankInfo[] getAllTanks(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null)
			return null;
		FluidTankInfo[] list = CrossModLoader.galacticraft.getAllTanks(te);
		if (list != null)
			return list;

		if (te instanceof IFluidHandler)
			return ((IFluidHandler) te).getTankInfo(null);

		list = CrossModLoader.ic2.getAllTanks(te);
		if (list != null)
			return list;
		list = CrossModLoader.techReborn.getAllTanks(te);
		if (list != null)
			return list;
		return CrossModLoader.bigReactors.getAllTanks(te);
	}

	public static FluidTankInfo getTankAt(World world, int x, int y, int z) {
		FluidTankInfo[] tanks = getAllTanks(world, x, y, z);
		if (tanks == null || tanks.length == 0)
			return null;
		return tanks[0];
	}
}
