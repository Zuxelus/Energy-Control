package com.zuxelus.energycontrol.items.cards;

import java.util.HashMap;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.screen.InfoPanelScreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class ItemCardSettingsReader {
	private ItemStack card;
	private InfoPanelBlockEntity panel;
	private Map<String, Object> updateSet;
	private InfoPanelScreen gui;
	private byte slot;

	public ItemCardSettingsReader(ItemStack card, InfoPanelBlockEntity panel, InfoPanelScreen gui, byte slot) {
		if (!(card.getItem() instanceof MainCardItem))
			EnergyControl.LOGGER.error("ItemCardSettingsReader sould be used for ItemCard items.");
		this.card = card;
		this.panel = panel;
		updateSet = new HashMap<String, Object>();
		this.gui = gui;
		this.slot = slot;
	}

	public void setInt(String name, Integer value) {
		updateSet.put(name, value);
	}

	public void setDouble(String name, double value) {
		updateSet.put(name, value);
	}

	public void setString(String name, String value) {
		updateSet.put(name, value);
	}

	public void setBoolean(String name, Boolean value) {
		updateSet.put(name, value);
	}

	public void commit() {
		if (!updateSet.isEmpty()) {
			NetworkHelper.setCardSettings(card, panel, updateSet, slot);
			updateSet = new HashMap<String, Object>();
		}
	}

	public void closeGui() {
		gui.prevCard = ItemStack.EMPTY;
		MinecraftClient.getInstance().openScreen(gui);
	}
}
