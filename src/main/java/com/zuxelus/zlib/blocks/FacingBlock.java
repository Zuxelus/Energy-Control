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
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(3.0F).sound(SoundType.METAL));
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

	public FacingBlock(Block.Properties builder) {
		super(builder);
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

	protected abstract TileEntityFacing createTileEntity();

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		TileEntityFacing te = createTileEntity();
		te.setFacing(state.get(FACING).getIndex());
		te.setRotation(rotation);
		return te;
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		PlayerEntity placer = context.getPlayer();
		rotation = placer.getHorizontalFacing().getOpposite();
		if (placer.rotationPitch >= 65)
			return getDefaultState().with(FACING, Direction.UP);
		if (placer.rotationPitch <= -65) {
			rotation = placer.getHorizontalFacing();
			return getDefaultState().with(FACING, Direction.DOWN);
		}
		switch (MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3) {
		case 0:
			return getDefaultState().with(FACING, Direction.NORTH);
		case 1:
			return getDefaultState().with(FACING, Direction.EAST);
		case 2:
			return getDefaultState().with(FACING, Direction.SOUTH);
		case 3:
			return getDefaultState().with(FACING, Direction.WEST);
		}
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof IInventory) {
				InventoryHelper.dropInventoryItems(world, pos, (IInventory) te);
				world.updateComparatorOutputLevel(pos, this);
			}
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}
}
