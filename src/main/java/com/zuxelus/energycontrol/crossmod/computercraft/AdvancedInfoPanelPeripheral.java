package com.zuxelus.energycontrol.crossmod.computercraft;

import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.HashMap;
import java.util.Map;

public class AdvancedInfoPanelPeripheral implements IPeripheral {
	private final TileEntityAdvancedInfoPanel te;

	public AdvancedInfoPanelPeripheral(TileEntityAdvancedInfoPanel te) {
		this.te = te;
	}

	@Override
	public String getType() {
		return TileEntityAdvancedInfoPanel.NAME;
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "isActive", "getRange", "getCardData", "getCardDataRaw", "getColorBack", "getColorText", "setColorBack",
				"setColorText", "getCardTitle", "setCardTitle", "getThickness", "setThickness", "getRotHor",
				"setRotHor", "getRotVert", "setRotVert" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] args) {
		int value;
		ItemStack stack;
		switch (method) {
		case 0:
			return new Object[] { te.powered };
		case 1:
			ItemStack itemStack = te.getStackInSlot(te.getSlotUpgradeRange());
			int upgradeCountRange = 0;
			if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemUpgrade && itemStack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE)
				upgradeCountRange = itemStack.getCount();
			return new Object[] { ItemCardMain.LOCATION_RANGE * (int) Math.pow(2, Math.min(upgradeCountRange, 7)) };
		case 2:
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
		case 3:
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
		case 4:
			return new Object[] { te.getColorBackground() };
		case 5:
			return new Object[] { te.getColorText() };
		case 6:
			value = ArgumentHelper.getInt(args, 0);
			if (value >= 0 && value < 16)
				te.setColorBackground(value);
			return null;
		case 7:
			value = ArgumentHelper.getInt(args, 0);
			if (value >= 0 && value < 16)
				te.setColorText(value);
			return null;
		case 8:
			value = ArgumentHelper.getInt(args, 0);
			if (value < 0 || value > 2)
				return new Object[] { "" };
			stack = te.getStackInSlot(value);
			if (!ItemCardMain.isCard(stack))
				return new Object[] { "" };
			return new Object[] { new ItemCardReader(stack).getTitle() };
		case 9:
			value = ArgumentHelper.getInt(args, 0);
			String title = ArgumentHelper.getString(args, 1);
			if (value < 0 || value > 2)
				return null;
			stack = te.getStackInSlot(value);
			if (ItemCardMain.isCard(stack))
				new ItemCardReader(stack).setTitle(title);
			return null;
		case 10:
			return new Object[] { ((int) te.thickness) };
		case 11:
			value = ArgumentHelper.getInt(args, 0);
			if (value > 0 && value <= 16) {
				te.thickness = (byte) value;
				te.updateTileEntity();
			}
			return null;
		case 12:
			return new Object[] { ((int) te.rotateHor / 7) };
		case 13:
			value = ArgumentHelper.getInt(args, 0);
			if (value > -9 && value < 9) {
				te.rotateHor = (byte) (value * 7);
				te.updateTileEntity();
			}
			return null;
		case 14:
			return new Object[] { ((int) te.rotateVert / 7) };
		case 15:
			value = ArgumentHelper.getInt(args, 0);
			if (value > -9 && value < 9) {
				te.rotateVert = (byte) (value * 7);
				te.updateTileEntity();
			}
			return null;
		}
		return null;
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (other instanceof AdvancedInfoPanelPeripheral && ((AdvancedInfoPanelPeripheral) other).te == this.te);
	}
}
