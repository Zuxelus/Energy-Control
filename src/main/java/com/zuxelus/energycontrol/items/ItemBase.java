package com.zuxelus.energycontrol.items;

import com.zuxelus.zlib.blocks.FacingBlock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBase extends ItemBlock { // 1.7.10
// In ItemBlock.onItemUse in onBlockPlaced there is no player

	public ItemBase(Block block) {
		super(block);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		if (field_150939_a instanceof FacingBlock)
			meta = ((FacingBlock) field_150939_a).getMetaForPlacement(world, x, y, z, side, hitX, hitY, hitZ, meta, player);
		if (!world.setBlock(x, y, z, field_150939_a, meta, 3))
			return false;

		if (world.getBlock(x, y, z) == field_150939_a) {
			field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
			field_150939_a.onPostBlockPlaced(world, x, y, z, meta);
		}
		return true;
	}
}