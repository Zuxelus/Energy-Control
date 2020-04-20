package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.CardState;
import com.zuxelus.energycontrol.utils.PanelSetting;
import com.zuxelus.energycontrol.utils.PanelString;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;

public class ItemCardTime extends ItemCardBase {
	public ItemCardTime() {
		super(ItemCardType.CARD_TIME, "cardTime");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.ItemCardTime";
	}

	@Override
	public CardState update(World world, ItemCardReader reader, int range, BlockPos pos) {
		return CardState.OK;
	}

	@Override
	protected List<PanelString> getStringData(int displaySettings, ItemCardReader reader, boolean showLabels) {
		List<PanelString> result = new ArrayList<PanelString>(1);
		int time = (int) ((FMLClientHandler.instance().getClient().theWorld.getWorldTime() + 6000) % 24000);
		int hours = time / 1000;
		int minutes = (time % 1000) * 6 / 100;
		String suffix = "";
		if ((displaySettings & 1) == 0) {
			suffix = hours < 12 ? "AM" : "PM";
			hours %= 12;
			if (hours == 0)
				hours += 12;
		}
		result.add(new PanelString("msg.ec.InfoPanelTime",String.format("%02d:%02d%s", hours, minutes, suffix),showLabels));
		return result;
	}

	@Override
	protected List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(1);
		result.add(new PanelSetting(I18n.format("msg.ec.cb24h"), 1, damage));
		return result;
	}
}