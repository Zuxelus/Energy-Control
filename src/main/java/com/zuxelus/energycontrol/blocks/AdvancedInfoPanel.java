package com.zuxelus.energycontrol.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.util.math.random.Random;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AdvancedInfoPanel extends InfoPanel {
	public static final MapCodec<AdvancedInfoPanel> CODEC = createCodec(settings -> new AdvancedInfoPanel());

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return CODEC;
	}

	public AdvancedInfoPanel() {
		// the shape follows the panel thickness, so it must not be cached per block state
		super(FabricBlockSettings.copyOf(ModItems.settings).nonOpaque().dynamicBounds());
	}

	@Override
	protected BlockEntityFacing newBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel_advanced.instantiate(pos, state);
	}

	// on/off depends on the power mode, so the block entity sets ACTIVE (light) instead of the redstone signal
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (world.isClient)
			return;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof TileEntityAdvancedInfoPanel)
			((TileEntityAdvancedInfoPanel) be).updatePower();
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) { }

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof TileEntityAdvancedInfoPanel))
			return VoxelShapes.fullCube();

		// the client gets the screen later than the block entity, so the shape only depends on the thickness
		int thickness = ((TileEntityAdvancedInfoPanel) tile).thickness;
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		Direction enumfacing = (Direction) state.get(FACING);
		if (enumfacing == null)
			return VoxelShapes.fullCube();
		switch (enumfacing) {
		case EAST:
			return Block.createCuboidShape(0.0D, 0.0D, 0.0D, thickness, 16.0D, 16.0D);
		case WEST:
			return Block.createCuboidShape(16.0D - thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		case SOUTH:
			return Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, thickness);
		case NORTH:
			return Block.createCuboidShape(0.0D, 0.0D, 16.0D - thickness, 16.0D, 16.0D, 16.0D);
		case UP:
			return Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, thickness, 16.0D);
		case DOWN:
			return Block.createCuboidShape(0.0D, 16.0D - thickness, 0.0D, 16.0D, 16.0D, 16.0D);
		default:
			return VoxelShapes.fullCube();
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getOutlineShape(state, world, pos, context);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return ActionResult.PASS;
		if (!world.isClient && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getSide())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getMainHandStack(), pos, hit.getPos()))
				return ActionResult.SUCCESS;
		if (!world.isClient)
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
		return ActionResult.SUCCESS;
	}
}
