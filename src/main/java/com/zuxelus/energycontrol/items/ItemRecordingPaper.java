package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.item.Item;

public class ItemRecordingPaper extends Item {

	public ItemRecordingPaper() {
		super();
		setMaxStackSize(1);
		setCreativeTab(EnergyControl.creativeTab);
	}
}
