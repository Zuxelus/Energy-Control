package com.zuxelus.energycontrol.api;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IItemCard {
	/**
	 * Called when the card is processed, for example in a Panel or Range Trigerrer.
	 * 
	 * @return The new state of the card
	 * @see CardState
	 */
	CardState update(World world, ICardReader reader, int range, BlockPos pos);

	/**
	 * Used to display data on Info Panels.
	 */
	List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels);

	/**
	 * @return A list of card settings
	 * @see PanelSetting
	 */
	@Environment(EnvType.CLIENT)
	List<PanelSetting> getSettingsList();

	/**
	 * @return Whether the card has a limited range
	 */
	boolean isRemoteCard();
}
