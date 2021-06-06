package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.wiring.TileEntityElectricMFSU;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemAFSUUpgradeKit extends Item {

	public ItemAFSUUpgradeKit() {
		setCreativeTab(EnergyControl.creativeTab);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (world.isRemote)
			return EnumActionResult.PASS;

		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockTileEntity))
			return EnumActionResult.PASS;
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityElectricMFSU))
			return EnumActionResult.PASS;

		TileEntityElectricMFSU mfsu = ((TileEntityElectricMFSU) te);
		int eustored = mfsu.getStored();
		int facing = mfsu.getFacing().getIndex();
		byte mode = mfsu.redstoneMode;
		ItemStack[] items = new ItemStack[mfsu.getSizeInventory()];
		for (int i = 0; i < items.length; i++)
			items[i] = mfsu.getStackInSlot(i);
		world.removeTileEntity(pos);
		IBlockState state = ModItems.blockAfsu.getStateFromMeta(facing);
		world.setBlockState(pos, state);
		TileEntityAFSU afsu = new TileEntityAFSU();
		afsu.addEnergy(eustored);
		afsu.setFacing(facing);
		afsu.setRedstoneMode(mode);
		for (int j = 0; j < items.length; j++)
			afsu.setInventorySlotContents(j, items[j]);
		world.setTileEntity(pos, afsu);
		afsu.markDirty();
		StackUtil.consumeOrError(player, hand, 1);
		return EnumActionResult.SUCCESS;
	}
}
