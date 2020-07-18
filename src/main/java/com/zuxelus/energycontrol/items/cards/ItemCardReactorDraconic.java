package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.reactor.IReactor;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardReactorDraconic extends ItemCardBase {
	public ItemCardReactorDraconic() {
		super(ItemCardType.CARD_REACTOR_DRACONIC, "card_reactor_draconic");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		if (!(te instanceof TileReactorCore))
			return CardState.NO_TARGET;

		TileReactorCore reactor = ((TileReactorCore)te);
		reader.setString("status", reactor.reactorState.value.name());
		reader.setDouble("temp", reactor.temperature.value);
		reader.setDouble("rate", reactor.generationRate.value);
		reader.setDouble("input", reactor.fieldInputRate.value);
		reader.setDouble("diam", reactor.getCoreDiameter());
		reader.setInt("saturation", reactor.saturation.value);
		reader.setDouble("fuel", reactor.convertedFuel.value);
		reader.setDouble("shield", reactor.shieldCharge.value);
		reader.setDouble("fuelMax", reactor.reactableFuel.value);
		reader.setDouble("fuelRate", reactor.fuelUseRate.value);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if ((displaySettings & 1) > 0)
			result.add(new PanelString("msg.ec.Status", reader.getString("status"), showLabels));
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
			result.add(new PanelString("msg.ec.ConvertedFuel", reader.getDouble("fuel"), showLabels));
			result.add(new PanelString("msg.ec.ConversionRate", reader.getDouble("fuelRate") * 1000000, showLabels));
			result.add(new PanelString("msg.ec.Fuel", reader.getDouble("fuelMax"), showLabels));
		}
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(6);
		result.add(new PanelSetting(I18n.format("msg.ec.cbStatus"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbDiameter"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInputRate"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbFieldStrength"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbSaturation"), 16, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbFuel"), 32, damage));
		return result;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_DRACONIC;
	}
}