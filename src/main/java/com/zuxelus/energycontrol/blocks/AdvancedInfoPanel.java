package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class AdvancedInfoPanel extends InfoPanel {

	public AdvancedInfoPanel() {
		super(Block.Properties.of(Material.METAL).strength(12.0F).noOcclusion());
	}

	@Override
	protected BlockEntityFacing createBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel_advanced.get().create(pos, state);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof TileEntityAdvancedInfoPanel))
			return Shapes.block();

		TileEntityAdvancedInfoPanel te = (TileEntityAdvancedInfoPanel) tile;
		Screen screen = te.getScreen();
		if (screen == null)
			return Shapes.block();

		Direction enumfacing = (Direction) state.getValue(FACING);
		if (!(te instanceof TileEntityAdvancedInfoPanel) || enumfacing == null)
			return Shapes.block();
		switch (enumfacing) {
		case EAST:
			return Block.box(0.0D, 0.0D, 0.0D, te.thickness, 16.0D, 16.0D);
		case WEST:
			return Block.box(16.0D - te.thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		case SOUTH:
			return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, te.thickness);
		case NORTH:
			return Block.box(0.0D, 0.0D, 16.0D - te.thickness, 16.0D, 16.0D, 16.0D);
		case UP:
			return Block.box(0.0D, 0.0D, 0.0D, 16.0D, te.thickness, 16.0D);
		case DOWN:
			return Block.box(0.0D, 16.0D - te.thickness, 0.0D, 16.0D, 16.0D, 16.0D);
		default:
			return Shapes.block();
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return getShape(state, world, pos, context);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return InteractionResult.PASS;
		if (!world.isClientSide && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getDirection())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getItemInHand(hand), pos, hit.getLocation()))
				return InteractionResult.SUCCESS;
		if (!world.isClientSide)
			NetworkHooks.openGui((ServerPlayer) player, (TileEntityAdvancedInfoPanel) te, pos);
		return InteractionResult.SUCCESS;
	}
}
