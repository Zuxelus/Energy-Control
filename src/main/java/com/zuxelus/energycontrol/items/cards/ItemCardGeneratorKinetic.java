package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.utils.CardState;
import com.zuxelus.energycontrol.utils.PanelSetting;
import com.zuxelus.energycontrol.utils.PanelString;

import ic2.core.block.kineticgenerator.tileentity.TileEntityWindKineticGenerator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardGeneratorKinetic extends ItemCardBase {

	public ItemCardGeneratorKinetic() {
		super(ItemCardType.CARD_GENERATOR_KINETIC, "card_generator_kinetic");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.card_generator_kinetic";
	}

	@Override
	public CardState update(World world, ItemCardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null) 
			return CardState.NO_TARGET;
		
		TileEntity entity = world.getTileEntity(target);
		NBTTagCompound tag = CrossModLoader.crossIc2.getGeneratorKineticData(entity);
		if (tag == null || !tag.hasKey("type"))
			return CardState.NO_TARGET;
		
		reader.setInt("type", tag.getInteger("type"));
		switch (tag.getInteger("type")) {
		case 1:
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			break;
		case 2:
			reader.setDouble("output", tag.getDouble("output"));
			reader.setDouble("wind", tag.getDouble("wind"));
			reader.setDouble("multiplier", tag.getDouble("multiplier"));
			reader.setInt("height", tag.getInteger("height"));
			reader.setDouble("health", tag.getDouble("health"));
			break;
		}
		//reader.setBoolean("active", tag.getBoolean("active"));
		return CardState.OK;
	}

	@Override
	protected List<PanelString> getStringData(int displaySettings, ItemCardReader reader, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		switch (reader.getInt("type")) {
		case 1:
			if ((displaySettings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelStorage", reader.getDouble("storage"), showLabels));
			if ((displaySettings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMaxStorage", reader.getDouble("maxStorage"), showLabels));
			/*if ((displaySettings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelStorage", reader.getInt("coils"), showLabels));
			if ((displaySettings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutputHU", reader.getInt("output"), showLabels));*/
			break;
		case 2:
			if ((displaySettings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutputKU", reader.getDouble("output"), showLabels));
			if ((displaySettings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelWindStrength", reader.getDouble("wind"), showLabels));
			if ((displaySettings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble("multiplier"), showLabels));
			if ((displaySettings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelHeight", reader.getInt("height"), showLabels));
			if ((displaySettings & 16) > 0)
				result.add(new PanelString("msg.ec.InfoPanelRotorHealth", reader.getDouble("health"), showLabels));
			break;
		}
		/*if ((displaySettings & 16) > 0)
			ItemCardType.addOnOff(result, reader, reader.getBoolean("active"));*/
		return result;
	}

	@Override
	protected List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(5);
		return result;
	}
}