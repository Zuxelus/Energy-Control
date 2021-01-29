package com.zuxelus.energycontrol.items.cards;

import java.util.HashMap;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.GuiInfoPanel;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;

public class ItemCardSettingsReader {
	private ItemStack card;
	private TileEntityInfoPanel panel;
	private Map<String, Object> updateSet;
	private GuiInfoPanel gui;
	private int slot;

	public ItemCardSettingsReader(ItemStack card, TileEntityInfoPanel panel, GuiInfoPanel gui, int slot) {
		if (!(card.getItem() instanceof ItemCardMain))
			EnergyControl.logger.error("ItemCardSettingsReader sould be used for ItemCard items.");
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
			ChannelHandler.updateServerCard(card, panel, updateSet, slot);
			updateSet = new HashMap<String, Object>();
		}
	}

	public void closeGui() {
		FMLClientHandler.instance().getClient().displayGuiScreen(gui);
	}
}
