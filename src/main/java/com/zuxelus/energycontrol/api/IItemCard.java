package com.zuxelus.energycontrol.api;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IItemCard {

	public int getDamage();

	public String getName();

	public String getUnlocalizedName();

	public CardState update(World world, ICardReader reader, int range, BlockPos pos);

	public CardState update(TileEntity panel, ICardReader reader, int range);

	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean showLabels);

	public List<PanelSetting> getSettingsList(ItemStack stack);

	ICardGui getSettingsScreen(ICardReader reader);

	public boolean isRemoteCard(int damage);

	public Object[] getRecipe();
}
