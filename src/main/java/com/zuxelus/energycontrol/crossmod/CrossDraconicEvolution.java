package com.zuxelus.energycontrol.crossmod;

import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;
import com.brandon3055.draconicevolution.common.tileentities.TileGenerator;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyTransceiver;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardDraconicEvolution;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitDraconicEvolution;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.DataHelper;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class CrossDraconicEvolution extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DataHelper.EUTYPE, "RF");
		if (te instanceof TileEnergyStorageCore) {
			tag.setDouble(DataHelper.ENERGY, ((TileEnergyStorageCore)te).getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileEnergyStorageCore)te).getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileEnergyInfuser) {
			tag.setDouble(DataHelper.ENERGY, ((TileEnergyInfuser) te).energy.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileEnergyInfuser) te).energy.getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileEnergyTransceiver) {
			tag.setDouble(DataHelper.ENERGY, ((TileEnergyTransceiver) te).getStorage().getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileEnergyTransceiver) te).getStorage().getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileGenerator) {
			tag.setDouble(DataHelper.ENERGY, ((TileGenerator) te).storage.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileGenerator) te).storage.getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (te instanceof TileReactorCore) {
			NBTTagCompound tag = new NBTTagCompound();
			TileReactorCore reactor = ((TileReactorCore) te);
			tag.setInteger("status", reactor.reactorState);
			tag.setDouble("temp", reactor.reactionTemperature);
			tag.setDouble("rate", reactor.generationRate);
			tag.setDouble("input", reactor.fieldInputRate);
			tag.setDouble("diam", reactor.getCoreDiameter());
			tag.setInteger("saturation", reactor.energySaturation);
			tag.setInteger("fuel", reactor.convertedFuel);
			tag.setDouble("shield", reactor.fieldCharge);
			tag.setInteger("fuelMax", reactor.reactorFuel);
			tag.setDouble("fuelRate", reactor.fuelUseRate);
			return tag;
		}
		return null;
	}

	@Override
	public void registerItems() {
		ItemKitMain.register(ItemKitDraconicEvolution::new);
		ItemCardMain.register(ItemCardDraconicEvolution::new);
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_DRACONIC_EVOLUTION,
			new Object[] { "RF", "PB", 'P', Items.paper, 'R', "dustRedstone",
				'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'B', "ingotDraconium" });

		Recipes.addKitRecipe(ItemCardType.KIT_DRACONIC_EVOLUTION, ItemCardType.CARD_DRACONIC_EVOLUTION);
	}
}
