package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.utils.StringUtils;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardGeneratorArray extends ItemCardBase {
	private static final double STATUS_NOT_FOUND = Integer.MIN_VALUE;
	private static final double STATUS_OUT_OF_RANGE = Integer.MIN_VALUE + 1;	

	public ItemCardGeneratorArray() {
		super(ItemCardType.CARD_GENERATOR_ARRAY, "card_generator_array");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		int cardCount = reader.getCardCount();
		if (cardCount == 0)
			return CardState.INVALID_CARD;

		double totalEnergy = 0.0;
		boolean foundAny = false;
		boolean outOfRange = false;
		for (int i = 0; i < cardCount; i++) {
			BlockPos target = getCoordinates(reader, i);
			int dx = target.getX() - pos.getX();
			int dy = target.getY() - pos.getY();
			int dz = target.getZ() - pos.getZ();
			if (Math.abs(dx) <= range && Math.abs(dy) <= range && Math.abs(dz) <= range) {
				TileEntity entity = world.getTileEntity(target);
				NBTTagCompound tag = CrossModLoader.crossIc2.getGeneratorData(entity);
				if (tag != null && tag.hasKey("type")) {
					int type = tag.getInteger("type");
					reader.setInt(String.format("_%dtype", i), type);
					switch (type) {
					case 1:
						reader.setDouble(String.format("_%dstorage", i), tag.getDouble("storage"));
						reader.setDouble(String.format("_%dmaxStorage", i), tag.getDouble("maxStorage"));
						reader.setDouble(String.format("_%dproduction", i), tag.getDouble("production"));
						break;
					case 2:
						reader.setDouble(String.format("_%dproduction", i), tag.getDouble("production"));
						break;
					}
					foundAny = true;
				} else
					reader.setDouble(String.format("_%dproduction", i), STATUS_NOT_FOUND);
			} else {
				reader.setDouble(String.format("_%dproduction", i), STATUS_OUT_OF_RANGE);
				outOfRange = true;
			}
		}
		if (!foundAny) {
			if (outOfRange)
				return CardState.OUT_OF_RANGE;
			return CardState.NO_TARGET;
		}
		return CardState.OK;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		PanelString line;
		double totalEnergy = 0;
		double totalStorage = 0;
		double totalOutput = 0;
		boolean showSummary = (displaySettings & 64) > 0;
		for (int i = 0; i < reader.getCardCount(); i++) {
			if (reader.hasField(String.format("_%dtype", i))) {
			int type = reader.getInt(String.format("_%dtype", i));
			double energy = 0;
			double storage = 0;
			double output = 0;
			switch (type) {
			case 1:
				energy = reader.getDouble(String.format("_%dstorage", i));
				storage = reader.getDouble(String.format("_%dmaxStorage", i));
				output = reader.getDouble(String.format("_%dproduction", i));
			case 2:
				output = reader.getDouble(String.format("_%dproduction", i));
				break;
			}

			boolean isOutOfRange = output == STATUS_OUT_OF_RANGE;
			boolean isNotFound = output == STATUS_NOT_FOUND;
			if (showSummary && !isOutOfRange && !isNotFound) {
				totalEnergy += energy;
				totalStorage += storage;
				totalOutput += output;
			}
		
			if ((displaySettings & 32) > 0) {
				if (isOutOfRange) {
					result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelOutOfRangeN", i + 1)));
				} else if (isNotFound) {
					result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelNotFoundN", i + 1)));
				} else {
					if ((displaySettings & 1) > 0 && type == 1) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelStorageN", i + 1, StringUtils.getFormatted("", energy, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", energy, false)));
					}
					if ((displaySettings & 2) > 0 && type == 1) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelMaxStorageN", i + 1, StringUtils.getFormatted("", storage, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", storage, false)));
					}
					if ((displaySettings & 8) > 0) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelOutputN", i + 1, StringUtils.getFormatted("", output, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", output, false)));
					}
				}
			}
			}
		}
		if (showSummary) {
			if ((displaySettings & 1) > 0) 
				result.add(new PanelString("msg.ec.InfoPanelStorage", totalEnergy, showLabels));
			if ((displaySettings & 2) > 0) 
				result.add(new PanelString("msg.ec.InfoPanelMaxStorage", totalStorage, showLabels));
			if ((displaySettings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput", totalOutput, showLabels));
		}
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(5);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelStorage"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMaxStorage"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEach"), 32, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelTotal"), 64, damage));
		return result;
	}
	
	@Override
	public boolean isRemoteCard() {
		return false;
	}
}