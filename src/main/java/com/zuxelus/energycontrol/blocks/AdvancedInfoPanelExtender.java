package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AdvancedInfoPanelExtender extends InfoPanelExtender {

	@Override
	protected BlockEntityFacing newBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel_advanced_extender.instantiate(pos, state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient)
			return ActionResult.PASS;
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return ActionResult.PASS;
		TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
		if (panel == null)
			return ActionResult.PASS;
		if (EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) panel).getFacing() == hit.getSide())
			if (((TileEntityInfoPanel) panel).runTouchAction(player.getStackInHand(hand), pos, hit.getPos()))
				return ActionResult.SUCCESS;
		player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
		return ActionResult.SUCCESS;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		BlockEntity te = world.getBlockEntity(pos);
		Direction enumfacing = (Direction) state.get(FACING);
		if (!(te instanceof TileEntityAdvancedInfoPanelExtender) || enumfacing == null)
			return Block.createCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		switch (enumfacing) {
		case EAST:
			return Block.createCuboidShape(0.0D, 0.0D, 0.0D, ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 16.0D, 16.0D);
		case WEST:
			return Block.createCuboidShape(16.0D - ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		case SOUTH:
			return Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, ((TileEntityAdvancedInfoPanelExtender)te).getThickness());
		case NORTH:
			return Block.createCuboidShape(0.0D, 0.0D, 16.0D - ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 16.0D, 16.0D, 16.0D);
		case UP:
			return Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 16.0D);
		case DOWN:
			return Block.createCuboidShape(0.0D, 16.0D - ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 0.0D, 16.0D, 16.0D, 16.0D);
		default:
			return Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		}
	}
}
