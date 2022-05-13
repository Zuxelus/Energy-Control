package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class HowlerAlarm extends FacingBlockSmall {
	private static final float[] AABB_DOWN = { 0.125F, 0.5625F, 0.125F, 0.875F, 1.0F, 0.875F };
	private static final float[] AABB_UP = { 0.125F, 0.0F, 0.125F, 0.875F, 0.4375F, 0.875F };
	private static final float[] AABB_NORTH = { 0.125F, 0.125F, 0.5625F, 0.875F, 0.875F, 1.0F };
	private static final float[] AABB_SOUTH = { 0.125F, 0.125F, 0.0F, 0.875F, 0.875F, 0.4375F };
	private static final float[] AABB_WEST = { 0.5625F, 0.125F, 0.125F, 1.0F, 0.875F, 0.875F };
	private static final float[] AABB_EAST = { 0.0F, 0.125F, 0.125F, 0.4375F, 0.875F, 0.875F };

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityHowlerAlarm();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"howler_alarm/back");
		icons[1] = registerIcon(ir,"howler_alarm/face");
		icons[2] = registerIcon(ir,"howler_alarm/side_hor");
		icons[3] = registerIcon(ir,"howler_alarm/side_vert");
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		int meta = world.getBlockMetadata(x, y, z);
		if (checkForDrop(world, x, y, z, meta) && !canPlaceBlock(world, x, y, z, ForgeDirection.getOrientation(meta).getOpposite())) {
			dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		} else 
			if (!world.isRemote)
				world.markBlockForUpdate(x, y, z);
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.getCrossMod(ModIDs.IC2).isWrench(player.getHeldItem()))
			return true;
		if (world.isRemote)
			player.openGui(EnergyControl.instance, getBlockGuiId(), world, x, y, z);
		return true;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_HOWLER_ALARM;
	}
}
