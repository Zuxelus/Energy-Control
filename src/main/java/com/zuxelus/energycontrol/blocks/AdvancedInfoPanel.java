package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AdvancedInfoPanel extends InfoPanel {

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityAdvancedInfoPanel();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileEntity tile = source.getTileEntity(pos);
		if (!(tile instanceof TileEntityAdvancedInfoPanel))
			return super.getBoundingBox(state, source, pos);

		TileEntityAdvancedInfoPanel te = (TileEntityAdvancedInfoPanel) tile;
		Screen screen = te.getScreen();
		if (screen == null)
			return super.getBoundingBox(state, source, pos);

		EnumFacing enumfacing = state.getValue(FACING);
		if (!(te instanceof TileEntityAdvancedInfoPanel) || enumfacing == null)
			return super.getBoundingBox(state, source, pos);
		switch (enumfacing) {
		case EAST:
			return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0625D * te.thickness, 1.0D, 1.0D);
		case WEST:
			return new AxisAlignedBB(1.0D - 0.0625D * te.thickness, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		case SOUTH:
			return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0625D * te.thickness);
		case NORTH:
			return new AxisAlignedBB(0.0D, 0.0D, 1.0D - 0.0625D * te.thickness, 1.0D, 1.0D, 1.0D);
		case UP:
			return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D * te.thickness, 1.0D);
		case DOWN:
			return new AxisAlignedBB(0.0D, 1.0D - 0.0625D * te.thickness, 0.0D, 1.0D, 1.0D, 1.0D);
		default:
			return super.getBoundingBox(state, source, pos);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.getCrossMod(ModIDs.IC2).isWrench(player.getHeldItem(hand)))
			return true;
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return true;
		if (!world.isRemote && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == facing)
			if (((TileEntityInfoPanel) te).runTouchAction(player.getHeldItem(hand), pos, hitX, hitY, hitZ))
				return true;
		if (!world.isRemote)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_ADVANCED_PANEL, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}
