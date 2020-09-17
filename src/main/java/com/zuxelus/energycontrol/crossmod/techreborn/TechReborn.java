package com.zuxelus.energycontrol.crossmod.techreborn;

import java.lang.reflect.Field;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import ic2.api.item.IC2Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;
import reborncore.api.fuel.FluidPowerManager;
import techreborn.api.TechRebornAPI;
import techreborn.api.TechRebornBlocks;
import techreborn.api.TechRebornItems;
import techreborn.api.power.IEnergyInterfaceTile;
import techreborn.api.reactor.FusionReactorRecipe;
import techreborn.items.ItemParts;
import techreborn.powerSystem.PoweredItem;
import techreborn.tiles.TileDieselGenerator;
import techreborn.tiles.TileGasTurbine;
import techreborn.tiles.TileHeatGenerator;
import techreborn.tiles.TileSemifluidGenerator;
import techreborn.tiles.TileThermalGenerator;
import techreborn.tiles.fusionReactor.TileEntityFusionController;

public class TechReborn extends CrossTechReborn {

	@Override
	public ItemStack getEnergyCard(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IEnergyInterfaceTile) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY);
			ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
			return sensorLocationCard;
		}
		return null;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof IEnergyInterfaceTile) {
			NBTTagCompound tag = new NBTTagCompound();
			IEnergyInterfaceTile storage = (IEnergyInterfaceTile) te;
			tag.setInteger("type", 12);
			tag.setString("euType", "EU");
			tag.setDouble("storage", storage.getEnergy());
			tag.setDouble("maxStorage", storage.getMaxPower());
			return tag;
		}
		return null;
	}

	@Override
	public ItemStack getGeneratorCard(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileThermalGenerator || te instanceof TileDieselGenerator || te instanceof TileSemifluidGenerator || te instanceof TileHeatGenerator || te instanceof TileGasTurbine) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_GENERATOR);
			ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
			return sensorLocationCard;
		}
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", "EU");
			boolean active = false;
			if (te instanceof TileThermalGenerator) {
				TileThermalGenerator thermal = ((TileThermalGenerator) te);
				active = thermal.tank.getFluidAmount() > 0 && thermal.getMaxPower() - thermal.getEnergy() >= thermal.euTick;
				tag.setInteger("type", 1);
				tag.setBoolean("active", active);
				tag.setDouble("storage", thermal.getEnergy());
				tag.setDouble("maxStorage", thermal.getMaxPower());
				if (active)
					tag.setDouble("production", thermal.euTick);
				else
					tag.setInteger("production", 0);
				return tag;
			}
			if (te instanceof TileDieselGenerator) {
				TileDieselGenerator diesel = ((TileDieselGenerator) te);
				double output = 0;
				if (!diesel.tank.isEmpty() && diesel.tank.getFluidType() != null && FluidPowerManager.fluidPowerValues.containsKey(diesel.tank.getFluidType()))
					output = ((Double) FluidPowerManager.fluidPowerValues.get(diesel.tank.getFluidType())).doubleValue();
				active = output > 0;
				tag.setInteger("type", 1);
				tag.setBoolean("active", active);
				tag.setDouble("storage", diesel.getEnergy());
				tag.setDouble("maxStorage", diesel.getMaxPower());
				tag.setDouble("production", output);
				return tag;
			}
			if (te instanceof TileSemifluidGenerator) {
				TileSemifluidGenerator fluid = ((TileSemifluidGenerator) te);
				active = fluid.tank.getFluidAmount() > 0 && fluid.getMaxPower() - fluid.getEnergy() >= fluid.euTick;
				tag.setInteger("type", 1);
				tag.setBoolean("active", active);
				tag.setDouble("storage", fluid.getEnergy());
				tag.setDouble("maxStorage", fluid.getMaxPower());
				if (active)
					tag.setDouble("production", fluid.euTick);
				else
					tag.setInteger("production", 0);
				return tag;
			}
			if (te instanceof TileHeatGenerator) {
				TileHeatGenerator heat = ((TileHeatGenerator) te);
				double output = 0;
				if (heat.getWorldObj().getBlock(heat.xCoord + 1, heat.yCoord, heat.zCoord) == Blocks.lava
						|| heat.getWorldObj().getBlock(heat.xCoord, heat.yCoord, heat.zCoord + 1) == Blocks.lava
						|| heat.getWorldObj().getBlock(heat.xCoord, heat.yCoord, heat.zCoord - 1) == Blocks.lava
						|| heat.getWorldObj().getBlock(heat.xCoord - 1, heat.yCoord, heat.zCoord) == Blocks.lava
						|| heat.getWorldObj().getBlock(heat.xCoord, heat.yCoord - 1, heat.zCoord) == Blocks.lava)
					output = heat.euTick;
				active = output > 0;
				tag.setInteger("type", 1);
				tag.setBoolean("active", active);
				tag.setDouble("storage", heat.getEnergy());
				tag.setDouble("maxStorage", heat.getMaxPower());
				tag.setDouble("production", output);
				return tag;
			}
			if (te instanceof TileGasTurbine) {
				TileGasTurbine gas = ((TileGasTurbine) te);
				active = gas.tank.getFluidAmount() > 0 && gas.getMaxPower() - gas.getEnergy() >= gas.euTick;
				tag.setInteger("type", 1);
				tag.setBoolean("active", active);
				tag.setDouble("storage", gas.getEnergy());
				tag.setDouble("maxStorage", gas.getMaxPower());
				if (active)
					tag.setDouble("production", gas.euTick);
				else
					tag.setInteger("production", 0);
				return tag;
			}
			if (te instanceof TileEntityFusionController) {
				tag.setInteger("type", 6);
				Field field = TileEntityFusionController.class.getDeclaredField("currentRecipe");
				field.setAccessible(true);
				FusionReactorRecipe recipe = (FusionReactorRecipe) field.get(te);
				tag.setBoolean("active", recipe != null);
				tag.setDouble("storage", ((TileEntityFusionController) te).getEnergy());
				tag.setDouble("maxStorage", ((TileEntityFusionController) te).getMaxPower());
				if (recipe == null)
					tag.setDouble("production", 0);
				else
					tag.setDouble("production", recipe.getEuTick());
				if (((TileEntityFusionController) te).finalTickTime == 0)
					tag.setInteger("progress", 0);
				else
					tag.setInteger("progress", (int) (((TileEntityFusionController) te).crafingTickTime * 100.0D / ((TileEntityFusionController) te).finalTickTime));
				tag.setInteger("coilCount", ((TileEntityFusionController) te).coilStatus);
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}

	@Override
	public FluidTankInfo[] getAllTanks(TileEntity te) {
		/*if (te instanceof TileBaseFluidGenerator)
			return ((TileBaseFluidGenerator) te).tank.getTankProperties();*/
		return null;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		PoweredItem.setEnergy(PoweredItem.getMaxPower(stack), stack);
		return stack;
	}

	@Override
	public ItemStack getItemStack(String name) {
		switch (name) {
		case "glassfiber":
			//return ItemStandaloneCables.getCableByName("glassfiber");
		case "copper_cable":
			//return ItemStandaloneCables.getCableByName("insulatedcopper");
		case "tin_cable":
			//return ItemStandaloneCables.getCableByName("tin");
		case "gold_cable":
			//return ItemStandaloneCables.getCableByName("gold");
			return new ItemStack(IC2Items.getItem("copperCableItem").getItem(), 1, 10);
		case "carbon_plate":
			return new ItemStack(TechRebornItems.getItem("PLATES"), 1, 2);
		case "energy_crystal":
			return new ItemStack(TechRebornItems.getItem("ENERGY_CRYSTAL"));
		case "circuit":
			return new ItemStack(TechRebornItems.getItem("PARTS"), 1, 1);
		case "transmitter":
			return new ItemStack(TechRebornItems.getItem("FREQUENCY_TRANSMITTER"));
		case "frame":
			return new ItemStack(TechRebornBlocks.getBlock("MACHINE_FRAMES"));
		case "mv_transformer":
			return new ItemStack(TechRebornBlocks.getBlock("MV_TRANSFORMER"));
		case "coolant_simple":
			return ItemParts.getPartByName("heliumCoolantSimple");
		case "energy_storage":
			return new ItemStack(TechRebornItems.getItem("UPGRADES"), 1, 2);
		}
		return null;
	}
}
