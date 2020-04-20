package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.crossmod.LiquidCardHelper;
import com.zuxelus.energycontrol.utils.CardState;
import com.zuxelus.energycontrol.utils.PanelSetting;
import com.zuxelus.energycontrol.utils.PanelString;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class ItemCardLiquid extends ItemCardBase {
	public ItemCardLiquid() {
		super(ItemCardType.CARD_LIQUID, "cardLiquid");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.ItemCardLiquid";
	}

	@Override
	public CardState update(World world, ItemCardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null) 
			return CardState.NO_TARGET;
		
		IFluidTankProperties storage = LiquidCardHelper.getStorageAt(world, target);
		if (storage == null)
			return CardState.NO_TARGET;
		
		int amount = 0;
		String name = "";
		if (storage.getContents() != null) {
			amount = storage.getContents().amount;
			if (amount > 0)
				name = FluidRegistry.getFluidName(storage.getContents());
		}
		reader.setInt("capacity", storage.getCapacity());
		reader.setInt("amount", amount);
		reader.setString("name", name);
		return CardState.OK;
	}

	@Override
	protected List<PanelString> getStringData(int displaySettings, ItemCardReader reader, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		int capacity = reader.getInt("capacity");
		int amount = reader.getInt("amount");

		if ((displaySettings & 1) > 0) {
			String name = reader.getString("name");
			if (name == "")
				name = I18n.format("msg.ec.None");
			result.add(new PanelString("msg.ec.InfoPanelLiquidName", name, showLabels));
		}
		if ((displaySettings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelLiquidAmount", amount, showLabels));
		if ((displaySettings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelLiquidFree", capacity - amount, showLabels));
		if ((displaySettings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelLiquidCapacity", capacity, showLabels));
		if ((displaySettings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelLiquidPercentage", capacity == 0 ? 100 : (amount * 100 / capacity), showLabels));
		return result;
	}

	@Override
	protected List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(5);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidName"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidAmount"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidFree"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidCapacity"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidPercentage"), 16, damage));
		return result;
	}
}