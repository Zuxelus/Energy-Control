package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.List;

import cofh.lib.inventory.ItemStorageCoFH;
import cofh.lib.item.IAugmentItem;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermal.lib.tileentity.DynamoTileBase;
import cofh.thermal.lib.tileentity.ThermalTileBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.IEnergyStorage;

public class CrossThermalExpansion extends CrossModBase {

	@Override
	public CompoundNBT getEnergyData(TileEntity te) {
		if (te instanceof ThermalTileBase)
			return setStorage(((ThermalTileBase) te).getEnergyStorage());
		return null;
	}

	@Override
	public CompoundNBT getCardData(TileEntity te) {
		try {
			if (te instanceof ThermalTileBase) {
				ThermalTileBase base = (ThermalTileBase) te;
				CompoundNBT tag = setStorage(base.getEnergyStorage());
				tag.putBoolean("active", base.getCurSpeed() != 0);
				if (te instanceof DynamoTileBase) {
					tag.putInt("prod", base.getCurSpeed());
					tag.putInt("max_prod", base.getMaxSpeed());
				} else {
					tag.putInt("usage", base.getCurSpeed());
					tag.putInt("max_usage", base.getMaxSpeed());
				}
				tag.putString("rsmode", base.redstoneControl().getMode().name());
				String augmentation = "";
				if (base.augSize() > 0) {
					Field field = ThermalTileBase.class.getDeclaredField("augments");
					field.setAccessible(true);
					List<ItemStorageCoFH> augments = (List<ItemStorageCoFH>) field.get(base);
					for (int i = 0; i < augments.size(); i++) {
						ItemStack stack = augments.get(i).getItemStack();
						if (!stack.isEmpty() && stack.getItem() instanceof IAugmentItem)
							if (augmentation.equals(""))
								augmentation = StringHelper.localize(stack.getDescriptionId());
							else
								augmentation = augmentation + "," + StringHelper.localize(stack.getDescriptionId());
					}
				}
				tag.putString("augmentation", augmentation);
				return tag;
			}
		} catch (Throwable t) { };
		return null;
	}

	public static CompoundNBT setStorage(IEnergyStorage storage) {
		CompoundNBT tag = new CompoundNBT();
		tag.putString("euType", "RF");
		tag.putDouble("storage", storage.getEnergyStored());
		tag.putDouble("maxStorage", storage.getMaxEnergyStored());
		return tag;
	}
}
