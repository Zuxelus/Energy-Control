package com.zuxelus.energycontrol.crossmod;

import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.RegisterEvent;

public class CrossModBase {

	public boolean isElectricItem(ItemStack stack) {
		return false;
	}

	public double dischargeItem(ItemStack stack, int needed, int tier) {
		return 0;
	}

	public CompoundTag getEnergyData(BlockEntity te) {
		return null;
	}

	public CompoundTag getCardData(BlockEntity te) {
		return null;
	}

	public CompoundTag getInventoryData(BlockEntity te) {
		return null;
	}

	public int getReactorHeat(Level world, BlockPos pos) {
		return -1;
	}

	public List<FluidInfo> getAllTanks(BlockEntity te) {
		return null;
	}

	public void registerItems(RegisterEvent.RegisterHelper<Item> event) { }

	public void updateEnergyNet(BlockEntity te, boolean isAdd) { }
}
