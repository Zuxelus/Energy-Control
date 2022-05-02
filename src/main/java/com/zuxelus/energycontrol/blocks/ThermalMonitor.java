package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ThermalMonitor extends FacingBlockSmall {
	protected static final float[] AABB_DOWN = { 0.0625F, 0.5625F, 0.0625F, 0.9375F, 1.0F, 0.9375F };
	protected static final float[] AABB_UP = { 0.0625F, 0.0F, 0.0625F, 0.9375F, 0.4375F, 0.9375F };
	protected static final float[] AABB_NORTH = { 0.0625F, 0.0625F, 0.5625F, 0.9375F, 0.9375F, 1.0F };
	protected static final float[] AABB_SOUTH = { 0.0625F, 0.0625F, 0.0F, 0.9375F, 0.9375F, 0.4375F };
	protected static final float[] AABB_WEST = { 0.5625F, 0.0625F, 0.0625F, 1.0F, 0.9375F, 0.9375F };
	protected static final float[] AABB_EAST = { 0.0F, 0.0625F, 0.0625F, 0.4375F, 0.9375F, 0.9375F };

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityThermalMonitor();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { // 1.7.10
		int id = mapping[meta][side];
		if (id == 1 && meta > 5)
			return meta > 11 ? icons[5] : icons[4];
		return icons[id];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"thermal_monitor/back");
		icons[1] = registerIcon(ir,"thermal_monitor/face_green");
		icons[2] = registerIcon(ir,"thermal_monitor/side_hor");
		icons[3] = registerIcon(ir,"thermal_monitor/side_vert");
		icons[4] = registerIcon(ir,"thermal_monitor/face_red");
		icons[5] = registerIcon(ir,"thermal_monitor/face_gray");
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
		TileEntity te = blockAccess.getTileEntity(x, y, z);
		int meta = blockAccess.getBlockMetadata(x, y, z);
		if (!(te instanceof TileEntityThermalMonitor))
			return 0;
		return ((TileEntityThermalMonitor) te).getPowered() ? side != meta ? 15 : 0 : 0;
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
		return BlockDamages.DAMAGE_THERMAL_MONITOR;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}
}
