package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardGenerator extends ItemCardBase {

	public ItemCardGenerator() {
		super(ItemCardType.CARD_GENERATOR, "card_generator");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.IC2).getGeneratorData(te);
		if (tag == null)
			 tag = CrossModLoader.getCrossMod(ModIDs.TECH_REBORN).getGeneratorData(te);
		if (tag == null)
			 tag = CrossModLoader.getCrossMod(ModIDs.THERMAL_EXPANSION).getGeneratorData(te);
		if (tag == null || !tag.hasKey("type"))
			return CardState.NO_TARGET;

		reader.setInt("type", tag.getInteger("type"));
		reader.setString("euType", tag.getString("euType"));
		switch (tag.getInteger("type")) {
		case 1:
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setDouble("production", tag.getDouble("production"));
			break;
		case 2:
			reader.setDouble("production", tag.getDouble("production"));
			reader.setDouble("multiplier", tag.getDouble("multiplier"));
			break;
		case 3:
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setDouble("production", tag.getDouble("production"));
			reader.setDouble("multiplier", tag.getDouble("multiplier"));
			break;
		case 4:
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setInt("items", tag.getInteger("items"));
			reader.setDouble("production", tag.getDouble("production"));
			reader.setDouble("multiplier", tag.getDouble("multiplier"));
			break;
		case 5:
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setDouble("production", tag.getDouble("production"));
			reader.setInt("burnTime", tag.getInteger("burnTime"));
			break;
		case 6:
			reader.setString("status", tag.getString("status"));
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setDouble("production", tag.getDouble("production"));
			reader.setDouble("multiplier", tag.getDouble("multiplier"));
			reader.setInt("progress", tag.getInteger("progress"));
			reader.setInt("coilCount", tag.getInteger("coilCount"));
			break;
		case 7:
			reader.setDouble("heat", tag.getDouble("heat"));
			reader.setInt("production", tag.getInteger("production"));
			reader.setInt("consumption", tag.getInteger("consumption"));
			reader.setInt("heatChange", tag.getInteger("heatChange"));
			reader.setInt("water", tag.getInteger("water"));
			reader.setDouble("calcification", tag.getDouble("calcification"));
			reader.setInt("pressure", tag.getInteger("pressure"));
			break;
		case 8:
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setDouble("progress", tag.getDouble("progress"));
			reader.setInt("steam", tag.getInteger("steam"));
			reader.setInt("water", tag.getInteger("water"));
			break;
		}
		reader.setBoolean("active", tag.getBoolean("active"));
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		String euType = reader.getString("euType");
		switch (reader.getInt("type")) {
		case 1:
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("maxStorage"), showLabels));
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			break;
		case 2:
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble("multiplier"), showLabels));
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			break;
		case 3:
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("maxStorage"), showLabels));
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble("multiplier"), showLabels));
			break;
		case 4:
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("maxStorage"), showLabels));
			if ((settings & 16) > 0)
				result.add(new PanelString("msg.ec.InfoPanelPellets", reader.getInt("items"), showLabels));
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble("multiplier"), showLabels));
			break;
		case 5:
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("maxStorage"), showLabels));
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			if ((settings & 64) > 0)
				result.add(new PanelString("msg.ec.InfoPanelBurnTime", reader.getInt("burnTime"), showLabels));
			break;
		case 6:
			result.add(new PanelString("msg.ec.InfoPanelStatus", reader.getString("status"), showLabels));
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("maxStorage"), showLabels));
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble("multiplier"), showLabels));
			if ((settings & 64) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelProgress", reader.getInt("progress"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelCoils", reader.getInt("coilCount"), showLabels));
			}
			break;
		case 7:
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelHeat", reader.getDouble("heat"), "C", showLabels));
			if ((settings & 8) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelHeatChange", reader.getInt("heatChange"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getInt("consumption"), "mB/t", showLabels));
				result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getInt("production"), "mB/t", showLabels));
			}
			if ((settings & 64) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelWater", reader.getInt("water"), "mB", showLabels));
				result.add(new PanelString("msg.ec.InfoPanelPressure", reader.getInt("pressure"), "Bar", showLabels));
				result.add(new PanelString("ic2.SteamGenerator.gui.calcification", reader.getDouble("calcification"), "%", showLabels));
			}
			break;
		case 8:
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getDouble("storage"), "EU", showLabels));
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getDouble("maxStorage"), "EU", showLabels));
			if ((settings & 64) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelProgress", reader.getDouble("progress"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelSteam", reader.getInt("steam"), "mB", showLabels));
				result.add(new PanelString("msg.ec.InfoPanelWater", reader.getInt("water"), "mB", showLabels));
			}
			break;
		}
		if ((settings & 32) > 0)
			addOnOff(result, isServer, reader.getBoolean("active"));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(7);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelCapacity"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMultiplier"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelItems"), 16));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 32));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelAdditionalInfo"), 64));
		return result;
	}
}
