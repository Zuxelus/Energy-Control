package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.api.IItemCard;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemCardReader implements ICardReader {
	private ItemStack card;

	public ItemCardReader(ItemStack card) {
		if (!ItemCardMain.isCard(card))
			EnergyControl.LOGGER.error("CardReader should be used for card items only.");
		this.card = card;
	}

	@Override
	public BlockPos getTarget() {
		CompoundTag tag = ItemStackHelper.getTag(card);
		if (tag == null)
			return null;
		if (!tag.contains("x") || !tag.contains("y") || !tag.contains("z"))
			return null;
		return new BlockPos(tag.getIntOr("x", 0), tag.getIntOr("y", 0), tag.getIntOr("z", 0));
	}

	@Override
	public void setInt(String name, Integer value) {
		ItemStackHelper.updateTag(card, tag -> tag.putInt(name, value));
	}

	@Override
	public Integer getInt(String name) {
		CompoundTag tag = ItemStackHelper.getTag(card);
		if (tag == null)
			return 0;
		return tag.getIntOr(name, 0);
	}

	@Override
	public void setLong(String name, Long value) {
		ItemStackHelper.updateTag(card, tag -> tag.putLong(name, value));
	}

	@Override
	public Long getLong(String name) {
		CompoundTag tag = ItemStackHelper.getTag(card);
		if (tag == null)
			return 0L;
		return tag.getLongOr(name, 0L);
	}

	@Override
	public void setDouble(String name, Double value) {
		ItemStackHelper.updateTag(card, tag -> tag.putDouble(name, value));
	}

	@Override
	public Double getDouble(String name) {
		CompoundTag tag = ItemStackHelper.getTag(card);
		if (tag == null)
			return 0.0;
		return tag.getDoubleOr(name, 0.0);
	}

	@Override
	public void setString(String name, String value) {
		if (name == null)
			return;
		ItemStackHelper.updateTag(card, tag -> tag.putString(name, value));
	}

	@Override
	public String getString(String name) {
		CompoundTag tag = ItemStackHelper.getTag(card);
		if (tag == null)
			return "";
		return tag.getStringOr(name, "");
	}

	@Override
	public void setByte(String name, Byte value) {
		ItemStackHelper.updateTag(card, tag -> tag.putByte(name, value));
	}

	@Override
	public Byte getByte(String name) {
		CompoundTag tag = ItemStackHelper.getTag(card);
		if (tag == null)
			return 0;
		return tag.getByteOr(name, (byte) 0);
	}

	@Override
	public void setBoolean(String name, Boolean value) {
		ItemStackHelper.updateTag(card, tag -> tag.putBoolean(name, value));
	}

	@Override
	public Boolean getBoolean(String name) {
		CompoundTag tag = ItemStackHelper.getTag(card);
		if (tag == null)
			return false;
		return tag.getBooleanOr(name, false);
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
		return ItemStackHelper.getTagCompound(card).contains(field);
	}

	@Override
	public void updateClient(ItemStack stack, BlockEntity panel, int slot) {
		if (panel instanceof TileEntityInfoPanel)
			ChannelHandler.updateClientCard(stack, (TileEntityInfoPanel) panel, slot);
	}

	@Override
	public void updateServer(ItemStack stack, BlockEntity panel, int slot) {
		if (panel instanceof TileEntityInfoPanel)
			ChannelHandler.updateServerCard(card, (TileEntityInfoPanel) panel, slot);
	}

	@Override
	public void setTag(String name, Tag value) {
		ItemStackHelper.updateTag(card, tag -> {
			if (value == null)
				tag.remove(name);
			else
				tag.put(name, value);
		});
	}

	@Override
	public CompoundTag getTag(String name) {
		CompoundTag tag = ItemStackHelper.getTag(card);
		return tag == null ? null : (CompoundTag) tag.get(name);
	}

	@Override
	public ListTag getTagList(String name, int type) {
		CompoundTag tag = ItemStackHelper.getTagCompound(card);
		return (ListTag) tag.getListOrEmpty(name);
	}

	@Override
	public ArrayList<ItemStack> getItemStackList(boolean reset, HolderLookup.Provider registries) {
		ListTag list = getTagList("Items", Tag.TAG_COMPOUND);
		ArrayList<ItemStack> result = new ArrayList<ItemStack> ();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag stackTag = list.getCompoundOrEmpty(i);
			ItemStack stack = ItemStackHelper.parseOptional(registries, stackTag);
			if (reset)
				stack.setCount(1);
			result.add(stack);
		}
		return result;
	}

	@Override
	public void setItemStackList(ArrayList<ItemStack> list, HolderLookup.Provider registries) {
		ListTag values = new ListTag();
		for (ItemStack stack : list) {
			values.add(ItemStackHelper.saveOptional(stack, registries));
		}
		setTag("Items", values);
	}

	@Override
	public void removeField(String name) {
		ItemStackHelper.updateTag(card, tag -> tag.remove(name));
	}

	@Override
	public int getCardCount() {
		return getInt("cardCount");
	}

	@Override
	public void reset() {
		BlockPos pos = getTarget();
		String title = getTitle();
		ItemStackHelper.setTag(card, new CompoundTag());
		if (pos != null)
			ItemStackHelper.setCoordinates(card, pos);
		if (!title.isEmpty())
			setTitle(title);
	}

	@Override
	public void copyFrom(CompoundTag nbt) {
		for (String name : nbt.keySet()) {
			Tag tag = nbt.get(name);
			TagType<?> type = tag.getType();
			if (type == StringTag.TYPE)
				setString(name, tag.asString().orElse(""));
			else if (type == IntTag.TYPE)
				setInt(name, ((NumericTag) tag).intValue());
			else if (type == DoubleTag.TYPE)
				setDouble(name, ((NumericTag) tag).doubleValue());
			else if (type == LongTag.TYPE)
				setLong(name, ((NumericTag) tag).longValue());
			else if (type == ByteTag.TYPE)
				setByte(name, ((NumericTag) tag).byteValue());
			else if (type == CompoundTag.TYPE)
				setTag(name, tag.copy());
		}
	}

	@Environment(EnvType.CLIENT)
	public static List<PanelString> getStateMessage(CardState state) {
		List<PanelString> result = new LinkedList<PanelString>();
		PanelString line = new PanelString();
		switch (state) {
		case OUT_OF_RANGE: line.textCenter = I18n.get("msg.ec.InfoPanelOutOfRange");
			break;
		case INVALID_CARD: line.textCenter = I18n.get("msg.ec.InfoPanelInvalidCard");
			break;
		case NO_TARGET: line.textCenter = I18n.get("msg.ec.InfoPanelNoTarget");
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

	public List<PanelString> getStringData(Level world, int settings, boolean isServer, boolean showLabels) {
		return ((IItemCard) card.getItem()).getStringData(world, settings, this, isServer, showLabels);
	}

	public List<PanelString> getAllData() {
		CompoundTag nbt = ItemStackHelper.getTag(card);
		if (nbt == null)
			return null;

		List<PanelString> result = new LinkedList<PanelString>();

		if (nbt.contains("title") && nbt.get("title").getId() == 8) {
			String title = nbt.getStringOr("title", "");
			if (!title.equals(""))
				result.add(new PanelString(String.format("title : %s", title)));
			nbt.remove("title");
		}
		if (nbt.contains("x") && nbt.contains("y") && nbt.contains("z") && nbt.get("x").getId() == 3
				&& nbt.get("y").getId() == 3 && nbt.get("z").getId() == 3) {
			result.add(new PanelString(String.format("xyz : %s %s %s", nbt.getIntOr("x", 0), nbt.getIntOr("y", 0), nbt.getIntOr("z", 0))));
			nbt.remove("x");
			nbt.remove("y");
			nbt.remove("z");
		}
		if (nbt.contains("cardCount") && nbt.get("cardCount").getId() == 3) {
			int count = nbt.getIntOr("cardCount", 0);
			result.add(new PanelString(String.format("cardCount : %s", count)));
			nbt.remove("cardCount");
			for (int i = 0; i < count; i++) {
				String[] value = { String.format("_%dx", i), String.format("_%dy", i), String.format("_%dz", i) };
				if (nbt.contains(value[0]) && nbt.contains(value[1]) && nbt.contains(value[2])
						&& nbt.get(value[0]).getId() == 3 && nbt.get(value[1]).getId() == 3
						&& nbt.get(value[2]).getId() == 3) {
					result.add(new PanelString(String.format("_%dxyz : %s %s %s", i, nbt.getIntOr(value[0], 0), nbt.getIntOr(value[1], 0), nbt.getIntOr(value[2], 0))));
					nbt.remove(value[0]);
					nbt.remove(value[1]);
					nbt.remove(value[2]);
				}
			}
		}
		for (String name : nbt.keySet()) {
			Tag tag = nbt.get(name);
			result.add(new PanelString(String.format("%s : %s", name, tag.toString())));
		}
		return result;
	}
}