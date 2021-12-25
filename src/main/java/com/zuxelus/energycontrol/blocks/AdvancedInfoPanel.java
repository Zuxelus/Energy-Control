package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AdvancedInfoPanel extends InfoPanel {

	public AdvancedInfoPanel() {
		super(FabricBlockSettings.copyOf(ModItems.settings).nonOpaque());
	}

	@Override
	protected BlockEntityFacing newBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel_advanced.instantiate(pos, state);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof TileEntityAdvancedInfoPanel))
			return VoxelShapes.fullCube();

		TileEntityAdvancedInfoPanel te = (TileEntityAdvancedInfoPanel) tile;
		Screen screen = te.getScreen();
		if (screen == null)
			return VoxelShapes.fullCube();

		Direction enumfacing = (Direction) state.get(FACING);
		if (!(te instanceof TileEntityAdvancedInfoPanel) || enumfacing == null)
			return VoxelShapes.fullCube();
		switch (enumfacing) {
		case EAST:
			return Block.createCuboidShape(0.0D, 0.0D, 0.0D, te.thickness, 16.0D, 16.0D);
		case WEST:
			return Block.createCuboidShape(16.0D - te.thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		case SOUTH:
			return Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, te.thickness);
		case NORTH:
			return Block.createCuboidShape(0.0D, 0.0D, 16.0D - te.thickness, 16.0D, 16.0D, 16.0D);
		case UP:
			return Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, te.thickness, 16.0D);
		case DOWN:
			return Block.createCuboidShape(0.0D, 16.0D - te.thickness, 0.0D, 16.0D, 16.0D, 16.0D);
		default:
			return VoxelShapes.fullCube();
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getOutlineShape(state, world, pos, context);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return ActionResult.PASS;
		if (!world.isClient && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getSide())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getStackInHand(hand), pos, hit.getPos()))
				return ActionResult.SUCCESS;
		if (!world.isClient)
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
		return ActionResult.SUCCESS;
	}
}
