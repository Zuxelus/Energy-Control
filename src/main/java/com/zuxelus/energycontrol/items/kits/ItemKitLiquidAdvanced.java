package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.LiquidCardHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;

public class ItemKitLiquidAdvanced extends ItemKitBase {
	public ItemKitLiquidAdvanced() {
		super(ItemCardType.KIT_LIQUID_ADVANCED, "kit_liquid_advanced");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos) {
		IFluidTank tank = LiquidCardHelper.getStorageAt(world, pos);
		if (tank != null) {
			ItemStack sensorLocationCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_LIQUID_ADVANCED);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		return CrossModLoader.ic2.getLiquidAdvancedCard(world, pos);
	}
}
