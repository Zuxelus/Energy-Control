package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardGregTech;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitGregTech;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import gregtech.tileentity.energy.storage.MultiTileEntityBatteryBox;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class CrossGregTech6 extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		if (te instanceof MultiTileEntityBatteryBox) {
			tag.setDouble(DataHelper.ENERGY, ((MultiTileEntityBatteryBox) te).mEnergy);
			tag.setDouble(DataHelper.CAPACITY, ((MultiTileEntityBatteryBox) te).mInput * 80 * ((MultiTileEntityBatteryBox) te).invsize());
			return tag;
		}
		return null;
	}

	@Override
	public ArrayList getHookValues(TileEntity te) {
		return null;
	}

	@Override
	public void registerItems() {
		ItemKitMain.register(ItemKitGregTech::new);
		ItemCardMain.register(ItemCardGregTech::new);
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_GREGTECH,
				new Object[] { "RF", "PB", 'P', Items.paper, 'R', "dyeGray",
					'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'B', "ingotBronze" });

		Recipes.addKitRecipe(ItemCardType.KIT_GREGTECH, ItemCardType.CARD_GREGTECH);
	}
}
