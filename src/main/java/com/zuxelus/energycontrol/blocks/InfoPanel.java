package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class InfoPanel extends FacingBlock {

	public InfoPanel() {
		super();
	}

	public InfoPanel(Block.Properties builder) {
		super(builder);
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return ModTileEntityTypes.info_panel.get().create();
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		TileEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return 0;
		return ((TileEntityInfoPanel)te).getPowered() ? 10 : 0;
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return ActionResultType.PASS;
		if (!world.isClientSide && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getDirection())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getItemInHand(hand), pos, hit.getLocation()))
				return ActionResultType.SUCCESS;
		if (!world.isClientSide)
				NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityInfoPanel) te, pos);
		return ActionResultType.SUCCESS;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!world.isClientSide)
			world.sendBlockUpdated(pos, state, state, 2);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof TileEntityFacing)
			return side == ((TileEntityFacing) te).getFacing();
		return false;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}
}