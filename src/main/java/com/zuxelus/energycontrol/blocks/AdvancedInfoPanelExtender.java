package com.zuxelus.energycontrol.blocks;

import com.mojang.serialization.MapCodec;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
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
	public static final MapCodec<AdvancedInfoPanelExtender> CODEC = simpleCodec(AdvancedInfoPanelExtender::new);

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	public AdvancedInfoPanelExtender(Properties properties) {
		// the shape follows the panel thickness, so it must not be cached per block state,
		// and like the core it is not opaque, so neighbours are not culled by a sloped panel
		super(properties.noOcclusion().dynamicShape());
	}

	@Override
	protected BlockEntityFacing createBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel_advanced_extender.get().create(pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.isClientSide())
			return InteractionResult.PASS;
		BlockEntity te = level.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return InteractionResult.PASS;
		TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
		if (panel == null)
			return InteractionResult.PASS;
		if (EnergyControl.altPressed.get(player) && panel.getFacing() == hit.getDirection())
			if (panel.runTouchAction(player.getMainHandItem(), pos, hit.getLocation()))
				return InteractionResult.SUCCESS;
		player.openMenu((TileEntityAdvancedInfoPanel) panel, buf -> buf.writeBlockPos(pos)); // the extender has no GUI of its own, open the core panel's
		return InteractionResult.SUCCESS;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockEntity te = level.getBlockEntity(pos);
		Direction enumfacing = state.getValue(FACING);
		if (!(te instanceof TileEntityAdvancedInfoPanelExtender) || enumfacing == null)
			return super.getShape(state, level, pos, context);

		int thickness = ((TileEntityAdvancedInfoPanelExtender) te).getThickness();
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		switch (enumfacing) {
		case EAST:
			return Block.box(0.0D, 0.0D, 0.0D, thickness, 16.0D, 16.0D);
		case WEST:
			return Block.box(16.0D - thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		case SOUTH:
			return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, thickness);
		case NORTH:
			return Block.box(0.0D, 0.0D, 16.0D - thickness, 16.0D, 16.0D, 16.0D);
		case UP:
			return Block.box(0.0D, 0.0D, 0.0D, 16.0D, thickness, 16.0D);
		case DOWN:
			return Block.box(0.0D, 16.0D - thickness, 0.0D, 16.0D, 16.0D, 16.0D);
		default:
			return super.getShape(state, level, pos, context);
		}
	}
}
