package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

public class CrossModLoader {
	private static final Map<String, CrossModBase> CROSS_MODS = new HashMap<>();
	private static final CrossModBase EMPTY = new CrossModBase();

	public static void init() {
		// Integrations with other mods are not ported to 26.3 yet
		//loadCrossMod(ModIDs.ADV_GENERATORS, CrossAdvGenerators::new);
		//loadCrossMod(ModIDs.APPLIED_ENERGISTICS, CrossAppEng::new);
		//loadCrossMod(ModIDs.BIG_REACTORS, CrossBigReactors::new);
		//loadCrossMod(ModIDs.BIGGER_REACTORS, CrossBiggerReactors::new);
		//loadCrossModSafely(ModIDs.COMPUTER_CRAFT, () -> CrossComputerCraft::new);
		//loadCrossModSafely(ModIDs.IC2, () -> CrossIC2Classic::new);
		//loadCrossModSafely(ModIDs.MEKANISM, () -> CrossMekanism::new);
		//loadCrossModSafely(ModIDs.MEKANISM_GENERATORS, () -> CrossMekanismGenerators::new);
		//loadCrossMod(ModIDs.IMMERSIVE_ENGINEERING, CrossImmersiveEngineering::new);
		//loadCrossModSafely(ModIDs.INDUSTRIAL_REBORN, () -> CrossIndustrialReborn::new);
		//loadCrossMod(ModIDs.THERMAL_EXPANSION, CrossThermalExpansion::new);
	}

	@SuppressWarnings("unused")
	private static void loadCrossMod(String modid, Supplier<? extends CrossModBase> factory) {
		CROSS_MODS.put(modid, ModList.get().isLoaded(modid) ? factory.get() : new CrossModBase());
	}

	@SuppressWarnings("unused")
	private static void loadCrossModSafely(String modid, Supplier<Supplier<? extends CrossModBase>> factory) {
		CROSS_MODS.put(modid, ModList.get().isLoaded(modid) ? factory.get().get() : new CrossModBase());
	}

	public static CrossModBase getCrossMod(String modid) {
		return CROSS_MODS.getOrDefault(modid, EMPTY);
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
		if (te == null)
			return null;
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			CompoundTag tag = crossMod.getEnergyData(te);
			if (tag != null)
				return tag;
		}
		EnergyHandler handler = te.getLevel().getCapability(Capabilities.Energy.BLOCK, te.getBlockPos(), te.getBlockState(), te, null);
		if (handler != null) {
			CompoundTag tag = new CompoundTag();
			tag.putString(DataHelper.EUTYPE, "FE");
			tag.putDouble(DataHelper.ENERGY, handler.getAmountAsLong());
			tag.putDouble(DataHelper.CAPACITY, handler.getCapacityAsLong());
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
		ResourceHandler<FluidResource> handler = world.getCapability(Capabilities.Fluid.BLOCK, pos, te.getBlockState(), te, null);
		if (handler != null) {
			List<FluidInfo> result = new ArrayList<>();
			for (int i = 0; i < handler.size(); i++) {
				FluidResource resource = handler.getResource(i);
				result.add(new FluidInfo(resource, handler.getAmountAsLong(i), handler.getCapacityAsLong(i, resource)));
			}
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
		if (te == null)
			return null;
		for (CrossModBase crossMod : CROSS_MODS.values()) {
			CompoundTag tag = crossMod.getInventoryData(te);
			if (tag != null)
				return tag;
		}
		ResourceHandler<ItemResource> handler = te.getLevel().getCapability(Capabilities.Item.BLOCK, te.getBlockPos(), te.getBlockState(), te, null);
		if (handler == null && !(te instanceof Container))
			return null;
		CompoundTag tag = new CompoundTag();
		if (handler != null) {
			int inUse = 0;
			int items = 0;
			tag.putInt("size", handler.size());
			for (int i = 0; i < Math.min(6, handler.size()); i++) {
				ItemStack stack = ItemUtil.getStack(handler, i);
				if (!stack.isEmpty()) {
					inUse++;
					items += stack.getCount();
				}
				tag.put("slot" + Integer.toString(i), ItemStackHelper.saveOptional(stack, te.getLevel().registryAccess()));
			}
			tag.putInt("used", inUse);
			tag.putInt("items", items);
		}
		if (te instanceof Container) {
			Container inv = (Container) te;
			if (te instanceof BaseContainerBlockEntity)
				tag.putString("name", ((BaseContainerBlockEntity) te).getDisplayName().getString());
			tag.putBoolean("sided", inv instanceof WorldlyContainer);
			if (handler == null) {
				int inUse = 0;
				int items = 0;
				tag.putInt("size", inv.getContainerSize());
				for (int i = 0; i < Math.min(6, inv.getContainerSize()); i++) {
					if (!inv.getItem(i).isEmpty()) {
						inUse++;
						items += inv.getItem(i).getCount();
					}
					tag.put("slot" + Integer.toString(i), ItemStackHelper.saveOptional(inv.getItem(i), te.getLevel().registryAccess()));
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

	public static void onRegister(RegisterEvent event) {
		event.register(Registries.ITEM, helper -> {
			for (CrossModBase crossMod : CROSS_MODS.values())
				crossMod.registerItems(helper);
		});
	}

	public static void addKitsToCreativeTab(CreativeModeTab.Output output) {
		for (CrossModBase crossMod : CROSS_MODS.values())
			crossMod.addKitsToCreativeTab(output);
	}

	public static void addCardsToCreativeTab(CreativeModeTab.Output output) {
		for (CrossModBase crossMod : CROSS_MODS.values())
			crossMod.addCardsToCreativeTab(output);
	}
}
