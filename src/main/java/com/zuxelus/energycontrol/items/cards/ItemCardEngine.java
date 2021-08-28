package com.zuxelus.energycontrol.items.cards;

import buildcraft.lib.engine.TileEngineBase_BC8;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemCardEngine extends ItemCardBase {

	public ItemCardEngine() {
		super(ItemCardType.CARD_ENGINE, "card_engine");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity entity = world.getTileEntity(target);
		NBTTagCompound tag = getEngineData(entity);
		if (tag == null || !tag.hasKey("type"))
			return CardState.NO_TARGET;

		reader.setInt("type", tag.getInteger("type"));
		if (tag.getInteger("type") == 1) {
			reader.setDouble("output", tag.getDouble("output"));
			reader.setDouble("power", tag.getDouble("power"));
			reader.setDouble("powerLevel", tag.getDouble("powerLevel"));
			reader.setDouble("maxPower", tag.getDouble("maxPower"));
			reader.setDouble("heat", tag.getDouble("heat"));
			reader.setDouble("heatLevel", tag.getDouble("heatLevel"));
			reader.setDouble("speed", tag.getDouble("speed"));
		}
		reader.setBoolean("active", tag.getBoolean("active"));
		return CardState.OK;
	}

	private NBTTagCompound getEngineData(TileEntity te) {
		if (!(te instanceof TileEngineBase_BC8))
			return null;
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", 1);
		tag.setDouble("output",(double) ((TileEngineBase_BC8) te).getCurrentOutput() / Math.pow(10, 6));
		//tag.setBoolean("powered",((TileEngineBase_BC8) te).isRedstonePowered);
		tag.setBoolean("active",((TileEngineBase_BC8) te).isBurning());
		tag.setDouble("power", ((TileEngineBase_BC8) te).getEnergyStored() / Math.pow(10, 6));
		tag.setDouble("powerLevel", ((TileEngineBase_BC8) te).getPowerLevel() * 100);
		tag.setDouble("maxPower", ((TileEngineBase_BC8) te).getMaxPower() / Math.pow(10, 6));
		tag.setDouble("heat", ((TileEngineBase_BC8) te).getHeat());
		tag.setDouble("heatLevel", ((TileEngineBase_BC8) te).getHeatLevel() * 100);
		tag.setDouble("speed", ((TileEngineBase_BC8) te).getPistonSpeed());
		return tag;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.getInt("type") == 1) {
			result.add(new PanelString("msg.ec.InfoPanelOutputMJ", reader.getDouble("output"), showLabels));
			if ((settings & 1) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelHeat", reader.getDouble("heat"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelHeatPercentage", reader.getDouble("heatLevel"), showLabels));
			}
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelStorageMJ", reader.getDouble("power"), showLabels));
			if ((settings & 4) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelPercentage", reader.getDouble("powerLevel"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelMaxStorageMJ", reader.getDouble("maxPower"), showLabels));
			}
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelSpeed", reader.getDouble("speed"), showLabels));
		}
		if ((settings & 16) > 0)
			addOnOff(result, isServer, reader.getBoolean("active"));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(5);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelHeat"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelStorage"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMaxStorage"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelSpeed"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 16));
		return result;
	}

	@Override
	public int getKitId() {
		return ItemCardType.KIT_GENERATOR;
	}
}
