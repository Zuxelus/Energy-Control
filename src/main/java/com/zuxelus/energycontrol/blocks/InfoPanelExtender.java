package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossIC2Exp;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;
import com.zuxelus.energycontrol.utils.KeyboardUtil;
import com.zuxelus.zlib.tileentities.TileEntityFacing;
import ic2.api.util.Keys;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class InfoPanelExtender extends FacingBlock {

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityInfoPanelExtender();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return 0;
		return ((TileEntityInfoPanelExtender)te).getPowered() ? 10 : 0;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_INFO_PANEL;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.getCrossMod(ModIDs.IC2).isWrench(player.getHeldItem(hand)))
			return true;
		if (world.isRemote)
			return true;
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return true;
		TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
		if (KeyboardUtil.isAltKeyDown(player) && panel.getFacing() == facing)
			if (panel.runTouchAction(player.getHeldItem(hand), pos, hitX, hitY, hitZ))
				return true;
		if (panel != null)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_INFO_PANEL, world, panel.getPos().getX(), panel.getPos().getY(), panel.getPos().getZ());
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}
