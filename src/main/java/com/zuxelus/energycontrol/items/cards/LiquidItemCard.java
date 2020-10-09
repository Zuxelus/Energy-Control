package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.LiquidCardHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.common.util.Tank;
import techreborn.blockentity.storage.fluid.TankUnitBaseBlockEntity;

public class LiquidItemCard extends MainCardItem {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		//IFluidTank storage = LiquidCardHelper.getStorageAt(world, target);
		BlockEntity be = world.getBlockEntity(target);
		if (be instanceof TankUnitBaseBlockEntity) {
			Tank tank = ((TankUnitBaseBlockEntity) be).getTank();
			reader.setInt("capacity", tank.getCapacity().getRawValue());
			reader.setInt("amount", tank.getFluidAmount().getRawValue());
			reader.setString("name", LiquidCardHelper.getFluidName(tank.getFluid()));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		int capacity = reader.getInt("capacity");
		int amount = reader.getInt("amount");

		if ((settings & 1) > 0) {
			String name = reader.getString("name");
			if (name == "")
				name = I18n.translate("msg.ec.None");
			result.add(new PanelString("msg.ec.InfoPanelName", name, showLabels));
		}
		if ((settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelAmountmB", amount, showLabels));
		if ((settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFreemB", capacity - amount, showLabels));
		if ((settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacitymB", capacity, showLabels));
		if ((settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPercentage", capacity == 0 ? 100 : (amount * 100 / capacity), showLabels));
		return result;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<PanelSetting>(5);
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelLiquidName"), 1, getCardType()));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelLiquidAmount"), 2, getCardType()));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelLiquidFree"), 4, getCardType()));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelLiquidCapacity"), 8, getCardType()));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelLiquidPercentage"), 16, getCardType()));
		return result;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_LIQUID;
	}

	@Override
	public int getCardType() {
		return ItemCardType.CARD_ENERGY;
	}
}