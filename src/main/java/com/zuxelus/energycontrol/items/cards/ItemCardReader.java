package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemCardReader implements ICardReader {
	private ItemStack card;

	public ItemCardReader(ItemStack card) {
		if (!(card.getItem() instanceof ItemCardMain))
			EnergyControl.LOGGER.error("CardReader should be used for card items only.");
		this.card = card;
	}

	private CompoundTag tag() {
		return ItemStackHelper.getTag(card);
	}

	@Override
	public BlockPos getTarget() {
		CompoundTag tag = tag();
		if (!tag.contains("x") || !tag.contains("y") || !tag.contains("z"))
			return null;
		return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
	}

	@Override
	public void setInt(String name, Integer value) {
		ItemStackHelper.update(card, tag -> tag.putInt(name, value));
	}

	@Override
	public Integer getInt(String name) {
		return tag().getInt(name);
	}

	@Override
	public void setLong(String name, Long value) {
		ItemStackHelper.update(card, tag -> tag.putLong(name, value));
	}

	@Override
	public Long getLong(String name) {
		return tag().getLong(name);
	}

	@Override
	public void setDouble(String name, Double value) {
		ItemStackHelper.update(card, tag -> tag.putDouble(name, value));
	}

	@Override
	public Double getDouble(String name) {
		return tag().getDouble(name);
	}

	@Override
	public void setString(String name, String value) {
		if (name == null)
			return;
		ItemStackHelper.update(card, tag -> tag.putString(name, value));
	}

	@Override
	public String getString(String name) {
		return tag().getString(name);
	}

	@Override
	public void setByte(String name, Byte value) {
		ItemStackHelper.update(card, tag -> tag.putByte(name, value));
	}

	@Override
	public Byte getByte(String name) {
		return tag().getByte(name);
	}

	@Override
	public void setBoolean(String name, Boolean value) {
		ItemStackHelper.update(card, tag -> tag.putBoolean(name, value));
	}

	@Override
	public Boolean getBoolean(String name) {
		return tag().getBoolean(name);
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
	public void setId(String id) {
		setString("id", id);
	}

	@Override
	public String getId() {
		String id = getString("id");
		if (id.isEmpty()) {
			id = UUID.randomUUID().toString();
			setId(id);
		}
		return id;
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
		return ItemStackHelper.contains(card, field);
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
		ItemStackHelper.update(card, tag -> {
			if (value == null)
				tag.remove(name);
			else
				tag.put(name, value);
		});
	}

	@Override
	public CompoundTag getTag(String name) {
		return (tag().contains(name, 10) ? tag().getCompound(name) : null);
	}

	@Override
	public ListTag getTagList(String name, int type) {
		return tag().getList(name, 10);
	}

	@Override
	public ArrayList<ItemStack> getItemStackList(boolean reset) {
		ListTag list = getTagList("Items", Tag.TAG_COMPOUND);
		ArrayList<ItemStack> result = new ArrayList<ItemStack> ();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag stackTag = list.getCompound(i);
			ItemStack stack = ItemStackHelper.loadStack(stackTag);
			if (reset)
				stack.setCount(1);
			result.add(stack);
		}
		return result;
	}

	@Override
	public void setItemStackList(ArrayList<ItemStack> list) {
		ListTag values = new ListTag();
		for (ItemStack stack : list)
			values.add(ItemStackHelper.saveStack(stack));
		setTag("Items", values);
	}

	@Override
	public void removeField(String name) {
		ItemStackHelper.update(card, tag -> tag.remove(name));
	}

	@Override
	public int getCardCount() {
		return getInt("cardCount");
	}

	@Override
	public void reset() {
		BlockPos pos = getTarget();
		String title = getTitle();
		String id = getId();
		ItemStackHelper.setTag(card, new CompoundTag());
		if (pos != null)
			ItemStackHelper.setCoordinates(card, pos);
		if (!title.isEmpty())
			setTitle(title);
		setId(id);
	}

	@Override
	public void copyFrom(CompoundTag nbt) {
		ItemStackHelper.update(card, dest -> {
			for (String name : nbt.getAllKeys()) {
				Tag tag = nbt.get(name);
				if (tag instanceof StringTag || tag instanceof IntTag || tag instanceof DoubleTag || tag instanceof LongTag || tag instanceof ByteTag || tag instanceof CompoundTag)
					dest.put(name, tag.copy());
			}
		});
	}

	public static List<PanelString> getStateMessage(CardState state) {
		List<PanelString> result = new LinkedList<>();
		PanelString line = new PanelString();
		switch (state) {
		case OUT_OF_RANGE:
			line.textCenter = Language.getInstance().getOrDefault("msg.ec.InfoPanelOutOfRange");
			break;
		case INVALID_CARD:
			line.textCenter = Language.getInstance().getOrDefault("msg.ec.InfoPanelInvalidCard");
			break;
		case NO_TARGET:
			line.textCenter = Language.getInstance().getOrDefault("msg.ec.InfoPanelNoTarget");
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
		List<PanelString> result = new LinkedList<>();
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
		if (!ItemStackHelper.hasTag(card))
			return null;

		CompoundTag nbt = tag();
		List<PanelString> result = new LinkedList<PanelString>();

		if (nbt.get("title") instanceof StringTag) {
			String title = nbt.getString("title");
			if (!title.equals(""))
				result.add(new PanelString(String.format("title : %s", title)));
			nbt.remove("title");
		}
		if (nbt.get("x") instanceof IntTag && nbt.get("y") instanceof IntTag && nbt.get("z") instanceof IntTag) {
			result.add(new PanelString(String.format("xyz : %s %s %s", nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"))));
			nbt.remove("x");
			nbt.remove("y");
			nbt.remove("z");
		}
		if (nbt.get("cardCount") instanceof IntTag) {
			int count = nbt.getInt("cardCount");
			result.add(new PanelString(String.format("cardCount : %s", count)));
			nbt.remove("cardCount");
			for (int i = 0; i < count; i++) {
				String[] value = { String.format("_%dx", i), String.format("_%dy", i), String.format("_%dz", i) };
				if (nbt.get(value[0]) instanceof IntTag && nbt.get(value[1]) instanceof IntTag && nbt.get(value[2]) instanceof IntTag) {
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
