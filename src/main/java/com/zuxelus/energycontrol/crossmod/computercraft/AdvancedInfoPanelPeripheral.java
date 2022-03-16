package com.zuxelus.energycontrol.crossmod.computercraft;

import java.util.List;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;

public class AdvancedInfoPanelPeripheral implements IPeripheral {
	private final TileEntityAdvancedInfoPanel te;

	public AdvancedInfoPanelPeripheral(TileEntityAdvancedInfoPanel te) {
		this.te = te;
	}

	@Override
	public String getType() {
		return TileEntityAdvancedInfoPanel.NAME;
	}

	@LuaFunction
	public final boolean isActive() {
		return te.powered;
	}

	@LuaFunction
	public final int getRange() {
		ItemStack stack = te.getStackInSlot(te.getSlotUpgradeRange());
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
	public final String getCardTitle(int slot) {
		if (slot < 0 || slot > 2)
			return "";
		ItemStack stack = te.getStackInSlot(slot);
		if (!ItemCardMain.isCard(stack))
			return ""; 
		return new ItemCardReader(stack).getTitle();
	}

	@LuaFunction
	public final void setCardTitle(int slot, String title) {
		if (slot < 0 || slot > 2)
			return;
		ItemStack stack = te.getStackInSlot(slot);
		if (title != null && ItemCardMain.isCard(stack))
			new ItemCardReader(stack).setTitle(title);
	}

	@LuaFunction
	public final int getThickness() {
		return te.thickness;
	}

	@LuaFunction
	public final void setThickness(int value) {
		if (value > 0 && value <= 16) {
			te.thickness = (byte) value;
			te.updateTileEntity();
		}
	}

	@LuaFunction
	public final int getRotHor() {
		return te.rotateHor / 7;
	}

	@LuaFunction
	public final void setRotHor(int value) {
		if (value > -9 && value < 9) {
			te.rotateHor = (byte) (value * 7);
			te.updateTileEntity();
		}
	}

	@LuaFunction
	public final int getRotVert() {
		return te.rotateVert / 7;
	}

	@LuaFunction
	public final void setRotVert(int value) {
		if (value > -9 && value < 9) {
			te.rotateVert = (byte) (value * 7);
			te.updateTileEntity();
		}
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (other instanceof AdvancedInfoPanelPeripheral && ((AdvancedInfoPanelPeripheral) other).te == this.te);
	}
}
