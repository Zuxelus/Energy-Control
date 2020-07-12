package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.LiquidCardHelper;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardLiquidAdvanced extends ItemCardBase {
	public ItemCardLiquidAdvanced() {
		super(ItemCardType.CARD_LIQUID_ADVANCED, "card_liquid_advanced");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null) 
			return CardState.NO_TARGET;

		List<IFluidTank> tanks = LiquidCardHelper.getAllTanks(world, target);
		if (tanks == null)
			return CardState.NO_TARGET;

		int i = 0;
		for (IFluidTank tank: tanks) {
			addTankInfo(reader, tank, i);
			i++;
		}
		reader.setInt("count", i);
		return CardState.OK;
	}

	private void addTankInfo(ICardReader reader, IFluidTank tank, int i) {
		FluidStack stack = tank.getFluid();
		int amount = 0;
		String name = "";
		if (stack != null) {
			amount = stack.amount;
			if (stack.amount > 0)
				name = FluidRegistry.getFluidName(stack);
		}
		reader.setInt(String.format("_%damount", i), amount);
		reader.setString(String.format("_%dname", i), name);
		reader.setInt(String.format("_%dcapacity", i), tank.getCapacity());
	}

	@Override
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		int count = reader.getInt("count");
		for (int i = 0; i < count; i++)
			addTankData(result, displaySettings, reader, showLabels, i);
		return result;
	}

	private void addTankData(List<PanelString> result, int displaySettings, ICardReader reader, boolean showLabels, int i) {
		if (!reader.hasField(String.format("_%dcapacity", i)))
			return;
		int capacity = reader.getInt(String.format("_%dcapacity", i));
		int amount = reader.getInt(String.format("_%damount", i));

		if ((displaySettings & 1) > 0) {
			String name = reader.getString(String.format("_%dname", i));
			if (name == "")
				name = I18n.format("msg.ec.None");
			result.add(new PanelString("msg.ec.InfoPanelName", name, showLabels));
		}
		if ((displaySettings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelAmountmB", amount, showLabels));
		if ((displaySettings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFreemB", capacity - amount, showLabels));
		if ((displaySettings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacitymB", capacity, showLabels));
		if ((displaySettings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPercentage", capacity == 0 ? 100 : (amount * 100 / capacity), showLabels));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(5);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidName"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidAmount"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidFree"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidCapacity"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidPercentage"), 16, damage));
		return result;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_LIQUID_ADVANCED;
	}
}