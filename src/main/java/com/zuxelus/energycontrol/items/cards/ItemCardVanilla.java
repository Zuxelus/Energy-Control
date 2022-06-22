package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardVanilla extends ItemCardBase {
	public static final int DISPLAY_BURNING = 1;
	public static final int DISPLAY_TIME = 2;
	public static final int DISPLAY_SLOT_1 = 4;
	public static final int DISPLAY_SLOT_2 = 8;
	public static final int DISPLAY_SLOT_3 = 16;

	public ItemCardVanilla() {
		super(ItemCardType.CARD_VANILLA, "card_vanilla");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		if (te instanceof TileEntityFurnace) {
			TileEntityFurnace furnace = (TileEntityFurnace) te;
			reader.setString("entity", "furnace");
			reader.setBoolean("burning", furnace.isBurning());
			reader.setInt("burnTime", furnace.furnaceBurnTime);
			ItemStack stack = furnace.getStackInSlot(0);
			reader.setString("cooking", stack != null ? String.format("%sx %s", stack.stackSize, stack.getDisplayName()) : "-");
			stack = furnace.getStackInSlot(1);
			reader.setString("fuel", stack != null ? String.format("%sx %s", stack.stackSize, stack.getDisplayName()) : "-");
			stack = furnace.getStackInSlot(2);
			reader.setString("output", stack != null ? String.format("%sx %s", stack.stackSize, stack.getDisplayName()) : "-");
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		if (reader.hasField("burnTime") && (settings & DISPLAY_TIME) > 0)
			result.add(new PanelString("msg.ec.InfoPanelBurnTime", reader.getInt("burnTime"), showLabels));
		if (reader.hasField("cooking") && (settings & DISPLAY_SLOT_1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelInput", reader.getString("cooking"), showLabels));
		if (reader.hasField("fuel") && (settings & DISPLAY_SLOT_2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getString("fuel"), showLabels));
		if (reader.hasField("output") && (settings & DISPLAY_SLOT_3) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getString("output"), showLabels));
		if (reader.hasField("burning") && (settings & DISPLAY_BURNING) > 0)
			addOnOff(result, isServer, reader.getBoolean("burning"));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<PanelSetting>();
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), DISPLAY_BURNING));
		result.add(new PanelSetting(I18n.format("msg.burnTimeText"), DISPLAY_TIME));
		result.add(new PanelSetting(I18n.format("msg.cookingText"), DISPLAY_SLOT_1));
		result.add(new PanelSetting(I18n.format("msg.fuelText"), DISPLAY_SLOT_2));
		result.add(new PanelSetting(I18n.format("msg.outputText"), DISPLAY_SLOT_3));
		return result;
	}
}
