package com.zuxelus.zlib.blocks;

import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class FacingBlockSmall extends FacingBlock {

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { // 1.7.10
		return icons[mapping[meta][side]];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess blockaccess, int x, int y, int z, int side) { // 1.7.10
		return icons[mapping[blockaccess.getBlockMetadata(x, y, z)][side]];
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		return canPlaceBlock(world, x, y, z, ForgeDirection.getOrientation(side).getOpposite());
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		for (ForgeDirection enumfacing : ForgeDirection.VALID_DIRECTIONS)
			if (canPlaceBlock(world, x, y, z, enumfacing))
				return true;
		return false;
	}

	protected static boolean canPlaceBlock(World world, int x, int y, int z, ForgeDirection direction) {
		return world.isSideSolid(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, direction.getOpposite());
	}

	@Override
	public int getMetaForPlacement(World world, int x, int y, int z, int facing, float hitX, float hitY, float hitZ, int meta, EntityPlayer placer) {
		return canPlaceBlock(world, x, y, z, ForgeDirection.getOrientation(facing).getOpposite()) ? facing : ForgeDirection.DOWN.ordinal();
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		int meta = world.getBlockMetadata(x, y, z);
		if (checkForDrop(world, x, y, z, meta) && !canPlaceBlock(world, x, y, z, ForgeDirection.getOrientation(meta).getOpposite())) {
			dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
	}

	protected boolean checkForDrop(World world, int x, int y, int z, int meta) {
		if (canPlaceBlockAt(world, x, y, z))
			return true;
		dropBlockAsItem(world, x, y, z, meta, 0);
		world.setBlockToAir(x, y, z);
		return false;
	}
}
