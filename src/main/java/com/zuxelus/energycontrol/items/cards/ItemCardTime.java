package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCardTime extends ItemCardMain {

	@Override
	public List<PanelString> getStringData(World world, int displaySettings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		int time = 0;
		if (world != null)
			time = (int) ((world.getDayTime() + 6000) % 24000);
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
	@OnlyIn(Dist.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<PanelSetting>(1);
		result.add(new PanelSetting(I18n.format("msg.ec.cb24h"), 1, id));
		return result;
	}
}