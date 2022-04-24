package com.zuxelus.energycontrol.blocks;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermo;
import com.zuxelus.energycontrol.tileentities.TileEntityThermo;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RemoteThermo extends FacingHorizontalEC {

	@Override
	public TileEntityFacing createTileEntity(int meta) {
		return new TileEntityRemoteThermo();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}


	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(CrossModLoader.getCrossMod(ModIDs.IC2).getItemStack("machine"));
		return drops;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		TileEntity te = blockAccess.getTileEntity(pos);
		if (!(te instanceof TileEntityThermo))
			return 0;
		return ((TileEntityThermo) te).getPowered() ? 15 : 0;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_REMOTE_THERMO;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@SideOnly(Side.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}
}
