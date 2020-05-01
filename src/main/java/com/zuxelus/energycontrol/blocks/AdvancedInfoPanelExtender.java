package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AdvancedInfoPanelExtender extends InfoPanelExtender {

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityAdvancedInfoPanelExtender te = new TileEntityAdvancedInfoPanelExtender();
		te.setFacing(meta);
		return te;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileEntity te = source.getTileEntity(pos);
		EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);
		if (!(te instanceof TileEntityAdvancedInfoPanelExtender) || enumfacing == null)
			return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		switch (enumfacing) {
		case EAST:
			return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0625D * ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 1.0D, 1.0D);
		case WEST:
			return new AxisAlignedBB(1.0D - 0.0625D * ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		case SOUTH:
			return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0625D * ((TileEntityAdvancedInfoPanelExtender)te).getThickness());
		case NORTH:
			return new AxisAlignedBB(0.0D, 0.0D, 1.0D - 0.0625D * ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 1.0D, 1.0D, 1.0D);
		case UP:
			return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D * ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 1.0D);
		case DOWN:
			return new AxisAlignedBB(0.0D, 1.0D - 0.0625D * ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 0.0D, 1.0D, 1.0D, 1.0D);
		default:
			return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		}
	}
}
