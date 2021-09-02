package com.zuxelus.energycontrol.api;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
	@OnlyIn(Dist.CLIENT)
	List<PanelSetting> getSettingsList();

	/**
	 * @return Whether the card has a limited range
	 */
	boolean isRemoteCard();

	/**
	 * @return The corresponding kit of the card, or en empty {@link ItemStack} if it doesn't have one
	 */
	Item getKitFromCard();
}
