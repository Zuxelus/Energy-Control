package com.zuxelus.energycontrol.crossmod.techreborn;

import java.lang.reflect.Field;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Loader;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.PowerSystem.EnergySystem;
import reborncore.common.powerSystem.PoweredItem;
import reborncore.common.powerSystem.PoweredItemContainerProvider;
import techreborn.api.TechRebornAPI;
import techreborn.api.reactor.FusionReactorRecipe;
import techreborn.items.ItemParts;
import techreborn.parts.powerCables.ItemStandaloneCables;
import techreborn.tiles.fusionReactor.TileEntityFusionController;
import techreborn.tiles.generator.TileBaseFluidGenerator;
import techreborn.tiles.generator.TileCreativePanel;
import techreborn.tiles.generator.TileGenerator;
import techreborn.tiles.generator.TileSolarPanel;
import techreborn.tiles.generator.TileWaterMill;
import techreborn.tiles.generator.TileWindMill;

public class TechReborn extends CrossTechReborn {

	@Override
	public ItemStack getEnergyCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IEnergyInterfaceTile) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
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
	public ItemStack getGeneratorCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileBaseFluidGenerator || te instanceof TileGenerator || te instanceof TileSolarPanel
				|| te instanceof TileCreativePanel || te instanceof TileWaterMill || te instanceof TileWindMill
				|| te instanceof TileEntityFusionController) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_GENERATOR);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			boolean active = false;
			if (te instanceof TileBaseFluidGenerator) {
				active = ((TileBaseFluidGenerator) te).isActive();
				tag.setInteger("type", 1);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
				tag.setBoolean("active", active);
				tag.setDouble("storage", ((TileBaseFluidGenerator) te).getEnergy());
				tag.setDouble("maxStorage", ((TileBaseFluidGenerator) te).getMaxPower());
				if (active) {
					FluidStack stack = ((TileBaseFluidGenerator) te).tank.getFluid();
					if (stack == null)
						tag.setDouble("production", 0);
					else {
						Field field = TileBaseFluidGenerator.class.getDeclaredField("euTick");
						field.setAccessible(true);
						tag.setDouble("production", (double) (int) field.get(te));
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
			if (te instanceof TileGenerator) {
				active = ((TileGenerator) te).isActive();
				tag.setInteger("type", 5);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
				tag.setBoolean("active", active);
				tag.setDouble("storage", ((TileGenerator) te).getEnergy());
				tag.setDouble("maxStorage", ((TileGenerator) te).getMaxPower());
				if (active)
					tag.setDouble("production", ((TileGenerator) te).outputAmount);
				else
					tag.setInteger("production", 0);
				tag.setDouble("burnTime", ((TileGenerator) te).getBurnTime());
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
				int power = (int) field.get(te);
				active = power != 0;
				tag.setInteger("type", 1);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
				tag.setBoolean("active", active);
				tag.setDouble("storage", ((TileSolarPanel) te).getEnergy());
				tag.setDouble("maxStorage", ((TileSolarPanel) te).getMaxPower());
				if (active)
					tag.setDouble("production", power);
				else
					tag.setInteger("production", 0);
				if (PowerSystem.getDisplayPower() != EnergySystem.EU) {
					tag.setDouble("storage", tag.getDouble("storage") * RebornCoreConfig.euPerFU);
					tag.setDouble("maxStorage", tag.getDouble("maxStorage") * RebornCoreConfig.euPerFU);
					tag.setDouble("production", tag.getDouble("production") * RebornCoreConfig.euPerFU);
				}
				return tag;
			}
			if (te instanceof TileCreativePanel) {
				tag.setInteger("type", 1);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
				tag.setBoolean("active", true);
				tag.setDouble("storage", ((TileCreativePanel) te).getEnergy());
				tag.setDouble("maxStorage", ((TileCreativePanel) te).getMaxPower());
				tag.setDouble("production", ((TileCreativePanel) te).getMaxOutput());
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
				tag.setDouble("production", water);
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
				Field field = TileWindMill.class.getDeclaredField("basePower");
				field.setAccessible(true);
				int basePower = (int) field.get(te);
				if (!active)
					tag.setDouble("production", 0);
				else if (te.getWorld().isThundering())
					tag.setDouble("production", basePower * 1.25D);
				else
					tag.setDouble("production", basePower);
				if (PowerSystem.getDisplayPower() != EnergySystem.EU) {
					tag.setDouble("storage", tag.getDouble("storage") * RebornCoreConfig.euPerFU);
					tag.setDouble("maxStorage", tag.getDouble("maxStorage") * RebornCoreConfig.euPerFU);
					tag.setDouble("production", tag.getDouble("production") * RebornCoreConfig.euPerFU);
				}
				return tag;
			}
			if (te instanceof TileEntityFusionController) {
				tag.setInteger("type", 6);
				tag.setString("euType", PowerSystem.getDisplayPower().abbreviation);
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
				tag.setInteger("coilCount", ((TileEntityFusionController) te).getCoilStatus());
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
	public IFluidTankProperties[] getAllTanks(TileEntity te) {
		if (te instanceof TileBaseFluidGenerator)
			return ((TileBaseFluidGenerator) te).tank.getTankProperties();
		return null;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		PoweredItem.setEnergy(PoweredItem.getMaxPower(stack), stack);
		return stack;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack) {
		if (Loader.isModLoaded("IC2"))
			return null;
		return (ICapabilityProvider)new PoweredItemContainerProvider(stack);
	}

	@Override
	public ItemStack getItemStack(String name) {
		switch (name) {
		case "glassfiber":
			return ItemStandaloneCables.getCableByName("glassfiber");
		case "copper_cable":
			return ItemStandaloneCables.getCableByName("insulatedcopper");
		case "tin_cable":
			return ItemStandaloneCables.getCableByName("tin");
		case "gold_cable":
			return ItemStandaloneCables.getCableByName("gold");
		case "carbon_plate":
			return new ItemStack(TechRebornAPI.getItem("PLATES"), 1, 2);
		case "energy_crystal":
			return new ItemStack(TechRebornAPI.getItem("ENERGY_CRYSTAL"));
		case "circuit":
			return new ItemStack(TechRebornAPI.getItem("PARTS"), 1, 1);
		case "transmitter":
			return new ItemStack(TechRebornAPI.getItem("FREQUENCY_TRANSMITTER"));
		case "frame":
			return new ItemStack(TechRebornAPI.getBlock("MACHINE_FRAMES"));
		case "mv_transformer":
			return new ItemStack(TechRebornAPI.getBlock("MV_TRANSFORMER"));
		case "coolant_simple":
			return ItemParts.getPartByName("coolant_simple");
		case "energy_storage":
			return new ItemStack(TechRebornAPI.getItem("UPGRADES"), 1, 2);
		}
		return null;
	}
}
