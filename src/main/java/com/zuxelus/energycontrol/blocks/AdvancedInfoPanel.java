package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class AdvancedInfoPanel extends InfoPanel {

	@Override
	protected TileEntityFacing createTileEntity() {
		return ModTileEntityTypes.info_panel_advanced.get().create();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileEntityAdvancedInfoPanel))
			return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

		TileEntityAdvancedInfoPanel te = (TileEntityAdvancedInfoPanel) tile;
		Screen screen = te.getScreen();
		if (screen == null)
			return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

		Direction enumfacing = (Direction) state.get(FACING);
		if (!(te instanceof TileEntityAdvancedInfoPanel) || enumfacing == null)
			return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		switch (enumfacing) {
		case EAST:
			return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, te.thickness, 16.0D, 16.0D);
		case WEST:
			return Block.makeCuboidShape(16.0D - te.thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		case SOUTH:
			return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, te.thickness);
		case NORTH:
			return Block.makeCuboidShape(0.0D, 0.0D, 16.0D - te.thickness, 16.0D, 16.0D, 16.0D);
		case UP:
			return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, te.thickness, 16.0D);
		case DOWN:
			return Block.makeCuboidShape(0.0D, 16.0D - te.thickness, 0.0D, 16.0D, 16.0D, 16.0D);
		default:
			return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return ActionResultType.PASS;
		if (!world.isRemote && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getFace())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getHeldItem(hand), pos, hit.getHitVec()))
				return ActionResultType.SUCCESS;
		if (!world.isRemote)
			NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityAdvancedInfoPanel) te, pos);
		return ActionResultType.SUCCESS;
	}
}
