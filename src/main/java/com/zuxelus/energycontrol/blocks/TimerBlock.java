package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TimerBlock extends FacingBlockSmall {
	protected static final float[] AABB_DOWN = { 0.0625F, 0.5625F, 0.0625F, 0.9375F, 1.0F, 0.9375F };
	protected static final float[] AABB_UP = { 0.0625F, 0.0F, 0.0625F, 0.9375F, 0.4375F, 0.9375F };
	protected static final float[] AABB_NORTH = { 0.0625F, 0.0625F, 0.5625F, 0.9375F, 0.9375F, 1.0F };
	protected static final float[] AABB_SOUTH = { 0.0625F, 0.0625F, 0.0F, 0.9375F, 0.9375F, 0.4375F };
	protected static final float[] AABB_WEST = { 0.5625F, 0.0625F, 0.0625F, 1.0F, 0.9375F, 0.9375F };
	protected static final float[] AABB_EAST = { 0.0F, 0.0625F, 0.0625F, 0.4375F, 0.9375F, 0.9375F };

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityTimer();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"timer/back");
		icons[1] = registerIcon(ir,"timer/face");
		icons[2] = registerIcon(ir,"timer/side_hor");
		icons[3] = registerIcon(ir,"timer/side_vert");
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
		TileEntity te = blockAccess.getTileEntity(x, y, z);
		int meta = blockAccess.getBlockMetadata(x, y, z);
		if (!(te instanceof TileEntityTimer))
			return 0;
		if (side == meta || side == ((TileEntityTimer) te).getRotation().getOpposite().ordinal())
			return 0;
		return ((TileEntityTimer) te).getPowered() ? 15 : 0;
	}

	@Override
	public float[] getBlockBounds(ForgeDirection dir) {
		switch (dir) {
		case EAST:
			return AABB_EAST;
		case WEST:
			return AABB_WEST;
		case SOUTH:
			return AABB_SOUTH;
		case NORTH:
		default:
			return AABB_NORTH;
		case UP:
			return AABB_UP;
		case DOWN:
			return AABB_DOWN;
		}
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_TIMER;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}


	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		if (!world.isRemote) {
			TileEntity be = world.getTileEntity(x, y, z);
			if (be instanceof TileEntityTimer)
				((TileEntityTimer) be).onNeighborChange();
		}
	}
}
