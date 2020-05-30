package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ICardGui;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.IItemCard;
import com.zuxelus.energycontrol.api.PanelString;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;

public abstract class ItemCardBase implements IItemCard {
	private IIcon icon;
	protected String name;
	protected int damage;
	private Object[] recipe;

	public ItemCardBase(int damage, String name) {
		this.damage = damage;
		this.name = name;
	}

	@Override
	public final int getDamage() {
		return damage;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getUnlocalizedName() {
		return "item." + name;
	}

	public void registerIcon(IIconRegister iconRegister) {
		icon = iconRegister.registerIcon(EnergyControl.MODID + ":" + name);
	}

	public IIcon getIcon() {
		return icon;
	}

	@Override
	public ICardGui getSettingsScreen(ICardReader reader) {
		return null;
	}
	
	@Override
	public boolean isRemoteCard() {
		return true;
	}

	protected ChunkCoordinates getCoordinates(ICardReader reader, int cardNumber) {
		if (cardNumber >= reader.getCardCount())
			return null;
		return new ChunkCoordinates(reader.getInt(String.format("_%dx", cardNumber)),
				reader.getInt(String.format("_%dy", cardNumber)), reader.getInt(String.format("_%dz", cardNumber)));
	}

	@Override
	public Object[] getRecipe() {
		return recipe;
	}

	protected final void addRecipe(Object[] recipe) {
		this.recipe = recipe;
	}
	
	protected void addOnOff(List<PanelString> result, Boolean value) {
		String text;
		int txtColor = 0;
		if (value) {
			txtColor = 0x00ff00;
			if (FMLCommonHandler.instance().getEffectiveSide().isClient())
				text = I18n.format("msg.ec.InfoPanelOn");
			else
				text = "On";
		} else {
			txtColor = 0xff0000;
			if (FMLCommonHandler.instance().getEffectiveSide().isClient())
				text = I18n.format("msg.ec.InfoPanelOff");
			else
				text = "Off";
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
