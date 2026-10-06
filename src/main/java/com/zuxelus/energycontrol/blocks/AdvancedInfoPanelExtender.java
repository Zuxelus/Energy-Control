package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AdvancedInfoPanelExtender extends InfoPanelExtender {
	// the shape follows the core panel thickness, so it must not be cached per block state,
	// and like the core it is not opaque, so neighbours are not culled by a sloped panel
	public AdvancedInfoPanelExtender() {
		super(ModItems.blockSettings().noOcclusion().dynamicShape());
	}

	@Override
	protected BlockEntityFacing createFacingBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel_advanced_extender.create(pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (world.isClientSide())
			return InteractionResult.PASS;
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return InteractionResult.PASS;
		TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
		if (panel == null)
			return InteractionResult.PASS;
		if (EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) panel).getFacing() == hit.getDirection())
			if (((TileEntityInfoPanel) panel).runTouchAction(player.getMainHandItem(), pos, hit.getLocation()))
				return InteractionResult.SUCCESS;
		player.openMenu(panel); // the extender has no GUI of its own, open the core panel's
		return InteractionResult.SUCCESS;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		BlockEntity te = world.getBlockEntity(pos);
		Direction enumfacing = (Direction) state.getValue(FACING);
		if (!(te instanceof TileEntityAdvancedInfoPanelExtender) || enumfacing == null)
			return Block.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		switch (enumfacing) {
		case EAST:
			return Block.box(0.0D, 0.0D, 0.0D, ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 16.0D, 16.0D);
		case WEST:
			return Block.box(16.0D - ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		case SOUTH:
			return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, ((TileEntityAdvancedInfoPanelExtender)te).getThickness());
		case NORTH:
			return Block.box(0.0D, 0.0D, 16.0D - ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 16.0D, 16.0D, 16.0D);
		case UP:
			return Block.box(0.0D, 0.0D, 0.0D, 16.0D, ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 16.0D);
		case DOWN:
			return Block.box(0.0D, 16.0D - ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 0.0D, 16.0D, 16.0D, 16.0D);
		default:
			return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		}
	}
}
