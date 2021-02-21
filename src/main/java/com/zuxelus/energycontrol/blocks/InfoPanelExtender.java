package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class InfoPanelExtender extends FacingBlock {

	public InfoPanelExtender() {
		super();
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return ModTileEntityTypes.info_panel_extender.get().create();
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return 0;
		return ((TileEntityInfoPanelExtender)te).getPowered() ? 10 : 0;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (world.isRemote)
			return ActionResultType.PASS;
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return ActionResultType.PASS;
		TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
/*		if (Keys.instance.isAltKeyDown(player) && panel.getFacing() == facing)
			if (panel.runTouchAction(player.getHeldItem(hand), pos, hit.getHitVec()))
				return ActionResultType.SUCCESS;
		if (panel != null)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_INFO_PANEL, world, panel.getPos().getX(), panel.getPos().getY(), panel.getPos().getZ());*/
		return ActionResultType.SUCCESS;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}
