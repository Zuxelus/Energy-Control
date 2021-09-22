package com.zuxelus.energycontrol.items.cards;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.utils.StringUtils;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemCardInventory extends ItemCardMain {

	@Override
	public CardState update(Level world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockEntity te = world.getBlockEntity(target);
		if (te instanceof Container) {
			Container inv = (Container) te;
			reader.setString("name", "TODO"/*inv.getName()*/);
			reader.setInt("size", inv.getContainerSize());
			reader.setBoolean("sided", false /*inv instanceof ISidedInventory*/); // TODO
			reader.removeField("slot0");
			reader.removeField("slot1");
			reader.removeField("slot2");
			reader.removeField("slot3");
			int inUse = 0;
			int items = 0;
			for (int i = 0; i < inv.getContainerSize(); i++) {
				if (inv.getItem(i) != ItemStack.EMPTY) {
					inUse++;
					items += inv.getItem(i).getCount();
				}
				if (i == 0)
					reader.setTag("slot0", inv.getItem(0).save(new CompoundTag()));
				if (i == 1)
					reader.setTag("slot1", inv.getItem(1).save(new CompoundTag()));
				if (i == 2)
					reader.setTag("slot2", inv.getItem(2).save(new CompoundTag()));
				if (i == 3)
					reader.setTag("slot3", inv.getItem(3).save(new CompoundTag()));
			}
			reader.setInt("used", inUse);
			reader.setInt("items", items);
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(Level world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		if (isServer) {
			result.add(new PanelString("msg.ec.InfoPanelTotalItems", reader.getInt("items"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSlotsUsed", String.format("%s/%s", reader.getInt("used"), reader.getInt("size")), showLabels));
		} else {
			result.add(new PanelString("msg.ec.InfoPanelName", I18n.get(reader.getString("name")), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelTotalItems", reader.getInt("items"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSlotsUsed", String.format("%s/%s", reader.getInt("used"), reader.getInt("size")), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSidedInventory", reader.getBoolean("sided").toString(), showLabels));
			if (reader.hasField("slot0"))
				result.add(new PanelString(String.format("msg.ec.InfoPanelSlot%d", 1), StringUtils.getItemName(ItemStack.of(reader.getTag("slot0"))), showLabels));
			if (reader.hasField("slot1"))
				result.add(new PanelString(String.format("msg.ec.InfoPanelSlot%d", 2), StringUtils.getItemName(ItemStack.of(reader.getTag("slot1"))), showLabels));
			if (reader.hasField("slot2"))
				result.add(new PanelString(String.format("msg.ec.InfoPanelSlot%d", 3), StringUtils.getItemName(ItemStack.of(reader.getTag("slot2"))), showLabels));
			if (reader.hasField("slot3"))
				result.add(new PanelString(String.format("msg.ec.InfoPanelSlot%d", 4), StringUtils.getItemName(ItemStack.of(reader.getTag("slot3"))), showLabels));
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}
}
