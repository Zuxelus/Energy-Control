package com.zuxelus.energycontrol.api;

import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IItemCard {

	CardState update(World world, ICardReader reader, int range, BlockPos pos);

	List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels);

	List<PanelSetting> getSettingsList();

	boolean isRemoteCard();

	int getKitFromCard();

	Object[] getRecipe();
}
