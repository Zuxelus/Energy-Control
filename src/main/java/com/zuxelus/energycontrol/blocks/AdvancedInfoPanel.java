package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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

public class AdvancedInfoPanel extends InfoPanel {

	public AdvancedInfoPanel() {
		super(Block.Properties.of(Material.METAL).strength(3.0F).sound(SoundType.METAL).noOcclusion());
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return ModTileEntityTypes.info_panel_advanced.get().create();
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		TileEntity tile = world.getBlockEntity(pos);
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
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return ActionResultType.PASS;
		if (!world.isClientSide && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getDirection())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getItemInHand(hand), pos, hit.getLocation()))
				return ActionResultType.SUCCESS;
		if (!world.isClientSide)
			NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityAdvancedInfoPanel) te, pos);
		return ActionResultType.SUCCESS;
	}
}
