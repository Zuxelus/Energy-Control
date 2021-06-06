package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.getCrossMod(ModIDs.IC2).isWrench(player.getHeldItem(hand)))
			return true;
		if (world.isRemote)
			return true;
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return true;
		TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
		if (panel instanceof TileEntityAdvancedInfoPanel)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_ADVANCED_PANEL, world, panel.getPos().getX(), panel.getPos().getY(), panel.getPos().getZ());
		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileEntity te = source.getTileEntity(pos);
		EnumFacing enumfacing = state.getValue(FACING);
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
