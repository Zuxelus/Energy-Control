package com.zuxelus.energycontrol.items.cards;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.utils.StringUtils;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardInventory extends ItemCardMain {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		CompoundNBT tag = CrossModLoader.getInventoryData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		if (isServer) {
			result.add(new PanelString("msg.ec.InfoPanelTotalItems", reader.getInt("items"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSlotsUsed", String.format("%s/%s", reader.getInt("used"), reader.getInt("size")), showLabels));
		} else {
			result.add(new PanelString("msg.ec.InfoPanelName", I18n.format(reader.getString("name")), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelTotalItems", reader.getInt("items"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSlotsUsed", String.format("%s/%s", reader.getInt("used"), reader.getInt("size")), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSidedInventory", reader.getBoolean("sided").toString(), showLabels));
			for (int i = 0; i < 6; i++)
				if (reader.hasField("slot" + Integer.toString(i))) {
					ItemStack stack = ItemStack.read(reader.getTag("slot" + Integer.toString(i)));
					result.add(new PanelString(String.format("msg.ec.InfoPanelSlot%d", i + 1), StringUtils.getItemName(stack) + " x" + Integer.toString(stack.getCount()), showLabels));
				}
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}
}
