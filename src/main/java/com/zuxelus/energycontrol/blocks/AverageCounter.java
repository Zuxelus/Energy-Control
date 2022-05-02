package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class AverageCounter extends FacingBlock {

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityAverageCounter();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { // 1.7.10
		return side == meta % 6 ? icons[1] : icons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"average_counter/input");
		icons[1] = registerIcon(ir,"average_counter/output");
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_AVERAGE_COUNTER;
	}
}
