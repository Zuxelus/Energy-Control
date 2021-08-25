package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.IFluidTank;

public class ItemCardLiquid extends ItemCardMain {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		List<IFluidTank> list = CrossModLoader.getAllTanks(world, target);
		if (list == null || list.size() < 1)
			return CardState.NO_TARGET;

		IFluidTank tank = list.get(0);

		int amount = 0;
		String name = "";
		if (tank.getFluid() != null) {
			amount = tank.getFluidAmount();
			if (amount > 0)
				name = I18n.format(tank.getFluid().getTranslationKey());
		}
		reader.setInt("capacity", tank.getCapacity());
		reader.setInt("amount", amount);
		reader.setString("name", name);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		int capacity = reader.getInt("capacity");
		int amount = reader.getInt("amount");

		if ((settings & 1) > 0) {
			String name = reader.getString("name");
			if (name == "")
				name = isServer ? "N/A" : I18n.format("msg.ec.None");
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
	@OnlyIn(Dist.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<PanelSetting>(5);
		/*result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidName"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidAmount"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidFree"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidCapacity"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidPercentage"), 16, damage));*/
		return result;
	}

	@Override
	public Item getKitFromCard() {
		return ModItems.kit_liquid.get();
	}

	@Override
	protected boolean isRemoteCard() {
		return true;
	}
}
