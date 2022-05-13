package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityIndustrialAlarm;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class IndustrialAlarm extends HowlerAlarm {

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityIndustrialAlarm))
			return 0;
		return ((TileEntityIndustrialAlarm)te).lightLevel;
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityIndustrialAlarm();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"industrial_alarm/back");
		icons[1] = registerIcon(ir,"industrial_alarm/face");
		icons[2] = registerIcon(ir,"industrial_alarm/side_hor");
		icons[3] = registerIcon(ir,"industrial_alarm/side_vert");
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_INDUSTRIAL_ALARM;
	}
}
