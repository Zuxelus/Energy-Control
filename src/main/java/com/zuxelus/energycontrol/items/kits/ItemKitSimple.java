package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public abstract class ItemKitSimple extends ItemKitBase {

	public ItemKitSimple(int damage, String textureName) {
		super(damage, textureName);
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
		ChunkCoordinates position = getTargetCoordinates(world, x, y, z, stack);
		if (position == null)
			return null;
			
		ItemStack sensorLocationCard = getItemCard();
		if (sensorLocationCard == null)
			return null;

		ItemStackHelper.setCoordinates(sensorLocationCard, position.posX, position.posY, position.posZ);
		return sensorLocationCard;
	}

	protected abstract ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z, ItemStack stack);

	protected abstract ItemStack getItemCard();
}
