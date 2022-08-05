package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.utils.DataHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardEnderIO extends ItemCardBase {

	public ItemCardEnderIO() {
		super(ItemCardType.CARD_ENDER_IO, "card_ender_io");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.ENDER_IO).getCardData(world, target);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.hasField(DataHelper.ENERGY) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getDouble(DataHelper.ENERGY), "�I", showLabels));
		if (reader.hasField(DataHelper.CAPACITY) && (settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getDouble(DataHelper.CAPACITY), "�I", showLabels));
		if (reader.hasField("leakage") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelLeakage", reader.getDouble("leakage"), "�I/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUT) && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUT), "�I/t", showLabels));
		if (reader.hasField("maxInput") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMaxInput", reader.getDouble("maxInput"), "�I/t", showLabels));
		if (reader.hasField("maxOutput") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMaxOutput", reader.getDouble("maxOutput"), "�I/t", showLabels));
		if (reader.hasField(DataHelper.DIFF) && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelDifference", reader.getDouble(DataHelper.DIFF), "�I/t", showLabels));
		if (reader.hasField("usage") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelUsing", reader.getDouble("usage"), "�I/t", showLabels));
		if (reader.hasField(DataHelper.EFFICIENCY) && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEfficiency", reader.getDouble(DataHelper.EFFICIENCY), "%", showLabels));
		if (reader.hasField(DataHelper.TANK) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK), showLabels));
		if (reader.hasField("usage1") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelUsing", reader.getDouble("usage1"), "t/mB", showLabels));
		if (reader.hasField(DataHelper.TANK2) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK2), showLabels));
		if (reader.hasField("usage2") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelUsing", reader.getDouble("usage2"), "t/mB", showLabels));
		if (reader.hasField(DataHelper.ACTIVE))
			addOnOff(result, isServer, reader.getBoolean(DataHelper.ACTIVE));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<>(2);
		return result;
	}
}
