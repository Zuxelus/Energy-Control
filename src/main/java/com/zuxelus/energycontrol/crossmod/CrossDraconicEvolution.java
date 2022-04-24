package com.zuxelus.energycontrol.crossmod;

import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.recipes.Recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CrossDraconicEvolution extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te == null)
			return null;

		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("euType", "RF");
		if (te instanceof IExtendedRFStorage) {
			tag.setDouble("maxStorage", (double) ((IExtendedRFStorage)te).getExtendedCapacity());
			tag.setDouble("storage", (double) ((IExtendedRFStorage)te).getExtendedStorage());
			return tag;
		}
		if (te instanceof TileEnergyInfuser) {
			tag.setDouble("maxStorage", ((TileEnergyInfuser) te).energyStorage.getMaxEnergyStored());
			tag.setDouble("storage", ((TileEnergyInfuser) te).energyStorage.getEnergyStored());
			return tag;
		}
		if (te instanceof TileCrystalDirectIO) {
			tag.setDouble("maxStorage", ((TileCrystalDirectIO) te).getMaxEnergyStored());
			tag.setDouble("storage", ((TileCrystalDirectIO) te).getEnergyStored());
			return tag;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (te instanceof TileReactorCore) {
			TileReactorCore reactor = ((TileReactorCore) te);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("status", reactor.reactorState.value.name());
			tag.setDouble("temp", reactor.temperature.value);
			tag.setDouble("rate", reactor.generationRate.value);
			tag.setDouble("input", reactor.fieldInputRate.value);
			tag.setDouble("diam", reactor.getCoreDiameter());
			tag.setInteger("saturation", reactor.saturation.value);
			tag.setDouble("fuel", reactor.convertedFuel.value);
			tag.setDouble("shield", reactor.shieldCharge.value);
			tag.setDouble("fuelMax", reactor.reactableFuel.value);
			tag.setDouble("fuelRate", reactor.fuelUseRate.value);
			return tag;
		}
		return null;
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_DRACONIC,
			new Object[] { "RF", "PB", 'P', Items.PAPER, 'R', "dustRedstone",
				'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'B', "ingotDraconium" });
	}
}
