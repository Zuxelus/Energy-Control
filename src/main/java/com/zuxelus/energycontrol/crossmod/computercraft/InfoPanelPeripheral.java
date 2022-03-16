package com.zuxelus.energycontrol.crossmod.computercraft;

import java.util.List;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;

public class InfoPanelPeripheral implements IPeripheral {
	private final TileEntityInfoPanel te;

	public InfoPanelPeripheral(TileEntityInfoPanel te) {
		this.te = te;
	}

	@Override
	public String getType() {
		return TileEntityInfoPanel.NAME;
	}

	@LuaFunction
	public final boolean hasColorUpgrade() {
		return te.getColored();
	}

	@LuaFunction
	public final boolean isActive() {
		return te.powered;
	}

	@LuaFunction
	public final int getRange() {
		ItemStack stack = te.getItem(te.getSlotUpgradeRange());
		int upgradeCountRange = 0;
		if (!stack.isEmpty() && stack.getItem().equals(ModItems.upgrade_range.get()))
			upgradeCountRange = stack.getCount();
		return ItemCardMain.LOCATION_RANGE * (int) Math.pow(2, Math.min(upgradeCountRange, 7));
	}

	@LuaFunction
	public final List<String> getCardData() {
		return te.getPanelStringList(false);
	}

	@LuaFunction
	public final List<String> getCardDataRaw() {
		return te.getPanelStringList(true);
	}

	@LuaFunction
	public final int getColorBack() {
		return te.getColorBackground();
	}

	@LuaFunction
	public final int getColorText() {
		return te.getColorText();
	}

	@LuaFunction
	public final void setColorBack(int value) {
		if (value >= 0 && value < 16)
			te.setColorBackground(value);
	}

	@LuaFunction
	public final void setColorText(int value) {
		if (value >= 0 && value < 16)
			te.setColorText(value);
	}

	@LuaFunction
	public final String getCardTitle() {
		ItemStack stack = te.getItem(0);
		if (!ItemCardMain.isCard(stack))
			return ""; 
		return new ItemCardReader(stack).getTitle();
	}

	@LuaFunction
	public final void setCardTitle(String title) {
		ItemStack stack = te.getItem(0);
		if (title != null && ItemCardMain.isCard(stack))
			new ItemCardReader(stack).setTitle(title);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (other instanceof InfoPanelPeripheral && ((InfoPanelPeripheral) other).te == this.te);
	}
}
