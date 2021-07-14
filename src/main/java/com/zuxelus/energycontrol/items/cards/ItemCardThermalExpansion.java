package com.zuxelus.energycontrol.items.cards;

import java.lang.reflect.Field;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import cofh.core.block.TilePowered;
import cofh.core.fluid.FluidTankCore;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.helpers.StringHelper;
import cofh.redstoneflux.api.IEnergyStorage;
import cofh.thermalexpansion.block.machine.TileInsolator;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import cofh.thermalexpansion.block.machine.TileSmelter;
import cofh.thermalexpansion.item.ItemAugment;
import ic2.core.block.generator.tileentity.TileEntityRTGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardThermalExpansion extends ItemCardBase {

	public ItemCardThermalExpansion() {
		super(ItemCardType.CARD_THERMAL_EXPANSION, "card_thermal_expansion");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		try {
			if (te instanceof TileMachineBase) {
				TileMachineBase machine = (TileMachineBase) te;
				reader.setInt("type", 1);
				reader.setBoolean("active", machine.isActive);
				reader.setInt("usage", machine.getInfoEnergyPerTick());
				reader.setString("rsmode", machine.getControl().name());
				IEnergyStorage storage = machine.getEnergyStorage();
				if (storage != null) {
					reader.setInt("storage", storage.getEnergyStored());
					reader.setInt("maxStorage", storage.getMaxEnergyStored());
				}
				Field field = TileMachineBase.class.getDeclaredField("energyConfig");
				field.setAccessible(true);
				EnergyConfig energyConfig = (EnergyConfig) field.get(machine);
				reader.setString("power", String.format("%s-%s RF/t", energyConfig.minPower, energyConfig.maxPower));
				String augmentation = "";
				if (machine.getAugmentSlots().length == 0)
					augmentation = StringHelper.localize("info.cofh.upgradeRequired");
				else
					for (int i = 0; i < machine.getAugmentSlots().length; i++) {
						ItemStack stack = machine.getAugmentSlots()[i]; 
						if (!stack.isEmpty() && stack.getItem() instanceof ItemAugment) {
							ItemAugment item = (ItemAugment) stack.getItem();
							if (augmentation.equals(""))
								augmentation = StringHelper.localize(String.format("item.thermalexpansion.augment.%s.name", item.getAugmentIdentifier(stack)));
							else
								augmentation = augmentation + "," + StringHelper.localize(String.format("item.thermalexpansion.augment.%s.name", item.getAugmentIdentifier(stack)));
						}
					}
				reader.setString("augmentation", augmentation);
				if (te instanceof TileSmelter) {
					reader.setInt("type", 2);
					if (((TileSmelter) machine).lockPrimary)
						reader.setString("lock", StringHelper.localize("gui.thermalexpansion.machine.smelter.modeLocked"));
					else
						reader.setString("lock", StringHelper.localize("gui.thermalexpansion.machine.smelter.modeUnlocked"));
				}
				if (te instanceof TileInsolator) {
					reader.setInt("type", 3);
					if (((TileInsolator) machine).lockPrimary)
						reader.setString("lock", StringHelper.localize("gui.thermalexpansion.machine.insolator.modeLocked"));
					else
						reader.setString("lock", StringHelper.localize("gui.thermalexpansion.machine.insolator.modeUnlocked"));
					FluidTankCore tank = ((TileInsolator) machine).getTank();
					reader.setString("water", String.format("%s / %s mB", tank.getFluidAmount(), tank.getCapacity()));
				}
				return CardState.OK;
			}
		} catch (Throwable t) {
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		result.add(new PanelString("msg.ec.InfoPanelPowerUsage", reader.getInt("usage"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelPower", reader.getString("power"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelEnergy", String.format("%s / %s RF",reader.getInt("storage"), reader.getInt("maxStorage")), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelRedstoneMode", reader.getString("rsmode"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelAugmentation", "", showLabels));
		String[] augmentation = reader.getString("augmentation").split(",");
		for (int i = 0; i < augmentation.length; i++)
			result.add(new PanelString(" " + augmentation[i]));
		addOnOff(result, isServer, reader.getBoolean("active"));
		switch (reader.getInt("type")) {
		case 2:
			result.add(new PanelString(reader.getString("lock")));
			break;
		case 3:
			result.add(new PanelString(reader.getString("lock")));
			result.add(new PanelString("msg.ec.InfoPanelWater", reader.getString("water"), showLabels));
			break;
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_THERMAL_EXPANSION;
	}
}
