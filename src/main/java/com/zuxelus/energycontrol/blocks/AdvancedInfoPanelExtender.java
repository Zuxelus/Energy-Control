package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class AdvancedInfoPanelExtender extends InfoPanelExtender {

	@Override
	protected TileEntityFacing createTileEntity() {
		return ModTileEntityTypes.info_panel_advanced_extender.get().create();
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (world.isClientSide)
			return ActionResultType.PASS;
		TileEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return ActionResultType.PASS;
		TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
		if (panel == null)
			return ActionResultType.PASS;
		if (EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) panel).getFacing() == hit.getDirection())
			if (((TileEntityInfoPanel) panel).runTouchAction(player.getItemInHand(hand), pos, hit.getLocation()))
				return ActionResultType.SUCCESS;
		NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityAdvancedInfoPanel) panel, pos);
		return ActionResultType.SUCCESS;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		TileEntity te = world.getBlockEntity(pos);
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
