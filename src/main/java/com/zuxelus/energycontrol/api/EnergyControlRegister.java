package com.zuxelus.energycontrol.api;

import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;

public final class EnergyControlRegister {

	public static void registerKit(IItemKit kit) {
		ItemKitMain.registerKit(kit);
	}

	public static void registerCard(IItemCard kit) {
		ItemCardMain.registerCard(kit);
	}
}
