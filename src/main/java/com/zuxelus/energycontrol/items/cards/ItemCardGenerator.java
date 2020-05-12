package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.EnergyStorageData;

import ic2.api.energy.EnergyNet;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
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
		
		TileEntity entity = world.getTileEntity(target);
		NBTTagCompound tag = CrossModLoader.crossIc2.getGeneratorData(entity);
		if (tag == null || !tag.hasKey("type"))
			return CardState.NO_TARGET;
		
		reader.setInt("type", tag.getInteger("type"));
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
		}
		reader.setBoolean("active", tag.getBoolean("active"));
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		switch (reader.getInt("type")) {
		case 1:
			if ((displaySettings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelStorage", reader.getDouble("storage"), showLabels));
			if ((displaySettings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMaxStorage", reader.getDouble("maxStorage"), showLabels));
			if ((displaySettings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("production"), showLabels));
			break;
		case 2:
			if ((displaySettings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble("multiplier"), showLabels));
			if ((displaySettings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("production"), showLabels));
			break;
		}
		if ((displaySettings & 16) > 0)
			addOnOff(result, reader.getBoolean("active"));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(5);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelStorage"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMaxStorage"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMultiplier"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 16, damage));
		return result;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_GENERATOR;
	}
}