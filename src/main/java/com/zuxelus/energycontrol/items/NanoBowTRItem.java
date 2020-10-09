package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyTier;
import techreborn.utils.InitUtils;

public class NanoBowTRItem extends NanoBowItem implements EnergyHolder, ItemDurabilityExtensions {
	static double maxCharge = 40000;
	static EnergyTier tier = EnergyTier.LOW;

	public NanoBowTRItem() {

	}

	@Override
	public boolean canRepair(ItemStack itemStack_1, ItemStack itemStack_2) {
		return false;
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (!isIn(group)) {
			return;
		}
		InitUtils.initPoweredItems(this, stacks);
	}

	// ItemDurabilityExtensions
	@Override
	public double getDurability(ItemStack stack) {
		return 1 - ItemUtils.getPowerForDurabilityBar(stack);
	}

	@Override
	public boolean showDurability(ItemStack stack) {
		return true;
	}

	@Override
	public int getDurabilityColor(ItemStack stack) {
		return PowerSystem.getDisplayPower().colour;
	}

	// EnergyHolder
	@Override
	public double getMaxStoredPower() {
		return maxCharge;
	}

	@Override
	public EnergyTier getTier() {
		return tier;
	}

	@Override
	public double getMaxOutput(EnergySide side) {
		return 0;
	}

	@Override
	protected void discharge(ItemStack stack, double amount, LivingEntity entity) {
		Energy.of(stack).use(amount);
	}

	@Override
	protected boolean canUse(ItemStack stack, double amount) {
		return Energy.of(stack).getEnergy() >= amount;
	}

	@Override
	protected boolean isModeSwitchKeyDown(PlayerEntity player) {
		return EnergyControl.modeSwitchKeyPressed.get(player.getGameProfile().getId());
	}
}
