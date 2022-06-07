package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.hooks.IC2ClassicHooks;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemDigitalThermometer;
import com.zuxelus.energycontrol.items.cards.ItemCardIC2;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitIC2;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import ic2.api.classic.reactor.IChamberReactor;
import ic2.api.classic.tile.machine.IEUStorage;
import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.ICustomDamageItem;
import ic2.api.reactor.IReactor;
import ic2.api.tile.IEnergyStorage;
import ic2.core.IC2;
import ic2.core.block.base.tile.*;
import ic2.core.block.generator.tile.*;
import ic2.core.block.machine.low.TileEntityMachineTank;
import ic2.core.block.personal.tile.TileEntityPersonalTank;
import ic2.core.item.reactor.ItemReactorUraniumRod;
import ic2.core.item.reactor.base.ItemHeatVentBase;
import ic2.core.item.reactor.base.ItemHeatVentBase.VentProperty;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class CrossIC2Classic extends CrossModBase {

	@Override
	public ItemStack getItemStack(String name) {
		switch (name) {
		case "transformer":
			return IC2Items.getItem("upgrade", "transformer");
		case "energy_storage":
			return IC2Items.getItem("upgrade", "energy_storage");
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		ElectricItem.manager.charge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		return stack;
	}

	@Override
	public boolean isWrench(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemToolWrench;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DataHelper.EUTYPE, "EU");
		if (te instanceof IEnergyStorage) {
			tag.setDouble(DataHelper.ENERGY, ((IEnergyStorage) te).getStored());
			tag.setDouble(DataHelper.CAPACITY, ((IEnergyStorage) te).getCapacity());
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileEntityNuclearSteamReactor) {
			result.add(new FluidInfo(((TileEntityNuclearSteamReactor) te).getWaterTank()));
			result.add(new FluidInfo(((TileEntityNuclearSteamReactor) te).getSteamTank()));
		}
		if (te instanceof TileEntityMachineTank)
			result.add(new FluidInfo(((TileEntityMachineTank) te).tank));
		if (te instanceof TileEntityPersonalTank)
			result.add(new FluidInfo(((TileEntityPersonalTank) te).tank));
		if (te instanceof TileEntityLiquidFuelGenerator)
			result.add(new FluidInfo(((TileEntityLiquidFuelGenerator) te).tank));
		if (result.size() == 0)
			return null;
		return result;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			if (te instanceof IEUStorage) {
				tag.setDouble(DataHelper.ENERGY, ((IEUStorage) te).getStoredEU());
				tag.setDouble(DataHelper.CAPACITY, ((IEUStorage) te).getMaxEU());
				if (te instanceof TileEntityFuelGeneratorBase) {
					tag.setBoolean(DataHelper.ACTIVE, ((TileEntityFuelGeneratorBase) te).fuel > 0);
					tag.setDouble(DataHelper.OUTPUT, ((TileEntityFuelGeneratorBase) te).fuel > 0 ? ((TileEntityGeneratorBase) te).getOutput() : 0);
					tag.setInteger(DataHelper.FUEL, ((TileEntityFuelGeneratorBase) te).fuel);
					if (te instanceof TileEntityWaterGenerator) {
						tag.setBoolean(DataHelper.ACTIVE, ((TileEntityFuelGeneratorBase) te).fuel > 0 || ((TileEntityWaterGenerator) te).water > 0);
						tag.setDouble(DataHelper.OUTPUT, ((TileEntityFuelGeneratorBase) te).fuel > 0 ? ((TileEntityGeneratorBase) te).getOutput() : ((TileEntityWaterGenerator) te).water * IC2.config.getInt("energyGeneratorWater") / 10000D);
					}
				}
				if (te instanceof TileEntityElectricBlock) {
					ArrayList values = getHookValues(te);
					if (values != null)
						tag.setDouble(DataHelper.DIFF, ((Integer) values.get(0) - (Integer) values.get(20)) / 20.0D);
				}
				if (te instanceof TileEntityWindGenerator) {
					tag.setDouble(DataHelper.OUTPUT, ((TileEntityWindGenerator) te).subProduction * 4);
				}
				if (te instanceof TileEntityBasicSteamTurbine) {
					FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityBasicSteamTurbine) te).tank);
				}
				return tag;
			}
			if (te instanceof TileEntitySolarPanel) {
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntitySolarPanel) te).getActive());
				tag.setDouble(DataHelper.OUTPUT, ((TileEntitySolarPanel) te).getActive() ? ((TileEntitySolarPanel) te).getOutput() : 0);
				return tag;
			}
			if (te instanceof TileEntityFuelBoiler) {
				int realHeat = ((TileEntityFuelBoiler) te).heat / 30;
				tag.setInteger(DataHelper.HEAT, realHeat);
				tag.setInteger(DataHelper.MAXHEAT, 800);
				tag.setDouble(DataHelper.CONSUMPTION, realHeat >= 100 ? realHeat * 0.32F / 160.0F : 0);
				tag.setDouble(DataHelper.OUTPUTMB, realHeat >= 100 ? (int) (realHeat * 0.32F) : 0);
				IFluidTankProperties[] tanks = ((TileEntityFuelBoiler) te).getTankProperties();
				FluidInfo.addTank(DataHelper.TANK, tag, tanks[0].getContents());
				FluidInfo.addTank(DataHelper.TANK2, tag, tanks[1].getContents());
				return tag;
			}
			if (te instanceof TileEntityNuclearReactorElectric)
				return getReactorData((TileEntityNuclearReactorElectric) te);
			if (te instanceof TileEntityReactorChamberElectric) {
				IReactor reactor = ((TileEntityReactorChamberElectric) te).getReactorInstance();
				if (reactor instanceof TileEntityNuclearReactorElectric)
					return getReactorData((TileEntityNuclearReactorElectric) reactor);
			}
			if (te instanceof TileEntityNuclearSteamReactor)
				return getReactorData((TileEntityNuclearSteamReactor) te);
			if (te instanceof TileEntitySteamReactorChamber) {
				IReactor reactor = ((TileEntitySteamReactorChamber) te).getReactorInstance();
				if (reactor instanceof TileEntityNuclearSteamReactor)
					return getReactorData((TileEntityNuclearSteamReactor) reactor);
			}
		} catch (Throwable t) { }
		return null;
	}

	private NBTTagCompound getReactorData(TileEntityNuclearReactorElectric reactor) {
		if (reactor == null)
			return null;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(DataHelper.HEAT, reactor.getHeat());
		tag.setInteger(DataHelper.MAXHEAT, reactor.getMaxHeat());
		tag.setBoolean(DataHelper.ACTIVE, reactor.produceEnergy());
		tag.setInteger(DataHelper.OUTPUT, (int) Math.round(reactor.getReactorEUEnergyOutput()));
		IChamberReactor chamber = (IChamberReactor) reactor;
		int size = chamber.getReactorSize();
		int dmgLeft = 0;
		for (int y = 0; y < 6; y++)
			for (int x = 0; x < size; x++) {
				ItemStack stack = chamber.getItemAt(x, y);
				if (!stack.isEmpty())
					dmgLeft = Math.max(dmgLeft, getNuclearCellTimeLeft(stack));
			}
		tag.setInteger("timeLeft", dmgLeft * reactor.getTickRate() / 20);
		return tag;
	}

	private NBTTagCompound getReactorData(TileEntityNuclearSteamReactor reactor) {
		if (reactor == null)
			return null;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(DataHelper.HEAT, reactor.getHeat());
		tag.setInteger(DataHelper.MAXHEAT, reactor.getMaxHeat());
		tag.setBoolean(DataHelper.ACTIVE, reactor.produceEnergy());
		tag.setDouble(DataHelper.CONSUMPTION, reactor.output * 3.2D / 160.0D);
		tag.setDouble(DataHelper.OUTPUTMB, reactor.output * 3.2D);
		IChamberReactor chamber = (IChamberReactor) reactor;
		int size = chamber.getReactorSize();
		int dmgLeft = 0;
		for (int y = 0; y < 6; y++)
			for (int x = 0; x < size; x++) {
				ItemStack stack = chamber.getItemAt(x, y);
				if (!stack.isEmpty()) {
					dmgLeft = Math.max(dmgLeft, getNuclearCellTimeLeft(stack));
					/*if (stack.getItem() instanceof ItemHeatVentBase) {
						ItemHeatVentBase vent = (ItemHeatVentBase) stack.getItem(); 
					    VentProperty prop = vent.getProperty(stack);
					    if (prop != null) {
					    	int type = prop.getType();
					    	
					    }
					}*/
				}
			}
		tag.setInteger("timeLeft", dmgLeft);
		FluidInfo.addTank(DataHelper.TANK, tag, reactor.getWaterTank());
		FluidInfo.addTank(DataHelper.TANK2, tag, reactor.getSteamTank());
		return tag;
	}

	private int getNuclearCellTimeLeft(ItemStack stack) {
		if (stack.isEmpty())
			return 0;
		
		Item item = stack.getItem();
		if (item instanceof ItemReactorUraniumRod)
			return ((ICustomDamageItem) item).getMaxCustomDamage(stack) - ((ICustomDamageItem) item).getCustomDamage(stack);
		return 0;
	}

	/*@Override
	public ItemStack getGeneratorCard(TileEntity te) {
		if (te instanceof TileEntitySolarPanel || te instanceof TileEntityGeneratorBase) {
			ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_GENERATOR);
			ItemStackHelper.setCoordinates(card, te.getPos());
			return card;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			boolean active;
			tag.setString("euType", "EU");
			if (te instanceof TileEntitySolarPanel) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntitySolarPanel) te).getOfferedEnergy());
				tag.setDouble("maxStorage", ((TileEntitySolarPanel) te).getOutput());
				active = ((TileEntitySolarPanel) te).getActive();
				tag.setBoolean("active", active);
				if (active)
					tag.setDouble("production", ((TileEntitySolarPanel) te).getOutput());
				else
					tag.setDouble("production", 0);
				return tag;
			}
			if (te instanceof TileEntityGeneratorBase) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntityGeneratorBase) te).getStoredEU());
				tag.setDouble("maxStorage", ((TileEntityGeneratorBase) te).getMaxEU());
				active = ((TileEntityGeneratorBase) te).getActive();
				tag.setBoolean("active", active);
				if (active)
					tag.setDouble("production", ((TileEntityGeneratorBase) te).getOutput());
				else
					tag.setDouble("production", 0);
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}

	@Override
	public ItemStack getReactorCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityNuclearReactorElectric || te instanceof TileEntityReactorChamberElectric || te instanceof TileEntityNuclearSteamReactor) {
			BlockPos position = IC2ReactorHelper.getTargetCoordinates(world, pos);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_REACTOR);
				ItemStackHelper.setCoordinates(sensorLocationCard, position);
				return sensorLocationCard;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound getReactorData(TileEntity te) {
		if (!(te instanceof IChamberReactor))
			return null;
		IReactor reactor = (IReactor) te;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("heat", reactor.getHeat());
		tag.setInteger("maxHeat", reactor.getMaxHeat());
		tag.setBoolean("reactorPoweredB", reactor.produceEnergy());
		tag.setInteger("output", (int) Math.round(reactor.getReactorEUEnergyOutput()));
		tag.setBoolean("isSteam", isSteamReactor(te));

		IChamberReactor chamber = (IChamberReactor) reactor;
		int size = chamber.getReactorSize();
		int dmgLeft = 0;
		for (int y = 0; y < 6; y++)
			for (int x = 0; x < size; x++) {
				ItemStack stack = chamber.getItemAt(x, y);
				if (!stack.isEmpty())
					dmgLeft = Math.max(dmgLeft, IC2ReactorHelper.getNuclearCellTimeLeft(stack));
			}

		int timeLeft;
		//Classic has a Higher Tick rate for Steam generation but damage tick rate is still the same...
		if (isSteamReactor(te)) {
			timeLeft = dmgLeft;
		} else
			timeLeft = dmgLeft * reactor.getTickRate() / 20;
		tag.setInteger("timeLeft", timeLeft);

		return tag;
	}*/

	@Override
	public int getHeat(World world, BlockPos pos) {
		IReactor reactor = IC2ReactorHelper.getReactorAround(world, pos);
		if (reactor != null)
			return reactor.getHeat();
		reactor = IC2ReactorHelper.getReactor3x3(world, pos);
		if (reactor != null)
			return reactor.getHeat();
		return -1;
	}

	@Override
	public ArrayList getHookValues(TileEntity te) {
		ArrayList values = IC2ClassicHooks.map.get(te);
		if (values == null)
			IC2ClassicHooks.map.put(te, null);
		return values;
	}

	@Override
	public void registerItems(Register<Item> event) {
		ModItems.itemThermometerDigital = ModItems.register(event, new ItemDigitalThermometer(), "thermometer_digital");
		ItemKitMain.register(ItemKitIC2::new);
		ItemCardMain.register(ItemCardIC2::new);
	}

	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModItems.registerItemModel(ModItems.itemThermometerDigital, 0, "thermometer_digital");
	}
}
