package com.zuxelus.energycontrol.items.cards;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.utils.StringUtils;

import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardInventory extends ItemCardBase {

	public ItemCardInventory() {
		super(ItemCardType.CARD_INVENTORY, "card_inventory");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		if (te instanceof IInventory && !(te instanceof TileEntityInfoPanel)) {
			IInventory inv = (IInventory) te;
			reader.setString("name", inv.getInventoryName());
			reader.setInt("size", inv.getSizeInventory());
			reader.setBoolean("sided", inv instanceof ISidedInventory);
			reader.removeField("slot0");
			reader.removeField("slot1");
			reader.removeField("slot2");
			reader.removeField("slot3");
			int inUse = 0;
			int items = 0;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (stack != null) {
					inUse++;
					items += stack.stackSize;
					if (i < 4)
						reader.setTag("slot" + i, stack.writeToNBT(new NBTTagCompound()));
				}
			}
			reader.setInt("used", inUse);
			reader.setInt("items", items);
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		if (isServer) {
			result.add(new PanelString("msg.ec.InfoPanelTotalItems", reader.getInt("items"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSlotsUsed", String.format("%s/%s", reader.getInt("used"), reader.getInt("size")), showLabels));
		} else {
			result.add(new PanelString("msg.ec.InfoPanelName", I18n.format(reader.getString("name")), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelTotalItems", reader.getInt("items"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSlotsUsed", String.format("%s/%s", reader.getInt("used"), reader.getInt("size")), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSidedInventory", reader.getBoolean("sided").toString(), showLabels));
			if (reader.hasField("slot0"))
				result.add(new PanelString(String.format("msg.ec.InfoPanelSlot%d", 1), StringUtils.getItemName(ItemStack.loadItemStackFromNBT(reader.getTag("slot0"))), showLabels));
			if (reader.hasField("slot1"))
				result.add(new PanelString(String.format("msg.ec.InfoPanelSlot%d", 2), StringUtils.getItemName(ItemStack.loadItemStackFromNBT(reader.getTag("slot1"))), showLabels));
			if (reader.hasField("slot2"))
				result.add(new PanelString(String.format("msg.ec.InfoPanelSlot%d", 3), StringUtils.getItemName(ItemStack.loadItemStackFromNBT(reader.getTag("slot2"))), showLabels));
			if (reader.hasField("slot3"))
				result.add(new PanelString(String.format("msg.ec.InfoPanelSlot%d", 4), StringUtils.getItemName(ItemStack.loadItemStackFromNBT(reader.getTag("slot3"))), showLabels));
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}
}
