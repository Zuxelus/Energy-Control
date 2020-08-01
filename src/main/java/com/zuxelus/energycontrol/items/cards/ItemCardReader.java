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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardReader implements ICardReader {
	private ItemStack card;
	private Map<String, Object> updateSet;

	public ItemCardReader(ItemStack card) {
		if (!(card.getItem() instanceof MainCardItem))
			EnergyControl.LOGGER.error("CardReader should be used for card items only.");
		this.card = card;
		updateSet = new HashMap<String, Object>();
	}

	@Override
	public BlockPos getTarget() {
		CompoundTag nbtTagCompound = card.getTag();
		if (nbtTagCompound == null)
			return null;

		return new BlockPos(nbtTagCompound.getInt("x"), nbtTagCompound.getInt("y"), nbtTagCompound.getInt("z"));
	}

	@Override
	public void setInt(String name, Integer value) {
		CompoundTag nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.contains(name)) {
			Integer prevValue = nbtTagCompound.getInt(name);
			if (prevValue == null || !prevValue.equals(value))
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		nbtTagCompound.putInt(name, value);
	}

	@Override
	public Integer getInt(String name) {
		CompoundTag nbtTagCompound = card.getTag();
		if (nbtTagCompound == null)
			return 0;
		return nbtTagCompound.getInt(name);
	}

	@Override
	public void setLong(String name, Long value) {
		CompoundTag nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.contains(name)) {
			Long prevValue = nbtTagCompound.getLong(name);
			if (prevValue == null || !prevValue.equals(value))
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		nbtTagCompound.putLong(name, value);
	}

	@Override
	public Long getLong(String name) {
		CompoundTag nbtTagCompound = card.getTag();
		if (nbtTagCompound == null)
			return 0L;
		return nbtTagCompound.getLong(name);
	}

	@Override
	public void setDouble(String name, Double value) {
		CompoundTag nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.contains(name)) {
			Double prevValue = nbtTagCompound.getDouble(name);
			if (prevValue == null || prevValue != value)
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		nbtTagCompound.putDouble(name, value);
	}

	@Override
	public Double getDouble(String name) {
		CompoundTag nbtTagCompound = card.getTag();
		if (nbtTagCompound == null)
			return 0.0;
		return nbtTagCompound.getDouble(name);
	}

	@Override
	public void setString(String name, String value) {
		if (name == null)
			return;
		CompoundTag nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.contains(name)) {
			String prevValue = nbtTagCompound.getString(name);
			if (prevValue == null || !prevValue.equals(value))
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		nbtTagCompound.putString(name, value);
	}

	@Override
	public String getString(String name) {
		CompoundTag nbtTagCompound = card.getTag();
		if (nbtTagCompound == null)
			return "";
		return nbtTagCompound.getString(name);
	}

	@Override
	public void setBoolean(String name, Boolean value) {
		CompoundTag nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.contains(name)) {
			Boolean prevValue = nbtTagCompound.getBoolean(name);
			if (prevValue == null || !prevValue.equals(value))
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		nbtTagCompound.putBoolean(name, value);
	}

	@Override
	public Boolean getBoolean(String name) {
		CompoundTag nbtTagCompound = card.getTag();
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
		setInt("state", state.getIndex());
	}

	@Override
	public boolean hasField(String field) {
		return ItemStackHelper.getTagCompound(card).contains(field);
	}

	@Override
	public void updateClient(BlockEntity panel, int slot) {
		if (!updateSet.isEmpty())
			NetworkHelper.setSensorCardField(panel, slot, updateSet);
	}

	@Override
	public void setTag(String name, CompoundTag value) {
		CompoundTag nbtTagCompound = ItemStackHelper.getTagCompound(card);
		if (nbtTagCompound.contains(name)) {
			Tag prevValue = nbtTagCompound.get(name);
			if (prevValue == null || !prevValue.equals(value))
				updateSet.put(name, value);
		} else
			updateSet.put(name, value);
		if (value == null) {
			nbtTagCompound.remove(name);
		} else
			nbtTagCompound.put(name, value);
	}

	@Override
	public CompoundTag getTag(String name) {
		CompoundTag nbtTagCompound = card.getTag();
		if (nbtTagCompound == null)
			return null;
		return (CompoundTag) nbtTagCompound.get(name);
	}

	public void clearField(String name) {
		CompoundTag nbtTagCompound = ItemStackHelper.getTagCompound(card);
		nbtTagCompound.remove(name);
	}

	@Override
	public int getCardCount() {
		return getInt("cardCount");
	}

	@Environment(EnvType.CLIENT)
	public static List<PanelString> getStateMessage(CardState state) {
		List<PanelString> result = new LinkedList<PanelString>();
		PanelString line = new PanelString();
		switch (state) {
		case OUT_OF_RANGE: line.textCenter = I18n.translate("msg.ec.InfoPanelOutOfRange");
			break;
		case INVALID_CARD: line.textCenter = I18n.translate("msg.ec.InfoPanelInvalidCard");
			break;
		case NO_TARGET: line.textCenter = I18n.translate("msg.ec.InfoPanelNoTarget");
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

	public List<PanelString> getStringData(World world, int settings, boolean showLabels) {
		return ((MainCardItem) card.getItem()).getStringData(world, settings, this, showLabels);
	}

	public List<PanelString> getAllData() {
		CompoundTag nbt = card.getTag();
		if (nbt == null)
			return null;

		nbt = card.getTag().copy();
		List<PanelString> result = new LinkedList<PanelString>();

		if (nbt.contains("title") && nbt.get("title").getType() == 8) {
			String title = nbt.getString("title");
			if (!title.equals(""))
				result.add(new PanelString(String.format("title : %s", title)));
			nbt.remove("title");
		}
		if (nbt.contains("x") && nbt.contains("y") && nbt.contains("z") && nbt.get("x").getType() == 3
				&& nbt.get("y").getType() == 3 && nbt.get("z").getType() == 3) {
			result.add(new PanelString(String.format("xyz : %s %s %s", nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"))));
			nbt.remove("x");
			nbt.remove("y");
			nbt.remove("z");
		}
		if (nbt.contains("cardCount") && nbt.get("cardCount").getType() == 3) {
			int count = nbt.getInt("cardCount");
			result.add(new PanelString(String.format("cardCount : %s", count)));
			nbt.remove("cardCount");
			for (int i = 0; i < count; i++) {
				String[] value = { String.format("_%dx", i), String.format("_%dy", i), String.format("_%dz", i) };
				if (nbt.contains(value[0]) && nbt.contains(value[1]) && nbt.contains(value[2])
						&& nbt.get(value[0]).getType() == 3 && nbt.get(value[1]).getType() == 3
						&& nbt.get(value[2]).getType() == 3) {
					result.add(new PanelString(String.format("_%dxyz : %s %s %s", i, nbt.getInt(value[0]), nbt.getInt(value[1]), nbt.getInt(value[2]))));
					nbt.remove(value[0]);
					nbt.remove(value[1]);
					nbt.remove(value[2]);
				}
			}
		}
		for (String name : nbt.getKeys()) {
			Tag tag = nbt.get(name);
			result.add(new PanelString(String.format("%s : %s", name, tag.toString())));
		}
		return result;
	}
}