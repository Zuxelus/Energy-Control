package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;

import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;
import com.brandon3055.draconicevolution.common.tileentities.TileGenerator;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyTransceiver;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileRemoteEnergyBase;
import com.brandon3055.draconicevolution.common.tileentities.gates.TileFluxGate;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyPylon;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileInvisibleMultiblock;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorEnergyInjector;
import com.zuxelus.energycontrol.hooks.DraconicEvolutionHooks;
import com.zuxelus.energycontrol.hooks.HBMHooks;
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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

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
		if (te instanceof TileGenerator) {
			tag.setDouble(DataHelper.ENERGY, ((TileGenerator) te).storage.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileGenerator) te).storage.getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileRemoteEnergyBase) {
			tag.setDouble(DataHelper.ENERGY, ((TileRemoteEnergyBase) te).getStorage().getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileRemoteEnergyBase) te).getStorage().getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileEnergyPylon) {
			tag.setDouble(DataHelper.ENERGY, ((TileEnergyPylon) te).getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileEnergyPylon) te).getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileInvisibleMultiblock) {
			TileEnergyStorageCore core = ((TileInvisibleMultiblock) te).getMaster();
			if (core == null)
				return null;
			tag.setDouble(DataHelper.ENERGY, core.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, core.getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		if (te instanceof TileInvisibleMultiblock)
			return getStorageData(((TileInvisibleMultiblock) te).getMaster());
		if (te instanceof TileEnergyStorageCore)
			return getStorageData((TileEnergyStorageCore) te);
		if (te instanceof TileEnergyInfuser) {
			tag.setDouble(DataHelper.ENERGY, ((TileEnergyInfuser) te).energy.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileEnergyInfuser) te).energy.getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileGenerator) {
			tag.setDouble(DataHelper.ENERGY, ((TileGenerator) te).storage.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileGenerator) te).storage.getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileRemoteEnergyBase) {
			tag.setDouble(DataHelper.ENERGY, ((TileRemoteEnergyBase) te).getStorage().getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileRemoteEnergyBase) te).getStorage().getMaxEnergyStored());
			tag.setInteger("connections", ((TileRemoteEnergyBase) te).linkedDevices.size());
			return tag;
		}
		if (te instanceof TileEnergyPylon) {
			tag.setDouble(DataHelper.ENERGY, ((TileEnergyPylon) te).getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileEnergyPylon) te).getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileFluxGate) {
			tag.setInteger("flowLow", ((TileFluxGate)te).flowRSLow);
			tag.setInteger("flowHigh", ((TileFluxGate)te).flowRSHigh);
			return tag;
		}
		if (te instanceof TileReactorEnergyInjector) {
			TileReactorCore core = ((TileReactorEnergyInjector) te).getCore();
			return getReactorData((TileReactorCore) core);
		}
		if (te instanceof TileReactorCore)
			return getReactorData((TileReactorCore) te);
		return null;
	}

	private NBTTagCompound getStorageData(TileEnergyStorageCore core) {
		if (core == null)
			return null;

		NBTTagCompound tag = new NBTTagCompound();
		tag.setDouble(DataHelper.ENERGY, core.getEnergyStored());
		tag.setDouble(DataHelper.CAPACITY, core.getMaxEnergyStored());
		ArrayList values = getHookValues(core);
		if (values != null)
			tag.setLong(DataHelper.DIFF, ((Long) values.get(0) - (Long) values.get(20)) / 20);
		tag.setInteger("tier", core.getTier() + 1);
		return tag;
	}

	private NBTTagCompound getReactorData(TileReactorCore reactor) {
		if (reactor == null)
			return null;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("status",
				(reactor.reactorState == 0 ? EnumChatFormatting.DARK_GRAY.toString() :
					reactor.reactorState == 1 ? EnumChatFormatting.RED.toString() :
					reactor.reactorState == 2 ? EnumChatFormatting.DARK_GREEN.toString() : EnumChatFormatting.RED.toString()) +
				StatCollector.translateToLocal("gui.de.status" + reactor.reactorState + ".txt"));
		tag.setDouble("temp", reactor.reactionTemperature);
		tag.setDouble(DataHelper.OUTPUT, (long) reactor.generationRate);
		tag.setDouble(DataHelper.CONSUMPTION, (long) reactor.fieldInputRate);
		tag.setDouble("diam", reactor.getCoreDiameter());
		tag.setDouble("saturation", reactor.energySaturation * 100D / reactor.maxEnergySaturation);
		tag.setInteger("fuel", reactor.convertedFuel);
		tag.setDouble("shield", reactor.fieldCharge * 100D / reactor.maxFieldCharge);
		tag.setInteger("fuelMax", reactor.reactorFuel);
		tag.setDouble("fuelRate", reactor.fuelUseRate);
		return tag;
	}

	@Override
	public ArrayList getHookValues(TileEntity te) {
		ArrayList values = DraconicEvolutionHooks.map.get(te);
		if (values == null)
			DraconicEvolutionHooks.map.put(te, null);
		return values;
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
