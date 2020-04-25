package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.utils.PanelString;

import net.minecraft.client.resources.I18n;

public class ItemCardType {
	public static final int CARD_ENERGY = 0;
	public static final int CARD_COUNTER = 1;
	public static final int CARD_LIQUID = 2;
	public static final int CARD_LIQUID_ADVANCED = 3;
	public static final int CARD_GENERATOR = 4;
	public static final int CARD_GENERATOR_KINETIC = 5;
	public static final int CARD_GENERATOR_HEAT = 6;
	public static final int CARD_REACTOR = 7;
	public static final int CARD_REACTOR5X5 = 8;
	
	public static final int CARD_TEXT = 18;
	public static final int CARD_TIME = 19;
	
	public static final int CARD_ENERGY_ARRAY = 10;
	public static final int CARD_LIQUID_ARRAY = 11;
	public static final int CARD_GENERATOR_ARRAY = 12;

	public static final int CARD_MAX = 19;
	
	public static void addOnOff(List<PanelString> result, ItemCardReader reader, Boolean value) {
		String text;
		int txtColor = 0;
		if (value) {
			txtColor = 0x00ff00;
			text = I18n.format("msg.ec.InfoPanelOn");
		} else {
			txtColor = 0xff0000;
			text = I18n.format("msg.ec.InfoPanelOff");
		}
		if (result.size() > 0) {
			PanelString firstLine = result.get(0);
			firstLine.textRight = text;
			firstLine.colorRight = txtColor;
		} else {
			PanelString line = new PanelString();
			line.textLeft = text;
			line.colorLeft = txtColor;
			result.add(line);
		}
	}
}
