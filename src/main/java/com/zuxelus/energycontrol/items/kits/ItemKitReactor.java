package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.item.IC2Items;
import ic2.api.reactor.IReactor;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorAccessHatch;
import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorFluidPort;
import ic2.core.block.reactor.tileentity.TileEntityReactorRedstonePort;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitReactor extends ItemKitBase {
	public ItemKitReactor() {
		super(ItemCardType.KIT_REACTOR, "kit_reactor");
		//addRecipe(new Object[] { "DF", "PW", 'P', Items.PAPER, 'D', StackUtil.copyWithWildCard(new ItemStack(ItemHelper.itemThermometerDigital)), 'F',
				//IC2Items.getItem("frequency_transmitter"), 'W', "dyeYellow" });
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockTileEntity))
			return ItemStack.EMPTY;

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
		return ItemStack.EMPTY;
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
