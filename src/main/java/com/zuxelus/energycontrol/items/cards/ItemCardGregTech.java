package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.StringUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.tileentity.energy.reactors.MultiTileEntityReactorCore2x2;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardGregTech extends ItemCardBase {

	public ItemCardGregTech() {
		super(ItemCardType.CARD_GREGTECH, "card_gregtech");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.GREGTECH).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@SideOnly(Side.CLIENT)
	private String getItemName(ItemStack stack) {
		List<String> list = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
		if (list.size() == 0)
			return stack.getItem().getUnlocalizedName();
		return list.get(0);
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.hasField(DataHelper.HEAT))
			result.add(new PanelString("msg.ec.InfoPanelTemp", reader.getDouble("heat"), "K", showLabels));
		if (reader.hasField(DataHelper.AMOUNTL) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelAmount", reader.getDouble(DataHelper.AMOUNTL), "L", showLabels));
		if (reader.hasField(DataHelper.ENERGY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getDouble(DataHelper.ENERGY), "EU", showLabels));
		if (reader.hasField("battery") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelBattery", reader.getDouble("battery"), "EU", showLabels));
		if (reader.hasField(DataHelper.ENERGYHU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getDouble(DataHelper.ENERGYHU), "HU", showLabels));
		if (reader.hasField(DataHelper.ENERGYRU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getDouble(DataHelper.ENERGYRU), "RU", showLabels));
		if (reader.hasField(DataHelper.OUTPUT) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUT), "EU/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTHU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTHU), "HU/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTL) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTL), "L/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTRU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTRU), "RU/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTST) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTST), "Steam/t", showLabels));
		if (reader.hasField(DataHelper.CONSUMPTION) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble(DataHelper.CONSUMPTION), "EU/t", showLabels));
		if (reader.hasField(DataHelper.CONSUMPTIONHU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble(DataHelper.CONSUMPTIONHU), "HU/t", showLabels));
		if (reader.hasField(DataHelper.CONSUMPTIONL) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble(DataHelper.CONSUMPTIONL), "L/t", showLabels));
		if (reader.hasField(DataHelper.CONSUMPTIONRU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble(DataHelper.CONSUMPTIONRU), "RU/t", showLabels));
		if (reader.hasField(DataHelper.CONSUMPTIONST) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble(DataHelper.CONSUMPTIONST), "Steam/t", showLabels));
		if (reader.hasField(DataHelper.TANK))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK), showLabels));
		if (reader.hasField(DataHelper.TANK2))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK2), showLabels));
		if (reader.hasField(DataHelper.TANK3))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK3), showLabels));
		if (reader.hasField(DataHelper.TANK4))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK4), showLabels));
		if (reader.hasField(DataHelper.CAPACITY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getDouble(DataHelper.CAPACITY), "EU", showLabels));
		if (reader.hasField(DataHelper.CAPACITYL) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getDouble(DataHelper.CAPACITYL), "L", showLabels));
		if (reader.hasField("limit0") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelLimitDischarge", reader.getDouble("limit0"), "EU", showLabels));
		if (reader.hasField("limit1") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelLimitCharge", reader.getDouble("limit1"), "EU", showLabels));
		if (reader.hasField("packet") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMaxPacket", reader.getDouble("packet"), "", showLabels));
		if (reader.hasField("amperage") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelAmperage", reader.getDouble("amperage"), "", showLabels));
		if (reader.hasField("remaining") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelRemaining", reader.getDouble("remaining"), "Minutes", showLabels));
		if (reader.hasField("neutrons") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelNeutrons", reader.getString("neutrons"), showLabels));
		if (reader.hasField("slot0")) {
			ItemStack stack = ItemStack.loadItemStackFromNBT(reader.getTag("slot0"));
			result.add(new PanelString(String.format("msg.ec.InfoPanelSlot%d", 1), String.format("%s x %s", stack.stackSize, StringUtils.getItemName(stack)), showLabels));
		}
		if (reader.hasField("slot1")) {
			ItemStack stack = ItemStack.loadItemStackFromNBT(reader.getTag("slot1"));
			result.add(new PanelString(String.format("msg.ec.InfoPanelSlot%d", 2), String.format("%s x %s", stack.stackSize, StringUtils.getItemName(stack)), showLabels));
		}
		if (reader.hasField("content") && (settings & 2) > 0) {
			String[] list = reader.getString("content").split(",");
			for (String item : list) {
				result.add(new PanelString(item));
			}
		}
		if (reader.hasField(DataHelper.ACTIVE))
			addOnOff(result, isServer, reader.getBoolean(DataHelper.ACTIVE));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}
}
