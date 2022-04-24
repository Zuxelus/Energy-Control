package com.zuxelus.energycontrol.blocks;

import java.util.Random;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InfoPanel extends FacingBlockActive {

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public int getLightValue(IBlockState state) {
		return state.getValue(ACTIVE) ? 10 : 0;
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityInfoPanel();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return true;
		if (!world.isRemote && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == facing)
			if (((TileEntityInfoPanel) te).runTouchAction(player.getHeldItem(hand), pos, hitX, hitY, hitZ))
				return true;
		return super.onBlockActivated(world, pos, state, player, hand, stack, facing, hitX, hitY, hitZ);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return super.getStateForPlacement(world, pos, facing, hitZ, hitZ, hitZ, meta, placer, stack).withProperty(ACTIVE, world.isBlockPowered(pos));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
		if (world.isRemote)
			return;

		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return;

		((TileEntityInfoPanel) te).updateBlockState(state);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		world.setBlockState(pos, state.cycleProperty(ACTIVE), 2);
		TileEntity be = world.getTileEntity(pos);
		if (be instanceof TileEntityInfoPanel)
			((TileEntityInfoPanel) be).updateExtenders(!state.getValue(ACTIVE));
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFacing)
			return side == ((TileEntityFacing) te).getFacing();
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_INFO_PANEL;
	}

	@SideOnly(Side.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}
}
