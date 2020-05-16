package com.zuxelus.energycontrol.crossmod.computercraft;

import java.util.List;

import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import dan200.computercraft.api.lua.ArgumentHelper;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class AdvancedInfoPanelPeripheral implements IPeripheral {
	private final TileEntityAdvancedInfoPanel te;

	public AdvancedInfoPanelPeripheral(TileEntityAdvancedInfoPanel te) {
		this.te = te;
	}

	@Override
	public String getType() {
		return te.NAME;
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "isActive", "getRange", "getCardData", "getColorBack", "getColorText", "setColorBack",
				"setColorText", "getCardTitle", "setCardTitle", "getThickness", "setThickness", "getRotHor",
				"setRotHor", "getRotVert", "setRotVert" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] args) throws LuaException, InterruptedException {
		int value;
		ItemStack stack;
		switch (method) {
		case 0:
			return new Object[] { te.powered };
		case 1:
			ItemStack itemStack = te.getStackInSlot(te.getSlotUpgradeRange());
			int upgradeCountRange = 0;
			if (itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof ItemUpgrade && itemStack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE)
				upgradeCountRange = itemStack.getCount();
			return new Object[] { ItemCardMain.LOCATION_RANGE * (int) Math.pow(2, Math.min(upgradeCountRange, 7)) };
		case 2:
			List<PanelString> joinedData = te.getPanelStringList(false);
			List<String> list = NonNullList.create();
			if (joinedData == null || joinedData.size() == 0)
				return new Object[] { list };

			for (PanelString panelString : joinedData) {
				if (panelString.textLeft != null)
					list.add(panelString.textLeft);
				if (panelString.textCenter != null)
					list.add(panelString.textCenter);
				if (panelString.textRight != null)
					list.add(panelString.textRight);
			}
			return new Object[] { list };
		case 3:
			return new Object[] { te.getColorBackground() };
		case 4:
			return new Object[] { te.getColorText() };
		case 5:
			value = ArgumentHelper.getInt(args, 0);
			if (value >= 0 && value < 16)
				te.setColorBackground(value);
			return null;
		case 6:
			value = ArgumentHelper.getInt(args, 0);
			if (value >= 0 && value < 16)
				te.setColorText(value);
			return null;
		case 7:
			value = ArgumentHelper.getInt(args, 0);
			if (value < 0 || value > 2)
				return new Object[] { "" };
			stack = te.getStackInSlot(value);
			if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
				return new Object[] { "" };
			return new Object[] { new ItemCardReader(stack).getTitle() };
		case 8:
			value = ArgumentHelper.getInt(args, 0);
			String title = ArgumentHelper.getString(args, 1);
			if (value < 0 || value > 2)
				return null;
			stack = te.getStackInSlot(value);
			if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain)
				new ItemCardReader(stack).setTitle(title);
			return null;
		case 9:
			return new Object[] { ((int) te.thickness) };
		case 10:
			value = ArgumentHelper.getInt(args, 0);
			if (value > 0 && value <= 16) {
				te.thickness = (byte) value;
				te.notifyBlockUpdate();
			}
			return null;
		case 11:
			return new Object[] { ((int) te.rotateHor / 7) };
		case 12:
			value = ArgumentHelper.getInt(args, 0);
			if (value > -9 && value < 9) {
				te.rotateHor = (byte) (value * 7);
				te.notifyBlockUpdate();
			}
			return null;
		case 13:
			return new Object[] { ((int) te.rotateVert / 7) };
		case 14:
			value = ArgumentHelper.getInt(args, 0);
			if (value > -9 && value < 9) {
				te.rotateVert = (byte) (value * 7);
				te.notifyBlockUpdate();
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
