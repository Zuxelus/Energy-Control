package com.zuxelus.energycontrol.crossmod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.utils.FluidInfo;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrossModLoader {
	private static final Map<String, CrossModBase> CROSS_MODS = new HashMap<>();

	public static void init() {
		loadCrossMod(ModIDs.TECH_REBORN, CrossTechReborn::new);
	}

	private static void loadCrossMod(String modid, Supplier<? extends CrossModBase> factory) {
		CROSS_MODS.put(modid, FabricLoader.getInstance().getModContainer(modid).isPresent() ? factory.get() : new CrossModBase());
	}

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
		/*Optional<IEnergyStorage> cap = te.getCapability(CapabilityEnergy.ENERGY).resolve();
		if (cap.isPresent()) {
			IEnergyStorage handler = cap.get();
			NbtCompound tag = new NbtCompound();
			tag.putString("euType", "FE");
			tag.putDouble("storage", handler.getEnergyStored());
			tag.putDouble("maxStorage", handler.getMaxEnergyStored());
			return tag;
		}*/
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
		/*Optional<IFluidHandler> fluid = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).resolve();
		if (fluid.isPresent()) {
			IFluidHandler handler = fluid.get();
			List<FluidInfo> result = new ArrayList<>();
			for (int i = 0; i < handler.getTanks(); i++) {
				FluidTank tank = new FluidTank(handler.getTankCapacity(i));
				tank.setFluid(handler.getFluidInTank(i));
				result.add(new FluidInfo(tank));
			}
			return result;
		}*/
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
		if (te instanceof FixedItemInv) {
			FixedItemInv inv = (FixedItemInv) te;
			/*if (te instanceof BaseContainerBlockEntity)
				tag.putString("name", ((BaseContainerBlockEntity) te).getDisplayName().getString());
			tag.putBoolean("sided", inv instanceof WorldlyContainer);*/
			int inUse = 0;
			int items = 0;
			tag.putInt("size", inv.getSlotCount());
			for (int i = 0; i < Math.min(6, inv.getSlotCount()); i++) {
				if (inv.getInvStack(i) != ItemStack.EMPTY) {
					inUse++;
					items += inv.getInvStack(i).getCount();
				}
				tag.put("slot" + Integer.toString(i), inv.getInvStack(i).writeNbt(new NbtCompound()));
			}
			tag.putInt("used", inUse);
			tag.putInt("items", items);
		}
		return tag;
	}
}
