package com.zuxelus.energycontrol.containers.slots;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.zlib.containers.slots.SlotFilter;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotCard extends SlotFilter {

	public SlotCard(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return EnergyControl.MODID + ":slots/slot_card";
	}
}
