package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityHoloPanelExtender extends TileEntityInfoPanelExtender {

	public TileEntityHoloPanelExtender() {
		super();
	}

	// IWrenchable
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player) {
		return new ItemStack(ModItems.blockHoloPanelExtender);
	}
}
