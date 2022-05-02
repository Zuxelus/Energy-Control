package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntitySeedLibrary;
import com.zuxelus.zlib.blocks.FacingHorizontal;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;

public class SeedLibrary extends FacingHorizontal {

	@Override
	public TileEntityFacing createTileEntity() {
		return new TileEntitySeedLibrary();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { // 1.7.10
		if (side == EnumFacing.DOWN.ordinal())
			return icons[0];
		if (side == EnumFacing.UP.ordinal())
			return meta > 5 ? icons[3] : icons[2];
		if (side == meta)
			return meta > 5 ? icons[5] : icons[4];
		return icons[1];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = ir.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_bottom");
		icons[1] = ir.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_side");
		icons[2] = ir.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_top_off");
		icons[3] = ir.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_top_on");
		icons[4] = ir.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_front_off");
		icons[5] = ir.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_front_on");
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_SEED_LIBRARY;
	}
}
