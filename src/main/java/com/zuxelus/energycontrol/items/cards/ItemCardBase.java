package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.api.*;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemCardBase {
	protected String name;
	protected int damage;

	public ItemCardBase(int damage, String name) {
		this.damage = damage;
		this.name = name;
	}

	public final int getDamage() {
		return damage;
	}

	public final String getName() {
		return name;
	}

	public final String getUnlocalizedName() {
		return "item." + name;
	}

	public boolean isRemoteCard() {
		return true;
	}

	public abstract CardState update(World world, ICardReader reader, int range, BlockPos pos);

	public abstract List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels);

	public abstract List<PanelSetting> getSettingsList();

	protected BlockPos getCoordinates(ICardReader reader, int cardNumber) {
		if (cardNumber >= reader.getCardCount())
			return null;
		return new BlockPos(reader.getInt(String.format("_%dx", cardNumber)),
				reader.getInt(String.format("_%dy", cardNumber)), reader.getInt(String.format("_%dz", cardNumber)));
	}

	protected void addHeat(List<PanelString> result, String name, int heat, int maxHeat, boolean showLabels) {
		PanelString line = new PanelString(name, heat, showLabels);
		int rate = maxHeat == 0? 0 : 10 * heat / maxHeat;
		line.colorLeft = rate < 4 ? 0x00ff00 : rate < 8 ? 0xffff00 : 0xff0000;
		result.add(line);
	}

	protected void addOnOff(List<PanelString> result, boolean isServer, boolean value) {
		String text;
		int txtColor;
		if (value) {
			txtColor = 0x00ff00;
			text = isServer ? "On" : I18n.format("msg.ec.InfoPanelOn");
		} else {
			txtColor = 0xff0000;
			text = isServer ? "Off" : I18n.format("msg.ec.InfoPanelOff");
		}
		if (result.size() > 0) {
			PanelString firstLine = result.get(0);
			if (firstLine.textCenter == null) {
				firstLine.textRight = text;
				firstLine.colorRight = txtColor;
				return;
			}
			if (result.size() > 1) {
				firstLine = result.get(1);
				firstLine.textRight = text;
				firstLine.colorRight = txtColor;
				return;
			}
		}
		PanelString line = new PanelString();
		line.textLeft = text;
		line.colorLeft = txtColor;
		result.add(line);
	}
}
