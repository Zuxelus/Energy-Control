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

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardMekanism extends ItemCardBase {

	public ItemCardMekanism() {
		super(ItemCardType.CARD_MEKANISM, "card_mekanism");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.MEKANISM).getCardData(world, target);
		if (tag == null)
			tag = CrossModLoader.getCrossMod(ModIDs.MEKANISM_GENERATORS).getCardData(world, target);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		String euType = reader.getString(DataHelper.EUTYPE);
		if (reader.hasField(DataHelper.STATUS) && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelStatus", reader.getString(DataHelper.STATUS), showLabels));
		if (reader.hasField("burn_rate") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelBurnRate", reader.getDouble("burn_rate"), showLabels));
		if (reader.hasField("heat_rate") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHeatRate", reader.getDouble("heat_rate"), showLabels));
		if (reader.hasField("rate_limit") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelRateLimit", reader.getDouble("rate_limit"), showLabels));
		if (reader.hasField("boil_rate") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelBoilRate", reader.getDouble("boil_rate"), showLabels));
		if (reader.hasField("input") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelInput", reader.getDouble("input"), euType + "/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUT) && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUT), euType + "/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTMB) && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTMB), "mB/t", showLabels));
		if (reader.hasField("usage") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelUsing", reader.getDouble("usage"), euType + "/t", showLabels));
		if (reader.hasField(DataHelper.CONSUMPTIONMB) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble(DataHelper.CONSUMPTIONMB), "mB/t", showLabels));
		if (reader.hasField(DataHelper.ENERGY) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getDouble(DataHelper.ENERGY), euType, showLabels));
		if (reader.hasField(DataHelper.CAPACITY) && (settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getDouble(DataHelper.CAPACITY), euType, showLabels));
		if (reader.hasField(DataHelper.DIFF) && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelDifference", reader.getDouble(DataHelper.DIFF), euType + "/t", showLabels));
		if (reader.hasField("flow_rate") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFlowRate", reader.getDouble("flow_rate"), showLabels));
		if (reader.hasField("max_flow_rate") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMaxFlowRate", reader.getDouble("max_flow_rate"), showLabels));
		if (reader.hasField("injection_rate") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelInjectionRate", reader.getDouble("injection_rate"), showLabels));
		if (reader.hasField("temp") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTemperature", reader.getString("temp"), showLabels));
		if (reader.hasField("plasma") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPlasma", reader.getString("plasma"), showLabels));
		if (reader.hasField("height") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHeight", reader.getInt("height"), showLabels));
		if (reader.hasField(DataHelper.TANK) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK), showLabels));
		if (reader.hasField(DataHelper.TANK2) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK2), showLabels));
		if (reader.hasField(DataHelper.TANK3) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK3), showLabels));
		if (reader.hasField(DataHelper.TANK4) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK4), showLabels));
		if (reader.hasField(DataHelper.TANK5) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK5), showLabels));
		if (reader.hasField(DataHelper.ACTIVE))
			addOnOff(result, isServer, reader.getBoolean(DataHelper.ACTIVE));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<>(4);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelUsing"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelCapacity"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelTank"), 16));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelRate"), 32));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOther"), 64));
		return result;
	}
}
