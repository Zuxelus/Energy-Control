package com.zuxelus.energycontroladdon;

import com.zuxelus.energycontrol.api.IItemCard;
import com.zuxelus.energycontrol.api.PanelString;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class CardBase extends Item implements IItemCard {
	protected String name;

	public CardBase(String name) {
		this.name = name;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item." + name;
	}

	@Override
	public boolean isRemoteCard(ItemStack stack) {
		return false;
	}

	protected static void addOnOff(List<PanelString> result, Boolean value) {
		String text;
		int color;
		if (value) {
			color = 0x00ff00;
			text = I18n.format("msg.ec.InfoPanelOn");
		} else {
			color = 0xff0000;
			text = I18n.format("msg.ec.InfoPanelOff");
		}
		if (result.size() > 0) {
			PanelString firstLine = result.get(0);
			firstLine.textRight = text;
			firstLine.colorRight = color;
		} else {
			PanelString line = new PanelString();
			line.textLeft = text;
			line.colorLeft = color;
			result.add(line);
		}
	}
}
