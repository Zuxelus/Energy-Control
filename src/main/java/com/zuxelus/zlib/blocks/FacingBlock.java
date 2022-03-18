package com.zuxelus.zlib.blocks;

import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class FacingBlock extends DirectionalBlock {
	protected Direction rotation;

	public FacingBlock() {
		super(Block.Properties.of(Material.METAL).strength(1.0F, 3.0F).sound(SoundType.METAL));
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	public FacingBlock(Block.Properties builder) {
		super(builder);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	protected abstract TileEntityFacing createTileEntity();

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		TileEntityFacing te = createTileEntity();
		te.setFacing(state.getValue(FACING).get3DDataValue());
		te.setRotation(rotation);
		return te;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		PlayerEntity placer = context.getPlayer();
		rotation = placer.getDirection().getOpposite();
		if (placer.xRot >= 65)
			return defaultBlockState().setValue(FACING, Direction.UP);
		if (placer.xRot <= -65) {
			rotation = placer.getDirection();
			return defaultBlockState().setValue(FACING, Direction.DOWN);
		}
		switch (MathHelper.floor(placer.yRot * 4.0F / 360.0F + 0.5D) & 3) {
		case 0:
			return defaultBlockState().setValue(FACING, Direction.NORTH);
		case 1:
			return defaultBlockState().setValue(FACING, Direction.EAST);
		case 2:
			return defaultBlockState().setValue(FACING, Direction.SOUTH);
		case 3:
			return defaultBlockState().setValue(FACING, Direction.WEST);
		}
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof IInventory) {
				InventoryHelper.dropContents(world, pos, (IInventory) te);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}
}
