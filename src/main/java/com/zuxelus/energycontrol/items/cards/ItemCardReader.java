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

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardReader implements ICardReader {
	private final ItemStack card;

	public ItemCardReader(ItemStack card) {
		if (!ItemCardMain.isCard(card))
			EnergyControl.logger.error("CardReader should be used for card items only.");
		this.card = card;
	}

	@Override
	public BlockPos getTarget() {
		NBTTagCompound tag = card.getTagCompound();
		if (tag == null)
			return null;

		return new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
	}

	@Override
	public void setInt(String name, Integer value) {
		NBTTagCompound tag = ItemStackHelper.getTagCompound(card);
		tag.setInteger(name, value);
	}

	@Override
	public Integer getInt(String name) {
		NBTTagCompound tag = card.getTagCompound();
		if (tag == null)
			return 0;
		return tag.getInteger(name);
	}

	@Override
	public void setLong(String name, Long value) {
		NBTTagCompound tag = ItemStackHelper.getTagCompound(card);
		tag.setLong(name, value);
	}

	@Override
	public Long getLong(String name) {
		NBTTagCompound tag = card.getTagCompound();
		if (tag == null)
			return 0L;
		return tag.getLong(name);
	}

	@Override
	public void setDouble(String name, Double value) {
		NBTTagCompound tag = ItemStackHelper.getTagCompound(card);
		tag.setDouble(name, value);
	}

	@Override
	public Double getDouble(String name) {
		NBTTagCompound tag = card.getTagCompound();
		if (tag == null)
			return 0.0;
		return tag.getDouble(name);
	}

	@Override
	public void setString(String name, String value) {
		if (name == null)
			return;
		NBTTagCompound tag = ItemStackHelper.getTagCompound(card);
		tag.setString(name, value);
	}

	@Override
	public String getString(String name) {
		NBTTagCompound tag = card.getTagCompound();
		if (tag == null)
			return "";
		return tag.getString(name);
	}

	@Override
	public void setByte(String name, Byte value) {
		NBTTagCompound tag = ItemStackHelper.getTagCompound(card);
		tag.setByte(name, value);
	}

	@Override
	public Byte getByte(String name) {
		NBTTagCompound tag = card.getTagCompound();
		if (tag == null)
			return 0;
		return tag.getByte(name);
	}

	@Override
	public void setBoolean(String name, Boolean value) {
		NBTTagCompound tag = ItemStackHelper.getTagCompound(card);
		tag.setBoolean(name, value);
	}

	@Override
	public Boolean getBoolean(String name) {
		NBTTagCompound tag = card.getTagCompound();
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
		return ItemStackHelper.getTagCompound(card).hasKey(field);
	}

	@Override
	public void updateClient(ItemStack stack, TileEntity panel, int slot) {
		if (panel instanceof TileEntityInfoPanel)
			ChannelHandler.updateClientCard(card, (TileEntityInfoPanel) panel, slot);
	}

	@Override
	public void updateServer(ItemStack stack, TileEntity panel, int slot) {
		if (panel instanceof TileEntityInfoPanel)
			ChannelHandler.updateServerCard(card, (TileEntityInfoPanel) panel, slot);
	}

	@Override
	public void setTag(String name, NBTBase value) {
		NBTTagCompound tag = ItemStackHelper.getTagCompound(card);
		if (value == null) {
			tag.removeTag(name);
		} else
			tag.setTag(name, value);
	}

	@Override
	public NBTTagCompound getTag(String name) {
		NBTTagCompound tag = ItemStackHelper.getTagCompound(card);
		return (NBTTagCompound) tag.getTag(name);
	}

	@Override
	public NBTTagList getTagList(String name, int type) {
		NBTTagCompound tag = ItemStackHelper.getTagCompound(card);
		return tag.getTagList(name, type);
	}

	@Override
	public ArrayList<ItemStack> getItemStackList(boolean reset) {
		NBTTagList list = getTagList("Items", Constants.NBT.TAG_COMPOUND);
		ArrayList<ItemStack> result = new ArrayList<>();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			ItemStack stack = new ItemStack(stackTag);
			if (reset)
				stack.setCount(1);
			result.add(stack);
		}
		return result;
	}

	@Override
	public void setItemStackList(ArrayList<ItemStack> list) {
		NBTTagList values = new NBTTagList();
		for (ItemStack stack : list) {
			NBTTagCompound stackTag = new NBTTagCompound();
			stack.writeToNBT(stackTag);
			values.appendTag(stackTag);
		}
		setTag("Items", values);
	}

	@Override
	public void removeField(String name) {
		NBTTagCompound tag = ItemStackHelper.getTagCompound(card);
		tag.removeTag(name);
	}

	@Override
	public int getCardCount() {
		return getInt("cardCount");
	}

	@Override
	public int getCardType() {
		return card.getItemDamage();
	}

	@Override
	public void reset() {
		BlockPos pos = getTarget();
		String title = getTitle();
		String id = getId();
		card.setTagCompound(new NBTTagCompound());
		if (pos != null)
			ItemStackHelper.setCoordinates(card, pos);
		if (!title.isEmpty())
			setTitle(title);
		setId(id);
	}

	@Override
	public void copyFrom(NBTTagCompound nbt) {
		for (String name : nbt.getKeySet()) {
			NBTBase tag = nbt.getTag(name);
			byte type = tag.getId();
			if (type == 8)
				setString(name, ((NBTTagString) tag).getString());
			else if (type == 3)
				setInt(name, ((NBTTagInt) tag).getInt());
			else if (type == 6)
				setDouble(name, ((NBTTagDouble) tag).getDouble());
			else if (type == 4)
				setLong(name, ((NBTTagLong) tag).getLong());
			else if (type == 1)
				setByte(name, ((NBTTagByte) tag).getByte());
			else if (type == 10)
				setTag(name, tag.copy());
		}
	}

	@SideOnly(Side.CLIENT)
	public static List<PanelString> getStateMessage(CardState state) {
		List<PanelString> result = new LinkedList<>();
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
		List<PanelString> result = new LinkedList<>();
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
		List<PanelString> result = new LinkedList<>();

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
			result.add(new PanelString(String.format("%s : %s", name, tag)));
		}
		return result;
	}
}
