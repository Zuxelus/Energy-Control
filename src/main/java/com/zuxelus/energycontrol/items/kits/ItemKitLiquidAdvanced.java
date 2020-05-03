package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.LiquidCardHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.reactor.IReactor;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.reactor.tileentity.TileEntityReactorAccessHatch;
import ic2.core.block.reactor.tileentity.TileEntityReactorFluidPort;
import ic2.core.block.reactor.tileentity.TileEntityReactorRedstonePort;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;

public class ItemKitLiquidAdvanced extends ItemKitBase {
	public ItemKitLiquidAdvanced() {
		super(ItemCardType.KIT_LIQUID_ADVANCED, "kit_liquid_advanced");
		//addRecipe(new Object[] { "BKB", 'B', Items.BUCKET, 'K', new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_LIQUID) });
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos) {
		IFluidTank tank = LiquidCardHelper.getStorageAt(world, pos);
		if (tank != null) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_LIQUID_ADVANCED);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}

		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockTileEntity))
			return ItemStack.EMPTY;
		
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityReactorFluidPort || te instanceof TileEntityReactorRedstonePort
				|| te instanceof TileEntityReactorAccessHatch) {
			BlockPos position = this.get5x5TargetCoordinates(world, pos);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_LIQUID_ADVANCED);
				ItemStackHelper.setCoordinates(sensorLocationCard, position);
				return sensorLocationCard;
			}
		}
		return ItemStack.EMPTY;
	}

	private BlockPos get5x5TargetCoordinates(World world, BlockPos pos) {
		IReactor reactor = ReactorHelper.getReactor3x3(world, pos);
		if (reactor != null)
			return reactor.getPosition();
		return null;
	}
}
