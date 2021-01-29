package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.Loader;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.api.praescriptum.fuels.Fuel;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.PowerSystem.EnergySystem;
import reborncore.common.powerSystem.PoweredItemContainerProvider;
import reborncore.common.powerSystem.forge.ForgePowerItemManager;
import techreborn.api.TechRebornAPI;
import techreborn.api.reactor.FusionReactorRecipe;
import techreborn.tiles.fusionReactor.TileFusionControlComputer;
import techreborn.tiles.generator.TileCreativeSolarPanel;
import techreborn.tiles.generator.TileSolarPanel;
import techreborn.tiles.generator.TileSolidFuelGenerator;
import techreborn.tiles.generator.TileWaterMill;
import techreborn.tiles.generator.TileWindMill;
import techreborn.tiles.generator.fluid.TileFluidGenerator;

public class CrossTechReborn extends CrossModBase {

	@Override
	public ItemStack getItemStack(String name) {
		switch (name) {
		case "glassfiber":
			return new ItemStack(TechRebornAPI.getBlock("CABLE"), 1, 4);
		case "carbon_plate":
			return new ItemStack(TechRebornAPI.getItem("PLATES"), 1, 2);
		case "energy_crystal":
			return new ItemStack(TechRebornAPI.getItem("ENERGY_CRYSTAL"));
		case "circuit":
			return new ItemStack(TechRebornAPI.getItem("PARTS"), 1, 29);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		ForgePowerItemManager capEnergy = new ForgePowerItemManager(stack);
		capEnergy.setEnergyStored(capEnergy.getMaxEnergyStored());
		return stack;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack) {
		if (Loader.isModLoaded("ic2"))
			return null;
		return (ICapabilityProvider)new PoweredItemContainerProvider(stack);
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof IEnergyInterfaceTile) {
			NBTTagCompound tag = new NBTTagCompound();
			IEnergyInterfaceTile storage = (IEnergyInterfaceTile) te;
			tag.setInteger("type", 12);
			tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
			if (PowerSystem.getDisplayPower() == EnergySystem.EU) {
				tag.setDouble("storage", storage.getEnergy());
				tag.setDouble("maxStorage", storage.getMaxPower());
			} else {
				tag.setDouble("storage", storage.getEnergy() * RebornCoreConfig.euPerFU);
				tag.setDouble("maxStorage", storage.getMaxPower() * RebornCoreConfig.euPerFU);
			}
			return tag;
		}
		return null;
	}

