package com.zuxelus.energycontrol.items.cards;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCardBigReactors extends ItemCardMain {
	private static final DecimalFormat df = new DecimalFormat("0.0");

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getBlockEntity(target);
		CompoundNBT tag = CrossModLoader.getCrossMod(ModIDs.BIG_REACTORS).getCardData(te);
		if (tag == null)
			tag = CrossModLoader.getCrossMod(ModIDs.BIGGER_REACTORS).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		switch (reader.getInt("type")) {
		case 1:
			if ((settings & 2) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelCoreHeat", reader.getDouble("heat"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelCasingHeat", reader.getDouble("coreHeat"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelPassiveCooling", reader.getBoolean("cooling").toString(), showLabels));
			}
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergyFE", reader.getString("storage"), showLabels));
			if ((settings & 16) > 0)
				if (reader.getBoolean("cooling"))
					result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("output"), "FE/t", showLabels));
				else
					result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("output"), "mB/t", showLabels));
			if ((settings & 32) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelFuelmb", reader.getString("fuel"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelWastemb", reader.getDouble("waste"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelBurnupRatemb", reader.getDouble("consumption"), showLabels));
			}
			if ((settings & 64) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelFuelRods", reader.getDouble("rods"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
			}
			break;
		case 2:
			if ((settings & 2) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelRotorSpeed", df.format(reader.getDouble("speed")), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelRotorEfficiency", reader.getDouble("efficiency"), "%", showLabels));
			}
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergyFE", reader.getString("storage"), showLabels));
			if ((settings & 16) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("output"), "FE/t", showLabels));
			if ((settings & 32) > 0)
				result.add(new PanelString("msg.ec.InfoPanelFlowRate", reader.getDouble("consumption"), showLabels));
			if ((settings & 64) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelBlades", reader.getInt("blades"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelRotorMass", reader.getInt("mass"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
			}
			break;
		}
		if ((settings & 1) > 0)
			addOnOff(result, isServer, reader.getBoolean("reactorPoweredB"));
		return result;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(6);
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelOnOff"), 1));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelHeat"), 2));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelEnergy"), 4));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelCapacity"), 8));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelOutput"), 16));
		result.add(new PanelSetting(I18n.get("msg.ec.cbFuel"), 32));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelOther"), 64));
		return result;
	}

	@Override
	public boolean isRemoteCard() {
		return true;
	}
}
