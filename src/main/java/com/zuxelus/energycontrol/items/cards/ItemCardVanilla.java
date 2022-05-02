package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import net.minecraft.client.resources.I18n;
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
			reader.setInt("type", 1);
			reader.setString("entity", "furnace");
			reader.setBoolean("burning", furnace.isBurning());
			reader.setInt("burnTime", furnace.furnaceBurnTime);
			NBTTagCompound tag = new NBTTagCompound();
			if (furnace.getStackInSlot(0) != null) {
				tag.setString("Cooking", furnace.getStackInSlot(0).getDisplayName());
				tag.setInteger("Csize", furnace.getStackInSlot(0).stackSize);
			}
			if (furnace.getStackInSlot(1) != null) {
				tag.setString("Fuel", furnace.getStackInSlot(1).getDisplayName());
				tag.setInteger("Fsize", furnace.getStackInSlot(1).stackSize);
			}
			if (furnace.getStackInSlot(2) != null) {
				tag.setString("Output", furnace.getStackInSlot(2).getDisplayName());
				tag.setInteger("Osize", furnace.getStackInSlot(2).stackSize);
			}
			reader.setTag("Info", tag);
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		int burnTime = reader.getInt("burnTime");
		NBTTagCompound tagCompound = reader.getTag("Info");

		if ((settings & DISPLAY_TIME) > 0)
			result.add(new PanelString("msg.burnTime", burnTime, showLabels));
		if ((settings & DISPLAY_SLOT_1) > 0) {
			String slot1pre = isServer ? "N/A" : I18n.format("msg.ec.None");
			if (tagCompound.hasKey("Cooking"))
				slot1pre = tagCompound.getString("Cooking");
			if (showLabels)
				result.add(new PanelString(I18n.format("msg.cooking", tagCompound.getInteger("Csize"), slot1pre)));
			else
				result.add(new PanelString(String.format("%sx - %s", tagCompound.getInteger("Csize"), slot1pre)));
		}
		if ((settings & DISPLAY_SLOT_2) > 0) {
			String slot2pre = I18n.format("msg.ec.None");
			if (tagCompound.hasKey("Fuel"))
				slot2pre = tagCompound.getString("Fuel");
			if (showLabels)
				result.add(new PanelString(I18n.format("msg.fuel", tagCompound.getInteger("Fsize"), slot2pre)));
			else
				result.add(new PanelString(String.format("%sx - %s", tagCompound.getInteger("Fsize"), slot2pre)));
		}
		if ((settings & DISPLAY_SLOT_3) > 0) {
			String slot3pre = I18n.format("msg.ec.None");
			if (tagCompound.hasKey("Output"))
				slot3pre = tagCompound.getString("Output");
			if (showLabels)
				result.add(new PanelString(I18n.format("msg.output", tagCompound.getInteger("Osize"), slot3pre)));
			else
				result.add(new PanelString(String.format("%sx - %s", tagCompound.getInteger("Osize"), slot3pre)));
		}
		if ((settings & DISPLAY_BURNING) > 0)
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
