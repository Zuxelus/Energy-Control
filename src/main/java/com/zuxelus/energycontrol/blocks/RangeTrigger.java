package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class RangeTrigger extends BlockBase {

	private IIcon[] icons = new IIcon[5];

	public RangeTrigger() {
		super(BlockDamages.DAMAGE_RANGE_TRIGGER, "range_trigger");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityRangeTrigger te = new TileEntityRangeTrigger();
		te.setFacing(0);
		return te;
	}

	@Override
	public IIcon getIconFromSide(int side) {
		if (side == 6)
			return icons[3];
		if (side == 7)
			return icons[4];
		if (side > 1)
			return icons[2];
		return icons[side];
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		icons[0] = registerIcon(iconRegister,"range_trigger/back");
		icons[1] = registerIcon(iconRegister,"range_trigger/face_gray");
		icons[2] = registerIcon(iconRegister,"range_trigger/side");
		icons[3] = registerIcon(iconRegister,"range_trigger/face_green");
		icons[4] = registerIcon(iconRegister,"range_trigger/face_red");
	}
}
