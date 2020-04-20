package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.EnergyStorageData;
import com.zuxelus.energycontrol.utils.CardState;
import com.zuxelus.energycontrol.utils.PanelSetting;
import com.zuxelus.energycontrol.utils.PanelString;

import ic2.api.energy.EnergyNet;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardGenerator extends ItemCardBase {
	public ItemCardGenerator() {
		super(ItemCardType.CARD_GENERATOR, "cardGenerator");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.ItemCardGenerator";
	}

	@Override
	public CardState update(World world, ItemCardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null) 
			return CardState.NO_TARGET;
		
		TileEntity entity = world.getTileEntity(target);
		EnergyStorageData data = CrossModLoader.crossIc2.getGeneratorData(entity);
		if (data != null) {
			reader.setDouble("storage", data.values.get(1));
			reader.setDouble("maxStorage", data.values.get(2));
			reader.setDouble("production", data.values.get(0));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	protected List<PanelString> getStringData(int displaySettings, ItemCardReader reader, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		if ((displaySettings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelStorage", reader.getDouble("storage"), showLabels));
		if ((displaySettings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMaxStorage", reader.getDouble("maxStorage"), showLabels));
		if ((displaySettings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("production"), showLabels));
		return result;
	}

	@Override
	protected List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(5);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelStorage"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMaxStorage"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 4, damage));
		return result;
	}
}