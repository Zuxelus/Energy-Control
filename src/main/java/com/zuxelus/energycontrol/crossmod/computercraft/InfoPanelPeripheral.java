package com.zuxelus.energycontrol.crossmod.computercraft;

import java.util.HashMap;
import java.util.Map;

import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import cpw.mods.fml.common.Loader;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
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

	@Override
	public String[] getMethodNames() {
		return new String[] { "hasColorUpgrade", "isActive", "getRange", "getCardData", "getCardDataRaw", "getColorBack", "getColorText",
				"setColorBack", "setColorText", "getCardTitle", "setCardTitle" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] args) {
		int value;
		ItemStack stack;
		switch (method) {
		case 0:
			return new Object[] { te.getColored() };
		case 1:
			return new Object[] { te.powered };
		case 2:
			ItemStack itemStack = te.getStackInSlot(te.getSlotUpgradeRange());
			int upgradeCountRange = 0;
			if (itemStack != null && itemStack.getItem() instanceof ItemUpgrade && itemStack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE)
				upgradeCountRange = itemStack.stackSize;
			return new Object[] { ItemCardMain.LOCATION_RANGE * (int) Math.pow(2, Math.min(upgradeCountRange, 7)) };
		case 3:
			if (!Loader.isModLoaded("cctweaked")) {
				Map<Integer, String> map = new HashMap<>();
				int i = 1;
				for (String line : te.getPanelStringList(false)) {
					map.put(i, line);
					i++;
				}
				return new Object[] { map };
			}
			return new Object[] { te.getPanelStringList(false) };
		case 4:
			if (!Loader.isModLoaded("cctweaked")) {
				Map<Integer, String> map = new HashMap<>();
				int i = 1;
				for (String line : te.getPanelStringList(true)) {
					map.put(i, line);
					i++;
				}
				return new Object[] { map };
			}
			return new Object[] { te.getPanelStringList(true) };
		case 5:
			return new Object[] { te.getColorBackground() };
		case 6:
			return new Object[] { te.getColorText() };
		case 7:
			value =  ArgumentHelper.getInt(args, 0);
			if (value >= 0 && value < 16)
				te.setColorBackground(value);
			return null;
		case 8:
			value =  ArgumentHelper.getInt(args, 0);
			if (value >= 0 && value < 16)
				te.setColorText(value);
			return null;
		case 9:
			stack = te.getStackInSlot(0);
			if (!ItemCardMain.isCard(stack))
				return new Object[] { "" }; 
			return new Object[] { new ItemCardReader(stack).getTitle() };
		case 10:
			String title = ArgumentHelper.getString(args, 0);
			stack = te.getStackInSlot(0);
			if (title != null && ItemCardMain.isCard(stack))
				new ItemCardReader(stack).setTitle(title);
			return null;
		}
		return null;
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (other instanceof InfoPanelPeripheral && ((InfoPanelPeripheral) other).te == this.te);
	}

	@Override
	public void attach(IComputerAccess computer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void detach(IComputerAccess computer) {
		// TODO Auto-generated method stub
		
	}
}
