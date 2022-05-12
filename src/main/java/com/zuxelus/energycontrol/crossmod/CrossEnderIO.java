package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.hooks.EnderIOHooks;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardEnderIO;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitEnderIO;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import crazypants.enderio.machine.capbank.TileCapBank;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class CrossEnderIO extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DataHelper.EUTYPE, "RF");
		if (te instanceof TileCapBank) {
			tag.setDouble(DataHelper.ENERGY, ((TileCapBank) te).getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileCapBank) te).getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		// TODO Auto-generated method stub
		return super.getAllTanks(te);
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		if (te instanceof TileCapBank) {
			tag.setDouble(DataHelper.ENERGY, ((TileCapBank) te).getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileCapBank) te).getMaxEnergyStored());
			ArrayList values = getHookValues(te);
			if (values != null)
				tag.setLong("diff", ((Long) values.get(0) - (Long) values.get(20)) / 20);
			return tag;
		}
		return null;
	}

	@Override
	public ArrayList getHookValues(TileEntity te) {
		ArrayList values = EnderIOHooks.map.get(te);
		if (values == null)
			EnderIOHooks.map.put(te, null);
		return values;
	}

	@Override
	public void registerItems() {
		ItemKitMain.register(ItemKitEnderIO::new);
		ItemCardMain.register(ItemCardEnderIO::new);
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_ENDER_IO,
			new Object[] { "IT", "PD", 'P', Items.paper, 'D', "dyePink",
				'T', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'I', "itemSilicon" });

		Recipes.addKitRecipe(ItemCardType.KIT_ENDER_IO, ItemCardType.CARD_ENDER_IO);
	}
}
