package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.utils.DataHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardEnderIO extends ItemCardBase {

	public ItemCardEnderIO() {
		super(ItemCardType.CARD_ENDER_IO, "card_ender_io");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.ENDER_IO).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.hasField(DataHelper.ENERGY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getLong(DataHelper.ENERGY), "RF", showLabels));
		if (reader.hasField(DataHelper.CAPACITY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong(DataHelper.CAPACITY), "RF", showLabels));
		if (reader.hasField(DataHelper.DIFF) && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelDifference", reader.getLong(DataHelper.DIFF), "RF/t", showLabels));
		
		/*String euType = reader.getString("euType");
		if (reader.hasField("storage") && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getDouble("storage"), euType, showLabels));
		if (reader.hasField("maxStorage") && (settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getDouble("maxStorage"), euType, showLabels));
		if (reader.hasField("leakage") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelLeakage", reader.getDouble("leakage"), euType + "/t", showLabels));
		if (reader.hasField("output") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("output"), euType + "/t", showLabels));
		if (reader.hasField("maxInput") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMaxInput", reader.getDouble("maxInput"), euType + "/t", showLabels));
		if (reader.hasField("maxOutput") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMaxOutput", reader.getDouble("maxOutput"), euType + "/t", showLabels));
		if (reader.hasField("difference") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelDifference", reader.getDouble("difference"), euType + "/t", showLabels));
		if (reader.hasField("usage") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelUsing", reader.getDouble("usage"), euType + "/t", showLabels));
		if (reader.hasField("efficiency") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEfficiency", reader.getDouble("efficiency"), "%", showLabels));
		if (reader.hasField("tank") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank"), showLabels));
		if (reader.hasField("usage1") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelUsing", reader.getDouble("usage1"), "t/mB", showLabels));
		if (reader.hasField("tank2") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank2"), showLabels));
		if (reader.hasField("usage2") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelUsing", reader.getDouble("usage2"), "t/mB", showLabels));
		if (reader.hasField("active"))
			addOnOff(result, isServer, reader.getBoolean("active"));*/
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(2);
		return result;
	}
}
