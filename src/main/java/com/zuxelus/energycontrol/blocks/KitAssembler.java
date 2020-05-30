package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class KitAssembler extends BlockBase {
	private IIcon[] icons = new IIcon[5];

	public KitAssembler() {
		super(BlockDamages.GUI_KIT_ASSEMBER, "kit_assembler");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityKitAssembler te = new TileEntityKitAssembler();
		te.setFacing(0);
		return te;
	}

	@Override
	public IIcon getIconFromSide(int side) {
		switch (side) {
		case 1:
		case 2:
		case 3:
			return icons[side];
		case 6:
			return icons[4];
		default:
			return icons[0];
		}
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		icons[0] = registerIcon(iconRegister,"kit_assembler/side");
		icons[1] = registerIcon(iconRegister,"kit_assembler/face");
		icons[2] = registerIcon(iconRegister,"kit_assembler/top");
		icons[3] = registerIcon(iconRegister,"kit_assembler/bottom");
		icons[4] = registerIcon(iconRegister,"kit_assembler/face_active");
	}
}
