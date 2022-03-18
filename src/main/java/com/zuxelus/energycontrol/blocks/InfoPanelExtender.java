package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;
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
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class InfoPanelExtender extends FacingBlockActive {

	public InfoPanelExtender() {
		super(Block.Properties.of(Material.METAL).strength(1.0F, 3.0F).lightLevel(state -> state.getValue(ACTIVE) ? 10 : 0).sound(SoundType.METAL));
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return ModTileEntityTypes.info_panel_extender.get().create();
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
		NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityInfoPanel) panel, pos);
		return ActionResultType.SUCCESS;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(ACTIVE, false);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}
