package com.zuxelus.energycontrol.blocks;

import java.util.Random;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class InfoPanel extends FacingBlockActive {

	public InfoPanel() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(3.0F).sound(SoundType.METAL));
	}

	public InfoPanel(Block.Properties builder) {
		super(builder);
	}

	@Override
	public int getLightValue(BlockState state) { // in 1.16 changed to ToIntFunction<>
		return state.get(ACTIVE) ? 10 : 0;
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return ModTileEntityTypes.info_panel.get().create();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return ActionResultType.PASS;
		if (!world.isRemote && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getFace())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getHeldItem(hand), pos, hit.getHitVec()))
				return ActionResultType.SUCCESS;
		if (!world.isRemote)
			NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityInfoPanel) te, pos);
		return ActionResultType.SUCCESS;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(ACTIVE, context.getWorld().isBlockPowered(context.getPos()));
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (world.isRemote)
			return;

		boolean flag = state.get(ACTIVE);
		if (flag == world.isBlockPowered(pos))
			return;

		if (flag)
			world.getPendingBlockTicks().scheduleTick(pos, this, 4);
		else {
			world.setBlockState(pos, state.cycle(ACTIVE), 2);
			updateExtenders(state, world, pos);
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		if (state.get(ACTIVE) && !world.isBlockPowered(pos)) {
			world.setBlockState(pos, state.cycle(ACTIVE), 2);
			updateExtenders(state, world, pos);
		}
	}

	private void updateExtenders(BlockState state, World world, BlockPos pos) {
		TileEntity be = world.getTileEntity(pos);
		if (be instanceof TileEntityInfoPanel)
			((TileEntityInfoPanel) be).updateExtenders(world, !state.get(ACTIVE));
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFacing)
			return side == ((TileEntityFacing) te).getFacing();
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}
}