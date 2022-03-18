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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class AdvancedInfoPanel extends InfoPanel {

	public AdvancedInfoPanel() {
		super(Block.Properties.of(Material.METAL).strength(1.0F, 3.0F).sound(SoundType.METAL).dynamicShape().noOcclusion());
	}

	@Override
	protected BlockEntityFacing createBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel_advanced.get().create(pos, state);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof TileEntityAdvancedInfoPanel))
			return super.getShape(state, world, pos, context);

		TileEntityAdvancedInfoPanel te = (TileEntityAdvancedInfoPanel) tile;
		Screen screen = te.getScreen();
		if (screen == null)
			return super.getShape(state, world, pos, context);

		Direction enumfacing = state.getValue(FACING);
		if (!(te instanceof TileEntityAdvancedInfoPanel) || enumfacing == null)
			return super.getShape(state, world, pos, context);
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
			return super.getShape(state, world, pos, context);
		}
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
