package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;

import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.tileentity.*;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate;
import com.zuxelus.energycontrol.hooks.DraconicEvolutionHooks;
import com.zuxelus.energycontrol.items.cards.ItemCardDraconicEvolution;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitDraconicEvolution;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.utils.DataHelper;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;

public class CrossDraconicEvolution extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DataHelper.EUTYPE, "RF");
		if (te instanceof IExtendedRFStorage) {
			tag.setDouble(DataHelper.ENERGY, (double) ((IExtendedRFStorage) te).getExtendedStorage());
			tag.setDouble(DataHelper.CAPACITY, (double) ((IExtendedRFStorage) te).getExtendedCapacity());
			return tag;
		}
		if (te instanceof TileInvisECoreBlock) {
			IMultiBlockPart core = ((TileInvisECoreBlock) te).getController();
			if (core instanceof TileEnergyStorageCore) {
				tag.setDouble(DataHelper.ENERGY, (double) ((IExtendedRFStorage) core).getExtendedStorage());
				tag.setDouble(DataHelper.CAPACITY, (double) ((IExtendedRFStorage) core).getExtendedCapacity());
				return tag;
			}
		}
		if (te instanceof TileEnergyInfuser) {
			tag.setDouble(DataHelper.ENERGY, ((TileEnergyInfuser) te).energyStorage.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileEnergyInfuser) te).energyStorage.getMaxEnergyStored());

			return tag;
		}
		if (te instanceof TileCrystalBase) {
			tag.setDouble(DataHelper.ENERGY, ((TileCrystalBase) te).getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileCrystalBase) te).getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		NBTTagCompound tag = new NBTTagCompound();
		if (te instanceof TileInvisECoreBlock) {
			IMultiBlockPart core = ((TileInvisECoreBlock) te).getController();
			if (core instanceof TileEnergyStorageCore)
				return getStorageData((TileEnergyStorageCore) core);
		}
		if (te instanceof TileEnergyCoreStabilizer)
			return getStorageData(((TileEnergyCoreStabilizer) te).findCore());
		if (te instanceof TileEnergyStorageCore)
			return getStorageData((TileEnergyStorageCore) te);
		if (te instanceof TileGenerator) {
			tag.setBoolean(DataHelper.ACTIVE, ((TileGenerator) te).active.value);
			tag.setDouble(DataHelper.ENERGY, ((TileGenerator) te).getEnergyStored(null));
			tag.setDouble(DataHelper.CAPACITY, ((TileGenerator) te).getMaxEnergyStored(null));
			tag.setDouble(DataHelper.OUTPUT, ((TileGenerator) te).active.value ? 6 * 14 : 0);
			return tag;
		}
		if (te instanceof TileCrystalBase) {
			tag.setDouble(DataHelper.ENERGY, ((TileCrystalBase) te).getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileCrystalBase) te).getMaxEnergyStored());
			tag.setInteger("connections", ((TileCrystalBase) te).getLinks().size());
			return tag;
		}
		if (te instanceof TileFluxGate) {
			tag.setInteger("flowLow", ((TileFluxGate) te).minFlow.value);
			tag.setInteger("flowHigh", ((TileFluxGate) te).maxFlow.value);
			return tag;
		}
		if (te instanceof TileFluidGate) {
			tag.setInteger("flowLowMB", ((TileFluidGate) te).minFlow.value);
			tag.setInteger("flowHighMB", ((TileFluidGate) te).maxFlow.value);
			return tag;
		}
		if (te instanceof TileReactorComponent)
			return getReactorData(((TileReactorComponent) te).tryGetCore());
		if (te instanceof TileReactorCore)
			return getReactorData((TileReactorCore) te);
		return null;
	}

	private NBTTagCompound getStorageData(TileEnergyStorageCore core) {
		if (core == null)
			return null;

		NBTTagCompound tag = new NBTTagCompound();
		tag.setDouble(DataHelper.ENERGY, core.getExtendedStorage());
		tag.setDouble(DataHelper.CAPACITY, core.getExtendedCapacity());
		ArrayList values = getHookValues(core);
		if (values != null)
			tag.setLong(DataHelper.DIFF, ((Long) values.get(0) - (Long) values.get(20)) / 20);
		tag.setInteger("tier", core.tier.value);
		return tag;
	}

	private NBTTagCompound getReactorData(TileReactorCore reactor) {
		if (reactor == null)
			return null;

		//reactor.getWorld().setBlockToAir(reactor.getPos());
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("status", reactor.reactorState.value.name());
		tag.setDouble("temp", reactor.temperature.value);
		tag.setDouble(DataHelper.OUTPUT, reactor.generationRate.value);
		tag.setDouble(DataHelper.CONSUMPTION, reactor.fieldInputRate.value);
		tag.setDouble("diam", reactor.getCoreDiameter());
		tag.setDouble("saturation", reactor.saturation.value * 100D / reactor.maxSaturation.value);
		tag.setDouble("fuel", reactor.convertedFuel.value);
		tag.setDouble("shield", reactor.shieldCharge.value * 100D / reactor.maxShieldCharge.value);
		tag.setDouble("fuelMax", reactor.reactableFuel.value);
		tag.setDouble("fuelRate", reactor.fuelUseRate.value);
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
	public void registerItems(Register<Item> event) {
		ItemKitMain.register(ItemKitDraconicEvolution::new);
		ItemCardMain.register(ItemCardDraconicEvolution::new);
	}
}
