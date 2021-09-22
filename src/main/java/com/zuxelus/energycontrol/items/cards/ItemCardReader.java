package com.zuxelus.energycontrol.items.cards;

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

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

public class ItemCardReader implements ICardReader {
	private ItemStack card;

	public ItemCardReader(ItemStack card) {
		if (!(card.getItem() instanceof ItemCardMain))
			EnergyControl.LOGGER.error("CardReader should be used for card items only.");
		this.card = card;
	}

	@Override
	public BlockPos getTarget() {
		CompoundTag tag = card.getTag();
		if (tag == null)
			return null;
		if (!tag.contains("x") || !tag.contains("y") || !tag.contains("z"))
			return null;
		return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
	}

	@Override
	public void setInt(String name, Integer value) {
		CompoundTag tag = ItemStackHelper.getTagCompound(card);
		tag.putInt(name, value);
	}

	@Override
	public Integer getInt(String name) {
		CompoundTag tag = card.getTag();
		if (tag == null)
			return 0;
		return tag.getInt(name);
	}

	@Override
	public void setLong(String name, Long value) {
		CompoundTag tag = ItemStackHelper.getTagCompound(card);
		tag.putLong(name, value);
	}

	@Override
	public Long getLong(String name) {
		CompoundTag tag = card.getTag();
		if (tag == null)
			return 0L;
		return tag.getLong(name);
	}

	@Override
	public void setDouble(String name, Double value) {
		CompoundTag tag = ItemStackHelper.getTagCompound(card);
		tag.putDouble(name, value);
	}

	@Override
	public Double getDouble(String name) {
		CompoundTag tag = card.getTag();
		if (tag == null)
			return 0.0;
		return tag.getDouble(name);
	}

	@Override
	public void setString(String name, String value) {
		if (name == null)
			return;
		CompoundTag tag = ItemStackHelper.getTagCompound(card);
		tag.putString(name, value);
	}

	@Override
	public String getString(String name) {
		CompoundTag tag = card.getTag();
		if (tag == null)
			return "";
		return tag.getString(name);
	}

	@Override
	public void setByte(String name, Byte value) {
		CompoundTag tag = ItemStackHelper.getTagCompound(card);
		tag.putByte(name, value);
	}

	@Override
	public Byte getByte(String name) {
		CompoundTag tag = card.getTag();
		if (tag == null)
			return 0;
		return tag.getByte(name);
	}

	@Override
	public void setBoolean(String name, Boolean value) {
		CompoundTag tag = ItemStackHelper.getTagCompound(card);
		tag.putBoolean(name, value);
	}

	@Override
	public Boolean getBoolean(String name) {
		CompoundTag tag = card.getTag();
		if (tag == null)
			return false;
		return tag.getBoolean(name);
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
		CompoundTag tag = ItemStackHelper.getTagCompound(card);
		if (value == null) {
			tag.remove(name);
		} else
			tag.put(name, value);
	}

	@Override
	public CompoundTag getTag(String name) {
		CompoundTag tag = card.getTag();
		return (CompoundTag) tag.get(name);
	}

	@Override
	public ListTag getTagList(String name, int type) {
		CompoundTag tag = ItemStackHelper.getTagCompound(card);
		return (ListTag) tag.getList(name, type);
	}

	@Override
	public ArrayList<ItemStack> getItemStackList(boolean reset) {
		ListTag list = getTagList("Items", Constants.NBT.TAG_COMPOUND);
		ArrayList<ItemStack> result = new ArrayList<ItemStack> ();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag stackTag = list.getCompound(i);
			ItemStack stack = ItemStack.of(stackTag);
			if (reset)
				stack.setCount(1);
			result.add(stack);
		}
		return result;
	}

	@Override
	public void setItemStackList(ArrayList<ItemStack> list) {
		ListTag values = new ListTag();
		for (ItemStack stack : list) {
			CompoundTag stackTag = new CompoundTag();
			stack.save(stackTag);
			values.add(stackTag);
		}
		setTag("Items", values);
	}

	@Override
	public void removeField(String name) {
		CompoundTag tag = ItemStackHelper.getTagCompound(card);
		tag.remove(name);
	}

	@Override
	public int getCardCount() {
		return getInt("cardCount");
	}

	@Override
	public void reset() {
		BlockPos pos = getTarget();
		String title = getTitle();
		card.setTag(new CompoundTag());
		if (pos != null)
			ItemStackHelper.setCoordinates(card, pos);
		if (!title.isEmpty())
			setTitle(title);
	}

	@Override
	public void copyFrom(CompoundTag nbt) {
		for (String name : nbt.getAllKeys()) {
			Tag tag = nbt.get(name);
			TagType<?> type = tag.getType();
			if (type == StringTag.TYPE)
				setString(name, tag.getAsString());
			else if (type == IntTag.TYPE)
				setInt(name, ((NumericTag) tag).getAsInt());
			else if (type == DoubleTag.TYPE)
				setDouble(name, ((NumericTag) tag).getAsDouble());
			else if (type == LongTag.TYPE)
				setLong(name, ((NumericTag) tag).getAsLong());
			else if (type == ByteTag.TYPE)
				setByte(name, ((NumericTag) tag).getAsByte());
		}
	}

	@OnlyIn(Dist.CLIENT)
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
		return ((ItemCardMain) card.getItem()).getStringData(world, settings, this, isServer, showLabels);
	}

	public List<PanelString> getAllData() {
		CompoundTag nbt = card.getTag();
		if (nbt == null)
			return null;

		nbt = card.getTag().copy();
		List<PanelString> result = new LinkedList<PanelString>();

		if (nbt.contains("title") && nbt.get("title").getId() == 8) {
			String title = nbt.getString("title");
			if (!title.equals(""))
				result.add(new PanelString(String.format("title : %s", title)));
			nbt.remove("title");
		}
		if (nbt.contains("x") && nbt.contains("y") && nbt.contains("z") && nbt.get("x").getId() == 3
				&& nbt.get("y").getId() == 3 && nbt.get("z").getId() == 3) {
			result.add(new PanelString(String.format("xyz : %s %s %s", nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"))));
			nbt.remove("x");
			nbt.remove("y");
			nbt.remove("z");
		}
		if (nbt.contains("cardCount") && nbt.get("cardCount").getId() == 3) {
			int count = nbt.getInt("cardCount");
			result.add(new PanelString(String.format("cardCount : %s", count)));
			nbt.remove("cardCount");
			for (int i = 0; i < count; i++) {
				String[] value = { String.format("_%dx", i), String.format("_%dy", i), String.format("_%dz", i) };
				if (nbt.contains(value[0]) && nbt.contains(value[1]) && nbt.contains(value[2])
						&& nbt.get(value[0]).getId() == 3 && nbt.get(value[1]).getId() == 3
						&& nbt.get(value[2]).getId() == 3) {
					result.add(new PanelString(String.format("_%dxyz : %s %s %s", i, nbt.getInt(value[0]), nbt.getInt(value[1]), nbt.getInt(value[2]))));
					nbt.remove(value[0]);
					nbt.remove(value[1]);
					nbt.remove(value[2]);
				}
			}
		}
		for (String name : nbt.getAllKeys()) {
			Tag tag = nbt.get(name);
			result.add(new PanelString(String.format("%s : %s", name, tag.toString())));
		}
		return result;
	}
}