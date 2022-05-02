package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;
import com.zuxelus.zlib.blocks.FacingHorizontal;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class RemoteThermalMonitor extends FacingHorizontal {

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityRemoteThermalMonitor();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { // 1.7.10
		if (side == meta)
			return icons[1];
		if (side == ForgeDirection.getOrientation(meta).getOpposite().ordinal())
			return icons[0];
		return icons[2];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"remote_thermo/back");
		icons[1] = registerIcon(ir,"remote_thermo/face");
		icons[2] = registerIcon(ir,"remote_thermo/side");
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
		TileEntity te = blockAccess.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityThermalMonitor))
			return 0;
		return ((TileEntityThermalMonitor) te).getPowered() ? 15 : 0;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_REMOTE_THERMO;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}
}
