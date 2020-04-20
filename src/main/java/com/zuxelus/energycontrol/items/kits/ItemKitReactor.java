package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.utils.ItemStackHelper;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.item.IC2Items;
import ic2.api.reactor.IReactor;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.reactor.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitReactor extends ItemKitBase {
	public ItemKitReactor() {
		super(ItemHelper.KIT_REACTOR, "kitReactor");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.ItemKitReactor";
	}
	
	@Override
	protected ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockTileEntity))
			return null;

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityNuclearReactorElectric || te instanceof TileEntityReactorChamberElectric) {
			BlockPos position = this.getTargetCoordinates(world, pos);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_REACTOR);
				ItemStackHelper.setCoordinates(sensorLocationCard, position);
				return sensorLocationCard;
			}
		} else if (te instanceof TileEntityReactorFluidPort || te instanceof TileEntityReactorRedstonePort
				|| te instanceof TileEntityReactorAccessHatch) {
			BlockPos position = this.get5x5TargetCoordinates(world, pos);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_REACTOR5X5);
				ItemStackHelper.setCoordinates(sensorLocationCard, position);
				return sensorLocationCard;
			}
		}
		return null;
	}
	
	private BlockPos getTargetCoordinates(World world, BlockPos pos) {
		IReactor reactor = ReactorHelper.getReactorAt(world, pos);
		if (reactor != null)
			return reactor.getPosition();
		return null;
	}

	private BlockPos get5x5TargetCoordinates(World world, BlockPos pos) {
		IReactor reactor = ReactorHelper.getReactor3x3(world, pos);
		if (reactor != null)
			return reactor.getPosition();
		return null;
	}
}
