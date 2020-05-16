package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.reactor.IReactor;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardReactor extends ItemCardBase {
	public ItemCardReactor() {
		super(ItemCardType.CARD_REACTOR, "card_reactor");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null) 
			return CardState.NO_TARGET;
		
		IReactor reactor = ReactorHelper.getReactorAt(world, target);
		if (reactor == null)
			return CardState.NO_TARGET;
		
		return CrossModLoader.ic2.updateCardReactor(world, reader, reactor);
	}

	@Override
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if ((displaySettings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHeat", reader.getInt("heat"), showLabels));
		if ((displaySettings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMaxHeat", reader.getInt("maxHeat"), showLabels));
		if ((displaySettings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMelting", reader.getInt("maxHeat") * 85 / 100, showLabels));
		if ((displaySettings & 16) > 0) {
			if (reader.getBoolean("isSteam")) {
				result.add(new PanelString("msg.ec.InfoPanelOutputSteam", ReactorHelper.euToSteam(reader.getInt("output")), showLabels));
			} else
				result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getInt("output"), showLabels));
		}
		int timeLeft = reader.getInt("timeLeft");
		if ((displaySettings & 32) > 0) {
			int hours = timeLeft / 3600;
			int minutes = (timeLeft % 3600) / 60;
			int seconds = timeLeft % 60;
			result.add(new PanelString("msg.ec.InfoPanelTimeRemaining", String.format("%d:%02d:%02d", hours, minutes, seconds), showLabels));
		}

		if ((displaySettings & 1) > 0)
			addOnOff(result, reader.getBoolean("reactorPoweredB"));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(6);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelHeat"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMaxHeat"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMelting"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 16, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelTimeRemaining"), 32, damage));
		return result;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_REACTOR;
	}
}