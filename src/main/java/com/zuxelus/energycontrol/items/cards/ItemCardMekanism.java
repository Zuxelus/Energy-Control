package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemCardMekanism extends ItemCardBase {

	public ItemCardMekanism() {
		super(ItemCardType.CARD_MEKANISM, "card_mekanism");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.MEKANISM).getCardData(te);
		if (tag == null)
			tag = CrossModLoader.getCrossMod(ModIDs.MEKANISM_GENERATORS).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		String euType = reader.getString("euType");
		if (reader.hasField("status") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelStatus", reader.getString("status"), showLabels));
		if (reader.hasField("burn_rate") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelBurnRate", reader.getDouble("burn_rate"), showLabels));
		if (reader.hasField("heat_rate") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHeatRate", reader.getDouble("heat_rate"), showLabels));
		if (reader.hasField("rate_limit") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelRateLimit", reader.getDouble("rate_limit"), showLabels));
		if (reader.hasField("boil_rate") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelBoilRate", reader.getDouble("boil_rate"), showLabels));
		if (reader.hasField("production") && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("production"), euType + "/t", showLabels));
		if (reader.hasField("usage") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelUsing", reader.getDouble("usage"), euType + "/t", showLabels));
		if (reader.hasField("storage") && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getDouble("storage"), euType, showLabels));
		if (reader.hasField("maxStorage") && (settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getDouble("maxStorage"), euType, showLabels));
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
		if (reader.hasField("tank") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank"), showLabels));
		if (reader.hasField("tank2") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank2"), showLabels));
		if (reader.hasField("tank3") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank3"), showLabels));
		if (reader.hasField("tank4") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank4"), showLabels));
		if (reader.hasField("tank5") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank5"), showLabels));
		if (reader.hasField("input") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelInput", reader.getDouble("input"), euType + "/t", showLabels));
		if (reader.hasField("output") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("output"), euType + "/t", showLabels));
		if (reader.hasField("active"))
			addOnOff(result, isServer, reader.getBoolean("active"));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
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
