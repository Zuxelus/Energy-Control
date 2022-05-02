package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.utils.FluidInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardLiquidAdvanced extends ItemCardBase {

	public ItemCardLiquidAdvanced() {
		super(ItemCardType.CARD_LIQUID_ADVANCED, "card_liquid_advanced");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		List<FluidInfo> tanks = CrossModLoader.getAllTanks(world, target.posX, target.posY, target.posZ);
		if (tanks == null || tanks.size() < 1)
			return CardState.NO_TARGET;

		int i = 0;
		for (FluidInfo tank: tanks) {
			tank.write(reader, i);
			i++;
		}
		reader.setInt("count", i);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		int count = reader.getInt("count");
		for (int i = 0; i < count; i++)
			addTankData(result, settings, reader, isServer, showLabels, i);
		return result;
	}

	private void addTankData(List<PanelString> result, int settings, ICardReader reader, boolean isServer, boolean showLabels, int i) {
		if (!reader.hasField(String.format("_%dcapacity", i)))
			return;
		int capacity = reader.getInt(String.format("_%dcapacity", i));
		int amount = reader.getInt(String.format("_%damount", i));

		if ((settings & 1) > 0) {
			String name = reader.getString(String.format("_%dname", i));
			if (name.isEmpty())
				name = isServer ? "N/A" : I18n.format("msg.ec.None");
			result.add(new PanelString("msg.ec.InfoPanelName", name, showLabels));
		}
		if ((settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelAmount", amount, "mB", showLabels));
		if ((settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFree", capacity - amount, "mB", showLabels));
		if ((settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", capacity, "mB", showLabels));
		if ((settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPercentage", capacity == 0 ? 100 : (amount * 100 / capacity), showLabels));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(5);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidName"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidAmount"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidFree"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidCapacity"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidPercentage"), 16));
		return result;
	}
}