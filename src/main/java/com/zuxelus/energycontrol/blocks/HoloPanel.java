package com.zuxelus.energycontrol.blocks;

import java.util.Random;

import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.blocks.FacingHorizontal;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class HoloPanel extends FacingHorizontal {
	protected static final float[] AABB_NORTH = { 0.0F, 0.0F, 0.25F, 1.0F, 0.0625F, 0.75F };
	protected static final float[] AABB_WEST = { 0.25F, 0.0F, 0.0F, 0.75F, 0.0625F, 1.0F };

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityHoloPanel();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { // 1.7.10
		boolean isHor = meta % 6 == 2 || meta % 6 == 3;
		if (side == EnumFacing.DOWN.ordinal())
			return isHor ? icons[0] : icons[1];
		if (side == EnumFacing.UP.ordinal()) {
			if (meta > 5)
				return isHor ? icons[4] : icons[5];
			return isHor ? icons[2] : icons[3];
		}
		return icons[6];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"holo_panel/panel_back_hor");
		icons[1] = registerIcon(ir,"holo_panel/panel_back_vert");
		icons[2] = registerIcon(ir,"holo_panel/panel_top_hor");
		icons[3] = registerIcon(ir,"holo_panel/panel_top_vert");
		icons[4] = registerIcon(ir,"holo_panel/panel_top_on_hor");
		icons[5] = registerIcon(ir,"holo_panel/panel_top_on_vert");
		icons[6] = registerIcon(ir,"holo_panel/panel_side");
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		return side == EnumFacing.UP.ordinal() ? canPlaceBlockAt(world, x, y, z) : false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		return block.isSideSolid(world, x, y - 1, z, ForgeDirection.UP);
	}

	@Override
	public int getMetaForPlacement(World world, int x, int y, int z, int facing, float hitX, float hitY, float hitZ, int meta, EntityPlayer placer) {
		int result = super.getMetaForPlacement(world, x, y, z, facing, hitX, hitY, hitZ, meta, placer);
		return world.isBlockIndirectlyGettingPowered(x, y, z) ? result + 6 : result;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		int meta = world.getBlockMetadata(x, y, z);
		if (!canPlaceBlockAt(world, x, y, z)) {
			dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		} else 
			if (!world.isRemote) {
				TileEntity te = world.getTileEntity(x, y, z);
				if (!(te instanceof TileEntityInfoPanel))
					return;

				((TileEntityInfoPanel) te).updateBlockState();
			}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int meta = world.getBlockMetadata(x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, meta > 5 ? meta - 6 : meta + 6, 2);
		TileEntity be = world.getTileEntity(x, y, z);
		if (be instanceof TileEntityInfoPanel)
			((TileEntityInfoPanel) be).updateExtenders(meta > 5);
	}

	@Override
	public float[] getBlockBounds(ForgeDirection dir) {
		switch (dir) {
		case WEST:
		case EAST:
			return AABB_WEST;
		case NORTH:
		case SOUTH:
		default:
			return AABB_NORTH;
		}
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_HOLO_PANEL;
	}
}
