package com.zuxelus.energycontrol.crossmod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.computercraft.CrossComputerCraft;
import com.zuxelus.energycontrol.crossmod.opencomputers.CrossOpenComputers;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.utils.FluidInfo;

import cpw.mods.fml.common.Loader;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class CrossModLoader {
	private static final Map<String, CrossModBase> CROSS_MODS = new HashMap<>();

	public static void preInit() {
		CROSS_MODS.put(ModIDs.IC2, Loader.isModLoaded(ModIDs.IC2_CLASSIC) ? new CrossModBase() /*CrossIC2Classic*/ : Loader.isModLoaded(ModIDs.IC2) ? new CrossIC2Exp() : new CrossModBase());
		loadCrossMod(ModIDs.GALACTICRAFT_PLANETS, CrossGalacticraft::new);
		loadCrossModSafely(ModIDs.HBM, () -> CrossHBM::new);
		loadCrossModSafely(ModIDs.COMPUTER_CRAFT, () -> CrossComputerCraft::new);
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
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			NBTTagCompound tag = crossMod.getEnergyData(te);
			if (tag != null)
				return tag;
		}
		return null;
	}

	public static ItemStack getGeneratorCard(World world, int x, int y, int z) {
		/*TileEntity te = world.getTileEntity(x, y, z);
		if (te != null) {
			for (CrossModBase crossMod : CROSS_MODS.values()) {
				ItemStack card = crossMod.getGeneratorCard(te);
				if (card != null)
					return card;
			}
		}*/
		return null;
	}

	public static List<FluidInfo> getAllTanks(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null)
			return null;
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			List<FluidInfo> list = crossMod.getAllTanks(te);
			if (list != null)
				return list;
		}
		return null;
	}

	public static FluidInfo getTankAt(World world, int x, int y, int z) {
		List<FluidInfo> tanks = getAllTanks(world, x, y, z);
		return tanks != null && tanks.size() > 0 ? tanks.get(0) : null;
	}

	public static int getHeat(World world, int x, int y, int z) {
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			int heat = crossMod.getHeat(world, x, y, z);
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
		if (te instanceof IInventory) {
			IInventory inv = (IInventory) te;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("name", inv.getInventoryName());
			tag.setBoolean("sided", inv instanceof ISidedInventory);
			int inUse = 0;
			int items = 0;
			tag.setInteger("size", inv.getSizeInventory());
			for (int i = 0; i < Math.min(6, inv.getSizeInventory()); i++) {
				if (inv.getStackInSlot(i) != null) {
					inUse++;
					items += inv.getStackInSlot(i).stackSize;
				}
				tag.setTag("slot" + Integer.toString(i), inv.getStackInSlot(i).writeToNBT(new NBTTagCompound()));
			}
			tag.setInteger("used", inUse);
			tag.setInteger("items", items);
		}
		return null;
	}

	public static boolean isElectricItem(ItemStack stack) {
		if (stack == null)
			return false;

		for (CrossModBase crossMod : CROSS_MODS.values())
			if (crossMod.isElectricItem(stack))
				return true;
		return false;
	}

	public static double dischargeItem(ItemStack stack, double amount) {
		for (CrossModBase crossMod : CROSS_MODS.values())
			if (crossMod.isElectricItem(stack)) {
				double result = crossMod.dischargeItem(stack, amount);
				if (result > 0)
					return result;
			}
		return 0;
	}

	public static void registerItems() {
		for (CrossModBase crossMod : CROSS_MODS.values())
			crossMod.registerItems();
	}

	public static void loadRecipes() {
		for (CrossModBase crossMod : CROSS_MODS.values())
			crossMod.loadRecipes();
	}
}
