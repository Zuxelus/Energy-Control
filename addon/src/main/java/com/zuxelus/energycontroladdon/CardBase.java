package com.zuxelus.energycontroladdon;

import java.util.List;

import com.zuxelus.energycontrol.api.ICardGui;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.IItemCard;
import com.zuxelus.energycontrol.api.PanelString;

import net.minecraft.client.resources.I18n;

public abstract class CardBase implements IItemCard {
	protected String name;
	protected int id;

	public CardBase(String name, int id) {
		this.name = name;
		this.id = id;
	}

	@Override
	public int getDamage() {
		return id;
	}

	@Override
	public String getName() {
		return EnergyControlAddon.MODID + ':' + name;
	}

	@Override
	public Object[] getRecipe() {
		return null;
	}

	@Override
	public ICardGui getSettingsScreen(ICardReader arg0) {
		return null;
	}

	@Override
	public String getUnlocalizedName() {
		return "item." + name;
	}

	@Override
	public boolean isRemoteCard() {
		return false;
	}

	protected void addOnOff(List<PanelString> result, Boolean value) {
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
