package com.zuxelus.energycontrol.items.cards;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.network.NetworkHelper;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardReader implements ICardReader {
	private ItemStack card;
	private Map<String, Object> updateSet;

	public ItemCardReader(ItemStack card) {
		if (!(card.getItem() instanceof ItemCardMain))
			EnergyControl.logger.error("CardReader should be used for card items only.");
		this.card = card;
		updateSet = new HashMap<String, Object>();
	}

	@Override
	public BlockPos getTarget() {
		NBTTagCompound nbtTagCompound = card.getTagCompound();
		if (nbtTagCompound == null)
			return null;

		return new BlockPos(nbtTagCompound.getInteger("x"), nbtTagCompound.getInteger("y"), nbtTagCompound.getInteger("z"));
	}

	@Override
	public void setInt(String name, Integer value) {
		NBTTagCompound nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.hasKey(name)) {
			Integer prevValue = nbtTagCompound.getInteger(name);
			if (prevValue == null || !prevValue.equals(value))
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		nbtTagCompound.setInteger(name, value);
	}

	@Override
	public Integer getInt(String name) {
		NBTTagCompound nbtTagCompound = card.getTagCompound();
		if (nbtTagCompound == null)
			return 0;
		return nbtTagCompound.getInteger(name);
	}

	@Override
	public void setLong(String name, Long value) {
		NBTTagCompound nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.hasKey(name)) {
			Long prevValue = nbtTagCompound.getLong(name);
			if (prevValue == null || !prevValue.equals(value))
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		nbtTagCompound.setLong(name, value);
	}

	@Override
	public Long getLong(String name) {
		NBTTagCompound nbtTagCompound = card.getTagCompound();
		if (nbtTagCompound == null)
			return 0L;
		return nbtTagCompound.getLong(name);
	}

	@Override
	public void setDouble(String name, Double value) {
		NBTTagCompound nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.hasKey(name)) {
			Double prevValue = nbtTagCompound.getDouble(name);
			if (prevValue == null || prevValue != value)
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		nbtTagCompound.setDouble(name, value);
	}

	@Override
	public Double getDouble(String name) {
		NBTTagCompound nbtTagCompound = card.getTagCompound();
		if (nbtTagCompound == null)
			return 0.0;
		return nbtTagCompound.getDouble(name);
	}

	@Override
	public void setString(String name, String value) {
		if (name == null)
			return;
		NBTTagCompound nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.hasKey(name)) {
			String prevValue = nbtTagCompound.getString(name);
			if (prevValue == null || !prevValue.equals(value))
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		nbtTagCompound.setString(name, value);
	}

	@Override
	public String getString(String name) {
		NBTTagCompound nbtTagCompound = card.getTagCompound();
		if (nbtTagCompound == null)
			return "";
		return nbtTagCompound.getString(name);
	}

	@Override
	public void setBoolean(String name, Boolean value) {
		NBTTagCompound nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.hasKey(name)) {
			Boolean prevValue = nbtTagCompound.getBoolean(name);
			if (prevValue == null || !prevValue.equals(value))
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		nbtTagCompound.setBoolean(name, value);
	}

	@Override
	public Boolean getBoolean(String name) {
		NBTTagCompound nbtTagCompound = card.getTagCompound();
		if (nbtTagCompound == null)
			return false;
		return nbtTagCompound.getBoolean(name);
	}

	@Override
	public void setTitle(String title) {
		setString("title", title);
	}

	@Override
	public String getTitle() {
		return getString("title");
	}

	@Override
	public CardState getState() {
		return CardState.fromInteger(getInt("state"));
	}

	@Override
	public void setState(CardState state) {
		if (state != null)
			setInt("state", state.getIndex());
		else
			setInt("state", CardState.NO_TARGET.getIndex());
	}

	@Override
	public boolean hasField(String field) {
		return ItemStackHelper.getTagCompound(card).hasKey(field);
	}

	@Override
	public void updateClient(TileEntity panel, int slot) {
		if (!updateSet.isEmpty())
			NetworkHelper.setSensorCardField(panel, slot, updateSet);
	}

	@Override
	public void setTag(String name, NBTTagCompound value) {
		NBTTagCompound nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.hasKey(name)) {
			NBTBase prevValue = nbtTagCompound.getTag(name);
			if (prevValue == null || !prevValue.equals(value))
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		if (value == null) {
			nbtTagCompound.removeTag(name);
		} else
			nbtTagCompound.setTag(name, value);
	}

	@Override
	public NBTTagCompound getTag(String name) {
		NBTTagCompound nbtTagCompound = card.getTagCompound();
		if (nbtTagCompound == null)
			return null;
		return (NBTTagCompound) nbtTagCompound.getTag(name);
	}

	@Override
	public void removeField(String name) {
		NBTTagCompound nbtTagCompound = ItemStackHelper.getTagCompound(card);
		nbtTagCompound.removeTag(name);
	}

	@Override
	public int getCardCount() {
		return getInt("cardCount");
	}
	
	@Override
	public int getCardType() {
		return card.getItemDamage();
	}

	@SideOnly(Side.CLIENT)
	public static List<PanelString> getStateMessage(CardState state) {
		List<PanelString> result = new LinkedList<PanelString>();
		PanelString line = new PanelString();
		switch (state) {
		case OUT_OF_RANGE: line.textCenter = I18n.format("msg.ec.InfoPanelOutOfRange");
			break;
		case INVALID_CARD: line.textCenter = I18n.format("msg.ec.InfoPanelInvalidCard");
			break;
		case NO_TARGET: line.textCenter = I18n.format("msg.ec.InfoPanelNoTarget");
			break;
		case CUSTOM_ERROR:
			break;
		case OK:
			break;
		default:
			break;
		}
		result.add(line);
		return result;
	}

	@Override
	public List<PanelString> getTitleList() {
		List<PanelString> result = new LinkedList<PanelString>();
		String title = getTitle();
		if (title != null && !title.isEmpty()) {
			PanelString titleString = new PanelString();
			titleString.textCenter = title;
			result.add(0, titleString);
		}
		return result;
	}

	public List<PanelString> getAllData() {
		NBTTagCompound nbt = card.getTagCompound();
		if (nbt == null)
			return null;

		nbt = card.getTagCompound().copy();
		List<PanelString> result = new LinkedList<PanelString>();

		if (nbt.hasKey("title") && nbt.getTag("title").getId() == 8) {
			String title = nbt.getString("title");
			if (!title.equals(""))
				result.add(new PanelString(String.format("title : %s", title)));
			nbt.removeTag("title");
		}
		if (nbt.hasKey("x") && nbt.hasKey("y") && nbt.hasKey("z") && nbt.getTag("x").getId() == 3
				&& nbt.getTag("y").getId() == 3 && nbt.getTag("z").getId() == 3) {
			result.add(new PanelString(String.format("xyz : %s %s %s", nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"))));
			nbt.removeTag("x");
			nbt.removeTag("y");
			nbt.removeTag("z");
		}
		if (nbt.hasKey("cardCount") && nbt.getTag("cardCount").getId() == 3) {
			int count = nbt.getInteger("cardCount");
			result.add(new PanelString(String.format("cardCount : %s", count)));
			nbt.removeTag("cardCount");
			for (int i = 0; i < count; i++) {
				String[] value = { String.format("_%dx", i), String.format("_%dy", i), String.format("_%dz", i) };
				if (nbt.hasKey(value[0]) && nbt.hasKey(value[1]) && nbt.hasKey(value[2])
						&& nbt.getTag(value[0]).getId() == 3 && nbt.getTag(value[1]).getId() == 3
						&& nbt.getTag(value[2]).getId() == 3) {
					result.add(new PanelString(String.format("_%dxyz : %s %s %s", i, nbt.getInteger(value[0]), nbt.getInteger(value[1]), nbt.getInteger(value[2]))));
					nbt.removeTag(value[0]);
					nbt.removeTag(value[1]);
					nbt.removeTag(value[2]);
				}
			}
		}
		for (String name : nbt.getKeySet()) {
			NBTBase tag = nbt.getTag(name);
			result.add(new PanelString(String.format("%s : %s", name, tag.toString())));
		}
		return result;
	}
}