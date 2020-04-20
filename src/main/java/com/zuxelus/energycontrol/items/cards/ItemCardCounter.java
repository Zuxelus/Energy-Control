package com.zuxelus.energycontrol.items.cards;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;
import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;
import com.zuxelus.energycontrol.utils.CardState;
import com.zuxelus.energycontrol.utils.PanelSetting;
import com.zuxelus.energycontrol.utils.PanelString;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardCounter extends ItemCardBase {
	public ItemCardCounter() {
		super(ItemCardType.CARD_COUNTER, "cardCounter");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.ItemCardCounter";
	}

	@Override
	public CardState update(World world, ItemCardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null) 
			return CardState.NO_TARGET;
		
		TileEntity tileEntity = world.getTileEntity(target);
		if (tileEntity == null)
			return CardState.NO_TARGET;
		
		if (tileEntity instanceof TileEntityEnergyCounter) {
			TileEntityEnergyCounter counter = (TileEntityEnergyCounter) tileEntity;
			reader.setDouble("energy", counter.counter);
			return CardState.OK;
		}
		if (tileEntity instanceof TileEntityAverageCounter) {
			TileEntityAverageCounter avgCounter = (TileEntityAverageCounter) tileEntity;
			reader.setInt("average", avgCounter.getClientAverage());
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	protected List<PanelString> getStringData(int displaySettings, ItemCardReader reader, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		// average counter
		if (reader.hasField("average")) {
			if ((displaySettings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getInt("average"), showLabels));
			return result;
		}
		// energy counter
		if ((displaySettings & 1) > 0) {
			double energy = reader.getDouble("energy");
			result.add(new PanelString("msg.ec.InfoPanelEnergy", energy, showLabels));
		}
		return result;
	}

	@Override
	protected List<PanelSetting> getSettingsList(ItemStack stack) {
		return null;
	}
}