	@Override
	public ItemStack getGeneratorCard(TileEntity te) {
		if (te instanceof TileFluidGenerator || te instanceof TileSolidFuelGenerator || te instanceof TileSolarPanel
				|| te instanceof TileCreativeSolarPanel || te instanceof TileWaterMill || te instanceof TileWindMill
				|| te instanceof TileFusionControlComputer) {
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
			boolean active = false;
			if (te instanceof TileFluidGenerator) {
				active = ((TileFluidGenerator) te).isActive();
				tag.setInteger("type", 1);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
				tag.setBoolean("active", active);
				tag.setDouble("storage", ((TileFluidGenerator) te).getEnergy());
				tag.setDouble("maxStorage", ((TileFluidGenerator) te).getMaxPower());
				if (active) {
					FluidStack stack = ((TileFluidGenerator) te).tank.getFluid();
					if (stack == null)
						tag.setInteger("production", 0);
					else {
						Fuel fuel = ((TileFluidGenerator) te).fuelHandler.findAndApply2(((TileFluidGenerator) te).tank.getFluid(), true);
						double output = fuel.getEnergyOutput() / fuel.getEnergyPerTick() / (Math.ceil(fuel.getEnergyOutput() / fuel.getEnergyPerTick()) + 1);
						tag.setDouble("production", fuel.getEnergyPerTick() * output);
					}
				} else
					tag.setInteger("production", 0);
				if (PowerSystem.getDisplayPower() != EnergySystem.EU) {
					tag.setDouble("storage", tag.getDouble("storage") * RebornCoreConfig.euPerFU);
					tag.setDouble("maxStorage", tag.getDouble("maxStorage") * RebornCoreConfig.euPerFU);
					tag.setDouble("production", tag.getDouble("production") * RebornCoreConfig.euPerFU);
				}
				return tag;
			}
			if (te instanceof TileSolidFuelGenerator) {
				active = ((TileSolidFuelGenerator) te).isActive();
				tag.setInteger("type", 5);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
				tag.setBoolean("active", active);
				tag.setDouble("storage", ((TileSolidFuelGenerator) te).getEnergy());
				tag.setDouble("maxStorage", ((TileSolidFuelGenerator) te).getMaxPower());
				if (active)
					tag.setDouble("production", ((TileSolidFuelGenerator) te).outputAmount);
				else
					tag.setInteger("production", 0);
				tag.setDouble("burnTime", ((TileSolidFuelGenerator) te).getBurnTime());
				if (PowerSystem.getDisplayPower() != EnergySystem.EU) {
					tag.setDouble("storage", tag.getDouble("storage") * RebornCoreConfig.euPerFU);
					tag.setDouble("maxStorage", tag.getDouble("maxStorage") * RebornCoreConfig.euPerFU);
					tag.setDouble("production", tag.getDouble("production") * RebornCoreConfig.euPerFU);
				}
				return tag;
			}
			if (te instanceof TileSolarPanel) {
				Field field = TileSolarPanel.class.getDeclaredField("powerToAdd");
				field.setAccessible(true);
				active = (int) field.get(te) != 0;
				tag.setInteger("type", 1);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
				tag.setBoolean("active", active);
				tag.setDouble("storage", ((TileSolarPanel) te).getEnergy());
				tag.setDouble("maxStorage", ((TileSolarPanel) te).getMaxPower());
				if (active)
					tag.setDouble("production", ((TileSolarPanel) te).getMaxOutput());
				else
					tag.setInteger("production", 0);
				if (PowerSystem.getDisplayPower() != EnergySystem.EU) {
					tag.setDouble("storage", tag.getDouble("storage") * RebornCoreConfig.euPerFU);
					tag.setDouble("maxStorage", tag.getDouble("maxStorage") * RebornCoreConfig.euPerFU);
					tag.setDouble("production", tag.getDouble("production") * RebornCoreConfig.euPerFU);
				}
				return tag;
			}
			if (te instanceof TileCreativeSolarPanel) {
				tag.setInteger("type", 1);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
				tag.setBoolean("active", true);
				tag.setDouble("storage", ((TileCreativeSolarPanel) te).getEnergy());
				tag.setDouble("maxStorage", ((TileCreativeSolarPanel) te).getMaxPower());
				tag.setDouble("production", ((TileCreativeSolarPanel) te).getMaxOutput());
				if (PowerSystem.getDisplayPower() != EnergySystem.EU) {
					tag.setDouble("storage", tag.getDouble("storage") * RebornCoreConfig.euPerFU);
					tag.setDouble("maxStorage", tag.getDouble("maxStorage") * RebornCoreConfig.euPerFU);
					tag.setDouble("production", tag.getDouble("production") * RebornCoreConfig.euPerFU);
				}
				return tag;
			}
			if (te instanceof TileWaterMill) {
				tag.setInteger("type", 1);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
				Field field = TileWaterMill.class.getDeclaredField("waterblocks");
				field.setAccessible(true);
				int water = (int) field.get(te);
				tag.setBoolean("active", water != 0);
				tag.setDouble("storage", ((TileWaterMill) te).getEnergy());
				tag.setDouble("maxStorage", ((TileWaterMill) te).getMaxPower());
				tag.setDouble("production", water * ((TileWaterMill) te).energyMultiplier);
				if (PowerSystem.getDisplayPower() != EnergySystem.EU) {
					tag.setDouble("storage", tag.getDouble("storage") * RebornCoreConfig.euPerFU);
					tag.setDouble("maxStorage", tag.getDouble("maxStorage") * RebornCoreConfig.euPerFU);
					tag.setDouble("production", tag.getDouble("production") * RebornCoreConfig.euPerFU);
				}
				return tag;
			}
			if (te instanceof TileWindMill) {
				tag.setInteger("type", 1);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
				active = te.getPos().getY() > 64;
				tag.setBoolean("active", active);
				tag.setDouble("storage", ((TileWindMill) te).getEnergy());
				tag.setDouble("maxStorage", ((TileWindMill) te).getMaxPower());
				if (!active)
					tag.setDouble("production", 0);
				else if (te.getWorld().isThundering())
					tag.setDouble("production", ((TileWindMill) te).baseEnergy * ((TileWindMill) te).thunderMultiplier);
				else
					tag.setDouble("production", ((TileWindMill) te).baseEnergy);
				if (PowerSystem.getDisplayPower() != EnergySystem.EU) {
					tag.setDouble("storage", tag.getDouble("storage") * RebornCoreConfig.euPerFU);
					tag.setDouble("maxStorage", tag.getDouble("maxStorage") * RebornCoreConfig.euPerFU);
					tag.setDouble("production", tag.getDouble("production") * RebornCoreConfig.euPerFU);
				}
				return tag;
			}
			if (te instanceof TileFusionControlComputer) {
				tag.setInteger("type", 6);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
				tag.setBoolean("active", ((TileFusionControlComputer) te).getState() == 2);
				switch (((TileFusionControlComputer) te).getState()) {
				case -1:
					tag.setString("status", "");
					break;
				case 0:
					tag.setString("status", "No recipe");
					break;
				case 1:
					tag.setString("status", "Charging");
					break;
				case 2:
					tag.setString("status", "Crafting");
					break;
				}
				tag.setDouble("storage", ((TileFusionControlComputer) te).getEnergy());
				tag.setDouble("maxStorage", ((TileFusionControlComputer) te).getMaxPower());
				Field field = TileFusionControlComputer.class.getDeclaredField("currentRecipe");
				field.setAccessible(true);
				FusionReactorRecipe recipe = (FusionReactorRecipe) field.get(te);
				if (recipe == null)
					tag.setDouble("production", 0);
				else
					tag.setDouble("production", recipe.getEuTick() * ((TileFusionControlComputer) te).getPowerMultiplier());
				if (((TileFusionControlComputer) te).finalTickTime == 0)
					tag.setInteger("progress", 0);
				else
					tag.setInteger("progress", (int) (((TileFusionControlComputer) te).crafingTickTime * 100.0D / ((TileFusionControlComputer) te).finalTickTime));
				tag.setDouble("multiplier", ((TileFusionControlComputer) te).getPowerMultiplier());
				tag.setInteger("coilCount", ((TileFusionControlComputer) te).coilCount);
				if (PowerSystem.getDisplayPower() != EnergySystem.EU) {
					tag.setDouble("storage", tag.getDouble("storage") * RebornCoreConfig.euPerFU);
					tag.setDouble("maxStorage", tag.getDouble("maxStorage") * RebornCoreConfig.euPerFU);
					tag.setDouble("production", tag.getDouble("production") * RebornCoreConfig.euPerFU);
				}
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		if (te instanceof TileFluidGenerator) {
			List<IFluidTank> result = new ArrayList<>();
			result.add(((TileFluidGenerator) te).tank);
			return result;
		}
		return null;
	}
}
