package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardDraconicEvolution extends ItemCardBase {
	public ItemCardDraconicEvolution() {
		super(ItemCardType.CARD_DRACONIC_EVOLUTION, "card_draconic_evolution");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.DRACONIC_EVOLUTION).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if ((settings & 1) > 0)
			result.add(new PanelString("msg.ec.Status", reader.getInt("status"), showLabels));
		result.add(new PanelString("msg.ec.CoreTemp", reader.getDouble("temp"), showLabels));
		if ((settings & 2) > 0)
			result.add(new PanelString("msg.ec.Diameter", reader.getDouble("diam"), showLabels));
		result.add(new PanelString("msg.ec.GenerationRate", reader.getDouble("rate"), showLabels));
		if ((settings & 4) > 0)
			result.add(new PanelString("msg.ec.InputRate", reader.getDouble("input"), showLabels));
		if ((settings & 8) > 0)
			result.add(new PanelString("msg.ec.FieldStrength", reader.getDouble("shield"), showLabels));
		if ((settings & 16) > 0)
			result.add(new PanelString("msg.ec.Saturation", reader.getInt("saturation"), showLabels));
		if ((settings & 32) > 0) {
			result.add(new PanelString("msg.ec.ConvertedFuel", reader.getInt("fuel"), showLabels));
			result.add(new PanelString("msg.ec.ConversionRate", reader.getDouble("fuelRate") * 1000000, showLabels));
			result.add(new PanelString("msg.ec.Fuel", reader.getInt("fuelMax"), showLabels));
		}
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<PanelSetting>(6);
		result.add(new PanelSetting(I18n.format("msg.ec.cbStatus"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbDiameter"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInputRate"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbFieldStrength"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbSaturation"), 16));
		result.add(new PanelSetting(I18n.format("msg.ec.cbFuel"), 32));
		return result;
	}
}