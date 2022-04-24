package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoloPanelExtender extends HoloPanel {

	@Override
	protected TileEntityFacing createTileEntity(int meta) {
		return new TileEntityHoloPanelExtender();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.getCrossMod(ModIDs.IC2).isWrench(player.getHeldItem(hand)))
			return true;
		if (world.isRemote)
			return true;
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityHoloPanelExtender))
			return true;
		TileEntityInfoPanel panel = ((TileEntityHoloPanelExtender) te).getCore();
		if (panel != null)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_HOLO_PANEL, world, panel.getPos().getX(), panel.getPos().getY(), panel.getPos().getZ());
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
}
