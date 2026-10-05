package com.zuxelus.energycontrol.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockWithEntity;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AdvancedInfoPanelExtender extends InfoPanelExtender {
	public static final MapCodec<AdvancedInfoPanelExtender> CODEC = createCodec(settings -> new AdvancedInfoPanelExtender());

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return CODEC;
	}

	// the shape follows the core panel thickness, so it must not be cached per block state,
	// and like the core it is not opaque, so neighbours are not culled by a sloped panel
	public AdvancedInfoPanelExtender() {
		super(ModItems.blockSettings().nonOpaque().dynamicBounds());
	}

	@Override
	protected BlockEntityFacing newBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel_advanced_extender.instantiate(pos, state);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient)
			return ActionResult.PASS;
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return ActionResult.PASS;
		TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
		if (panel == null)
			return ActionResult.PASS;
		if (EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) panel).getFacing() == hit.getSide())
			if (((TileEntityInfoPanel) panel).runTouchAction(player.getMainHandStack(), pos, hit.getPos()))
				return ActionResult.SUCCESS;
		player.openHandledScreen(panel); // the extender has no GUI of its own, open the core panel's
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
