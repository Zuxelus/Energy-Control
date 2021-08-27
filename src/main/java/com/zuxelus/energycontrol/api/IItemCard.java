package com.zuxelus.energycontrol.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface IItemCard {
	CardState update(World world, ICardReader reader, int range, BlockPos pos);

	List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels);

	List<PanelSetting> getSettingsList(ItemStack stack);

	boolean isRemoteCard(ItemStack stack);

	ItemStack getKitFromCard(ItemStack stack);
}
