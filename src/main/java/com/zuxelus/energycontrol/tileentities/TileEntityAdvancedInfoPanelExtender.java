package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityAdvancedInfoPanelExtender extends TileEntityInfoPanelExtender {

	public byte getThickness() {
		if (screen == null)
			return 16;
		TileEntityInfoPanel core = screen.getCore(worldObj);
		if (!(core instanceof TileEntityAdvancedInfoPanel))
			return 16;
		return ((TileEntityAdvancedInfoPanel) core).thickness;
	}

	public byte getRotateHor() {
		if (screen == null)
			return 0;
		TileEntityInfoPanel core = screen.getCore(worldObj);
		if (!(core instanceof TileEntityAdvancedInfoPanel))
			return 0;
		return ((TileEntityAdvancedInfoPanel) core).rotateHor;
	}

	public byte getRotateVert() {
		if (screen == null)
			return 0;
		TileEntityInfoPanel core = screen.getCore(worldObj);
		if (!(core instanceof TileEntityAdvancedInfoPanel))
			return 0;
		return ((TileEntityAdvancedInfoPanel) core).rotateVert;
	}

	@Override
	public ForgeDirection getRotation() {
		if (screen == null)
			return ForgeDirection.NORTH;
		TileEntityInfoPanel core = screen.getCore(worldObj);
		if (!(core instanceof TileEntityAdvancedInfoPanel))
			return ForgeDirection.NORTH;
		return core.getRotation();
	}

	// IWrenchable
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player) {
		return new ItemStack(ModItems.blockInfoPanelAdvancedExtender);
	}
}
