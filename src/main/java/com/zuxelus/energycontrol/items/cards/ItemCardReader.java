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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardReader implements ICardReader {
	private ItemStack card;

	public ItemCardReader(ItemStack card) {
		if (!(card.getItem() instanceof ItemCardMain))
			EnergyControl.LOGGER.error("CardReader should be used for card items only.");
		this.card = card;
	}

	@Override
	public BlockPos getTarget() {
		NbtCompound tag = card.getNbt();
		if (tag == null)
			return null;
		if (!tag.contains("x") || !tag.contains("y") || !tag.contains("z"))
			return null;
		return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
	}

	@Override
	public void setInt(String name, Integer value) {
		NbtCompound tag = ItemStackHelper.getTagCompound(card);
		tag.putInt(name, value);
	}

	@Override
	public Integer getInt(String name) {
		NbtCompound tag = card.getNbt();
		if (tag == null)
			return 0;
		return tag.getInt(name);
	}

	@Override
	public void setLong(String name, Long value) {
		NbtCompound tag = ItemStackHelper.getTagCompound(card);
		tag.putLong(name, value);
	}

	@Override
	public Long getLong(String name) {
		NbtCompound tag = card.getNbt();
		if (tag == null)
			return 0L;
		return tag.getLong(name);
	}

	@Override
	public void setDouble(String name, Double value) {
		NbtCompound tag = ItemStackHelper.getTagCompound(card);
		tag.putDouble(name, value);
	}

	@Override
	public Double getDouble(String name) {
		NbtCompound tag = card.getNbt();
		if (tag == null)
			return 0.0;
		return tag.getDouble(name);
	}

	@Override
	public void setString(String name, String value) {
		if (name == null)
			return;
		NbtCompound tag = ItemStackHelper.getTagCompound(card);
		tag.putString(name, value);
	}

	@Override
	public String getString(String name) {
		NbtCompound tag = card.getNbt();
		if (tag == null)
			return "";
		return tag.getString(name);
	}

	@Override
	public void setByte(String name, Byte value) {
		NbtCompound tag = ItemStackHelper.getTagCompound(card);
		tag.putByte(name, value);
	}

	@Override
	public Byte getByte(String name) {
		NbtCompound tag = card.getNbt();
		if (tag == null)
			return 0;
		return tag.getByte(name);
	}

	@Override
	public void setBoolean(String name, Boolean value) {
		NbtCompound tag = ItemStackHelper.getTagCompound(card);
		tag.putBoolean(name, value);
	}

	@Override
	public Boolean getBoolean(String name) {
		NbtCompound tag = card.getNbt();
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
	public void setTag(String name, NbtElement value) {
		NbtCompound tag = ItemStackHelper.getTagCompound(card);
		if (value == null) {
			tag.remove(name);
		} else
			tag.put(name, value);
	}

	@Override
	public NbtCompound getTag(String name) {
		NbtCompound tag = card.getNbt();
		return (NbtCompound) tag.get(name);
	}

	@Override
	public NbtList getTagList(String name, int type) {
		NbtCompound tag = ItemStackHelper.getTagCompound(card);
		return (NbtList) tag.getList(name, type);
	}

	@Override
	public ArrayList<ItemStack> getItemStackList(boolean reset) {
		NbtList list = getTagList("Items", NbtElement.COMPOUND_TYPE);
		ArrayList<ItemStack> result = new ArrayList<ItemStack> ();
		for (int i = 0; i < list.size(); i++) {
			NbtCompound stackTag = list.getCompound(i);
			ItemStack stack = ItemStack.fromNbt(stackTag);
			if (reset)
				stack.setCount(1);
			result.add(stack);
		}
		return result;
	}

	@Override
	public void setItemStackList(ArrayList<ItemStack> list) {
		NbtList values = new NbtList();
		for (ItemStack stack : list) {
			NbtCompound stackTag = new NbtCompound();
			stack.writeNbt(stackTag);
			values.add(stackTag);
		}
		setTag("Items", values);
	}

	@Override
	public void removeField(String name) {
		NbtCompound tag = ItemStackHelper.getTagCompound(card);
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
		card.setNbt(new NbtCompound());
		if (pos != null)
			ItemStackHelper.setCoordinates(card, pos);
		if (!title.isEmpty())
			setTitle(title);
	}

	@Override
	public void copyFrom(NbtCompound nbt) {
		for (String name : nbt.getKeys()) {
			NbtElement tag = nbt.get(name);
			NbtType<?> type = tag.getNbtType();
			if (type == NbtString.TYPE)
				setString(name, tag.asString());
			else if (type == NbtInt.TYPE)
				setInt(name, ((AbstractNbtNumber) tag).intValue());
			else if (type == NbtDouble.TYPE)
				setDouble(name, ((AbstractNbtNumber) tag).doubleValue());
			else if (type == NbtLong.TYPE)
				setLong(name, ((AbstractNbtNumber) tag).longValue());
			else if (type == NbtByte.TYPE)
				setByte(name, ((AbstractNbtNumber) tag).byteValue());
			else if (type == NbtCompound.TYPE)
				setTag(name, tag.copy());
		}
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

	public List<PanelString> getStringData(World world, int settings, boolean isServer, boolean showLabels) {
		return ((ItemCardMain) card.getItem()).getStringData(world, settings, this, isServer, showLabels);
	}

	public List<PanelString> getAllData() {
		NbtCompound nbt = card.getNbt();
		if (nbt == null)
			return null;

		nbt = card.getNbt().copy();
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
			NbtElement tag = nbt.get(name);
			result.add(new PanelString(String.format("%s : %s", name, tag.toString())));
		}
		return result;
	}
}