package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.utils.CardState;
import com.zuxelus.energycontrol.utils.PanelSetting;
import com.zuxelus.energycontrol.utils.PanelString;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.reactor.IReactor;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardReactor5x5 extends ItemCardBase {
	public ItemCardReactor5x5() {
		super(ItemCardType.CARD_REACTOR5X5, "cardReactor5x5");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.ItemCardReactor5x5";
	}

	@Override
	public CardState update(World world, ItemCardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null) 
			return CardState.NO_TARGET;
		
		IReactor reactor = ReactorHelper.getReactorAt(world, target);
		if (reactor == null || !(reactor instanceof TileEntityNuclearReactorElectric))
			return CardState.NO_TARGET;
		
		reader.setInt("heat", reactor.getHeat());
		reader.setInt("maxHeat", reactor.getMaxHeat());
		reader.setBoolean("reactorPowered", reactor.produceEnergy());
		reader.setInt("output", ((TileEntityNuclearReactorElectric)reactor).EmitHeat);

		IInventory inventory = (IInventory) reactor;
		int slotCount = inventory.getSizeInventory();
		int dmgLeft = 0;
		for (int i = 0; i < slotCount; i++) {
			ItemStack rStack = inventory.getStackInSlot(i);
			if (rStack != null)
				dmgLeft = Math.max(dmgLeft, ReactorHelper.getNuclearCellTimeLeft(rStack));
		}

		int timeLeft = dmgLeft * reactor.getTickRate() / 20;
		reader.setInt("timeLeft", timeLeft);
		return CardState.OK;
	}

	@Override
	protected List<PanelString> getStringData(int displaySettings, ItemCardReader reader, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		if ((displaySettings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHeat", reader.getInt("heat"), showLabels));
		if ((displaySettings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMaxHeat", reader.getInt("maxHeat"), showLabels));
		if ((displaySettings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMelting", reader.getInt("maxHeat") * 85 / 100, showLabels));
		if ((displaySettings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutputHeat", reader.getInt("output"), showLabels));
		int timeLeft = reader.getInt("timeLeft");
		if ((displaySettings & 32) > 0) {
			int hours = timeLeft / 3600;
			int minutes = (timeLeft % 3600) / 60;
			int seconds = timeLeft % 60;
			result.add(new PanelString("msg.ec.InfoPanelTimeRemaining", String.format("%d:%02d:%02d", hours, minutes, seconds), showLabels));
		}

		int txtColor = 0;
		if ((displaySettings & 1) > 0) {
			String text;
			boolean reactorPowered = reader.getBoolean("reactorPowered");
			if (reactorPowered) {
				txtColor = 0x00ff00;
				text = I18n.format("msg.ec.InfoPanelOn");
			} else {
				txtColor = 0xff0000;
				text = I18n.format("msg.ec.InfoPanelOff");
			}
			if (result.size() > 0) {
				PanelString firstLine = result.get(0);
				firstLine.textRight = text;
				firstLine.colorRight = txtColor;
			} else {
				PanelString line = new PanelString();
				line.textLeft = text;
				line.colorLeft = txtColor;
				result.add(line);
			}
		}
		return result;
	}

	@Override
	protected List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(6);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelHeat"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMaxHeat"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMelting"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 16, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelTimeRemaining"), 32, damage));
		return result;
	}
}