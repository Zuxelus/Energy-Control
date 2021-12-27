package com.zuxelus.energycontrol.crossmod.rei;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.containers.ContainerKitAssembler;

import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext;
import me.shedaniel.rei.api.common.transfer.info.simple.SimplePlayerInventoryMenuInfo;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.api.common.transfer.info.stack.VanillaSlotAccessor;

public class KitAssemblerMenuInfo implements SimplePlayerInventoryMenuInfo<ContainerKitAssembler, KitAssemblerDisplay> {
	private final KitAssemblerDisplay display;

	public KitAssemblerMenuInfo(KitAssemblerDisplay display) {
		this.display = display;
	}

	@Override
	public Iterable<SlotAccessor> getInputSlots(MenuInfoContext<ContainerKitAssembler, ?, KitAssemblerDisplay> context) {
		ContainerKitAssembler menu = context.getMenu();

		List<SlotAccessor> list = new ArrayList<>(4);
		for (int i = 1; i < 5; i++)
			list.add(new VanillaSlotAccessor(menu.getSlot(i)));
		return list;
	}

	@Override
	public KitAssemblerDisplay getDisplay() {
		return display;
	}
}
