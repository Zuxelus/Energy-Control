package com.zuxelus.energycontrol.containers.slots;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;

public class SlotPower extends SlotFilter {

	public SlotPower(Container inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public int getMaxStackSize() {
		return 16;
	}

	@Override
	public Identifier getNoItemIcon() {
		return Identifier.fromNamespaceAndPath(EnergyControl.MODID, "slots/slot_power");
	}
}
