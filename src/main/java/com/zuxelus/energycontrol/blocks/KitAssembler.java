package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.blocks.FacingHorizontal;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class KitAssembler extends FacingHorizontal {

	@Override
	public TileEntityFacing createTileEntity() {
		return new TileEntityKitAssembler();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { // 1.7.10
		if (side == meta % 6)
			return meta > 5 ? icons[4] : icons[3];
		if (side < 2)
			return icons[side];
		return icons[2];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"kit_assembler/bottom");
		icons[1] = registerIcon(ir,"kit_assembler/top");
		icons[2] = registerIcon(ir,"kit_assembler/side");
		icons[3] = registerIcon(ir,"kit_assembler/face");
		icons[4] = registerIcon(ir,"kit_assembler/face_active");
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.GUI_KIT_ASSEMBER;
	}
}
