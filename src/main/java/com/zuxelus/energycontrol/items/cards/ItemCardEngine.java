package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

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

public class ItemCardEngine extends ItemCardBase {

	public ItemCardEngine() {
		super(ItemCardType.CARD_ENGINE, "card_engine");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.BUILDCRAFT).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.getInt("type") == 1) {
			result.add(new PanelString("msg.ec.InfoPanelOutputMJ", reader.getDouble("output"), showLabels));
			if ((settings & 1) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelHeat", reader.getDouble("heat"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelHeatPercentage", reader.getDouble("heatLevel"), showLabels));
			}
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelStorageMJ", reader.getDouble("power"), showLabels));
			if ((settings & 4) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelPercentage", reader.getDouble("powerLevel"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelMaxStorageMJ", reader.getDouble("maxPower"), showLabels));
			}
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelSpeed", reader.getDouble("speed"), showLabels));
		}
		if ((settings & 16) > 0)
			addOnOff(result, isServer, reader.getBoolean("active"));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(5);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelHeat"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelStorage"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMaxStorage"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelSpeed"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 16));
		return result;
	}
}
