package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;

import ic2.api.util.Keys;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class InfoPanelExtender extends FacingBlock {

	public InfoPanelExtender() {
		super();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityInfoPanelExtender te = new TileEntityInfoPanelExtender();
		te.setFacing(meta);
		return te;
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.ic2.isWrench(player.getHeldItem(hand)))
			return true;
		if (world.isRemote)
			return true;
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return true;
		TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
		if (Keys.instance.isAltKeyDown(player) && panel.getFacing() == facing)
			if (panel.runTouchAction(pos, hitX, hitY, hitZ))
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
