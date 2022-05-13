package com.zuxelus.energycontrol.api;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IItemCard {

	/**
	 * Called when the card is processed, for example in a Panel or Range Trigger.
	 * 
	 * @return The new state of the card
	 * @see CardState
	 */
	CardState update(World world, ICardReader reader, int range, int x, int y, int z);

	/**
	 * Used to display data on Info Panels.
	 */
	List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels);

	/**
	 * @return A list of card settings
	 * @see PanelSetting
	 */
	@SideOnly(Side.CLIENT)
	List<PanelSetting> getSettingsList(ItemStack stack);

	/**
	 * @return Whether the card has a limited range
	 */
	boolean isRemoteCard(ItemStack stack);
}
