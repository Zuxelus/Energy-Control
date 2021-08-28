package com.zuxelus.energycontrol.items.cards;

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

import java.util.ArrayList;
import java.util.List;

public class ItemCardGeneratorHeat extends ItemCardBase {

	public ItemCardGeneratorHeat() {
		super(ItemCardType.CARD_GENERATOR_HEAT, "card_generator_heat");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null) 
			return CardState.NO_TARGET;

		TileEntity entity = world.getTileEntity(target);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.IC2).getGeneratorHeatData(entity);
		if (tag == null || !tag.hasKey("type"))
			return CardState.NO_TARGET;

		reader.setInt("type", tag.getInteger("type"));
		switch (tag.getInteger("type")) {
		case 1: // TileEntityElectricHeatGenerator
		case 3: // TileEntityLiquidHeatExchanger
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setInt("items", tag.getInteger("items"));
			break;
		case 2: // TileEntityFluidHeatGenerator
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			break;
		case 4: // TileEntityRTHeatGenerator
			reader.setInt("items", tag.getInteger("items"));
			reader.setDouble("multiplier", tag.getDouble("multiplier"));
			break;
		}
		reader.setInt("output", tag.getInteger("output"));
		reader.setInt("energy", tag.getInteger("energy"));
		reader.setBoolean("active", tag.getBoolean("active"));
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if ((settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutputHU", reader.getInt("output"), showLabels));
		if ((settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelBufferHU", reader.getInt("energy"), showLabels));
		switch (reader.getInt("type")) {
		case 1: // TileEntityElectricHeatGenerator
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelStorageEU", reader.getDouble("storage"), showLabels));
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacityEU", reader.getDouble("maxStorage"), showLabels));
			if ((settings & 16) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCoils", reader.getInt("items"), showLabels));
			break;
		case 2: // TileEntityFluidHeatGenerator
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelStorageL", reader.getDouble("storage"), showLabels));
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacityL", reader.getDouble("maxStorage"), showLabels));
			break;
		case 3: // TileEntityLiquidHeatExchanger
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelStorageL", reader.getDouble("storage"), showLabels));
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacityL", reader.getDouble("maxStorage"), showLabels));
			if ((settings & 16) > 0)
				result.add(new PanelString("msg.ec.InfoPanelConductors", reader.getInt("items"), showLabels));
			break;
		case 4: // TileEntityRTHeatGenerator
			if ((settings & 16) > 0)
				result.add(new PanelString("msg.ec.InfoPanelPellets", reader.getInt("items"), showLabels));
			if ((settings & 32) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getInt("multiplier"), showLabels));
			break;
		case 5: // TileEntitySolidHeatGenerator
			break;
		}
		if ((settings & 64) > 0)
			addOnOff(result, isServer, reader.getBoolean("active"));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(7);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelBuffer"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelStorage"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelCapacity"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelItems"), 16));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMultiplier"), 32));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 64));
		return result;
	}

	@Override
	public int getKitId() {
		return ItemCardType.KIT_GENERATOR;
	}
}
