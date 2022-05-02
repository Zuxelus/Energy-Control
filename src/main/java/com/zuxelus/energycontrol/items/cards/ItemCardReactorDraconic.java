package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardReactorDraconic extends ItemCardBase {
	public ItemCardReactorDraconic() {
		super(ItemCardType.CARD_REACTOR_DRACONIC, "card_reactor_draconic");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		if (!(te instanceof TileReactorCore))
			return CardState.NO_TARGET;

		TileReactorCore reactor = ((TileReactorCore) te);
		reader.setInt("status", reactor.reactorState);
		reader.setDouble("temp", reactor.reactionTemperature);
		reader.setDouble("rate", reactor.generationRate);
		reader.setDouble("input", reactor.fieldInputRate);
		reader.setDouble("diam", reactor.getCoreDiameter());
		reader.setInt("saturation", reactor.energySaturation);
		reader.setInt("fuel", reactor.convertedFuel);
		reader.setDouble("shield", reactor.fieldCharge);
		reader.setInt("fuelMax", reactor.reactorFuel);
		reader.setDouble("fuelRate", reactor.fuelUseRate);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if ((displaySettings & 1) > 0)
			result.add(new PanelString("msg.ec.Status", reader.getInt("status"), showLabels));
		result.add(new PanelString("msg.ec.CoreTemp", reader.getDouble("temp"), showLabels));
		if ((displaySettings & 2) > 0)
			result.add(new PanelString("msg.ec.Diameter", reader.getDouble("diam"), showLabels));
		result.add(new PanelString("msg.ec.GenerationRate", reader.getDouble("rate"), showLabels));
		if ((displaySettings & 4) > 0)
			result.add(new PanelString("msg.ec.InputRate", reader.getDouble("input"), showLabels));
		if ((displaySettings & 8) > 0)
			result.add(new PanelString("msg.ec.FieldStrength", reader.getDouble("shield"), showLabels));
		if ((displaySettings & 16) > 0)
			result.add(new PanelString("msg.ec.Saturation", reader.getInt("saturation"), showLabels));
		if ((displaySettings & 32) > 0) {
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
		result.add(new PanelSetting(I18n.format("msg.ec.cbStatus"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbDiameter"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInputRate"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbFieldStrength"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbSaturation"), 16, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbFuel"), 32, damage));
		return result;
	}
}