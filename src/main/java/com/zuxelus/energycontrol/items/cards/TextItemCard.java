package com.zuxelus.energycontrol.items.cards;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardGui;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.gui.CardTextScreen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TextItemCard extends MainCardItem {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		boolean started = false;
		for (int i = 9; i >= 0; i--) {
			String text = reader.getString("line_" + i);
			if (text.equals("") && !started)
				continue;
			started = true;
			result.add(0, new PanelString(text));
		}
		String title = reader.getTitle();
		if (title != null && !title.isEmpty()) {
			PanelString titleString = new PanelString();
			titleString.textCenter = title;
			result.add(0, titleString);
		}
		return result;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public ICardGui getSettingsScreen(ItemCardReader reader) {
		return new CardTextScreen(reader);
	}

	@Override
	public int getCardType() {
		return ItemCardType.CARD_TEXT;
	}
}
