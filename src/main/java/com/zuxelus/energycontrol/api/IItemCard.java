package com.zuxelus.energycontrol.api;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public interface IItemCard {

	int getDamage();

	String getName();

	String getUnlocalizedName();

	CardState update(World world, ICardReader reader, int range, int x, int y, int z);

	List<PanelString> getStringData(int settings, ICardReader reader, boolean showLabels);

	List<PanelSetting> getSettingsList(ItemStack stack);

	ICardGui getSettingsScreen(ICardReader reader);

	boolean isRemoteCard();

	int getKitFromCard();

	Object[] getRecipe();

	void registerIcon(IIconRegister iconRegister);

	IIcon getIcon();
}
