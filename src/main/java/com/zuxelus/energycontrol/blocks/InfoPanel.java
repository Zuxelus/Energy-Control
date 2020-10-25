package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityFacing;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInventory;

import ic2.api.util.Keys;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InfoPanel extends FacingBlock {
	EnumFacing rotation;

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityInfoPanel te = new TileEntityInfoPanel();
		te.setFacing(meta);
		if (rotation != null)
			te.setRotation(rotation.getIndex());
		return te;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return 0;
		return ((TileEntityInfoPanel)te).getPowered() ? 10 : 0;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_INFO_PANEL;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return true;
		if (!world.isRemote && Keys.instance.isAltKeyDown(player) && ((TileEntityInfoPanel) te).getFacing() == facing)
			if (((TileEntityInfoPanel) te).runTouchAction(pos, hitX, hitY, hitZ))
				return true;
		return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		rotation = placer.getHorizontalFacing().getOpposite();
		if (placer.rotationPitch >= 65)
			return getDefaultState().withProperty(FACING, EnumFacing.UP);
		if (placer.rotationPitch <= -65) {
			rotation = placer.getHorizontalFacing();
			return getDefaultState().withProperty(FACING, EnumFacing.DOWN);
		}
		switch (MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3) {
		case 0:
			return getDefaultState().withProperty(FACING, EnumFacing.NORTH);
		case 1:
			return getDefaultState().withProperty(FACING, EnumFacing.EAST);
		case 2:
			return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
		case 3:
			return getDefaultState().withProperty(FACING, EnumFacing.WEST);
		}
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(CrossModLoader.ic2.getItemStack("machine"));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!world.isRemote)
			world.notifyBlockUpdate(pos, state, state, 2);
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

	@SideOnly(Side.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}
}