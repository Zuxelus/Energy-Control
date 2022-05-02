package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.zlib.blocks.FacingHorizontal;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class RangeTrigger extends FacingHorizontal {

	@Override
	public TileEntityFacing createTileEntity() {
		return new TileEntityRangeTrigger();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { // 1.7.10
		if (side == meta) {
			if (meta < 6)
				return icons[1];
			return meta > 11 ? icons[4] : icons[3];
		}
		if (side == ForgeDirection.getOrientation(meta).getOpposite().ordinal())
			return icons[0];
		return icons[2];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"range_trigger/back");
		icons[1] = registerIcon(ir,"range_trigger/face_gray");
		icons[2] = registerIcon(ir,"range_trigger/side");
		icons[3] = registerIcon(ir,"range_trigger/face_green");
		icons[4] = registerIcon(ir,"range_trigger/face_red");
	}

		@Override
	public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
		TileEntity te = blockAccess.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityRangeTrigger))
			return 0;
		return ((TileEntityRangeTrigger) te).getPowered() ? 15 : 0;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_RANGE_TRIGGER;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}
}
