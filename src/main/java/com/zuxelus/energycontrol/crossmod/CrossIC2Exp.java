package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemAFB;
import com.zuxelus.energycontrol.items.ItemAFSUUpgradeKit;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.FluidInfo;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.ICustomDamageItem;
import ic2.api.item.IElectricItem;
import ic2.api.reactor.IReactor;
import ic2.api.tile.IEnergyStorage;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.generator.tileentity.*;
import ic2.core.block.heatgenerator.tileentity.TileEntityElectricHeatGenerator;
import ic2.core.block.kineticgenerator.tileentity.*;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.block.reactor.tileentity.*;
import ic2.core.item.reactor.ItemReactorLithiumCell;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CrossIC2Exp extends CrossModBase {

	@Override
	public Item getItem(String name) {
		switch (name) {
		case "afb":
			return new ItemAFB();
		case "afsu_upgrade_kit":
			return new ItemAFSUUpgradeKit();
		case "seed":
			return IC2Items.getItem("crop_seed_bag").getItem();
		default:
			return null;
		}
	}

	@Override
	public ItemStack getItemStack(String name) {
		switch (name) {
		case "transformer":
			return IC2Items.getItem("upgrade", "transformer");
		case "energy_storage":
			return IC2Items.getItem("upgrade", "energy_storage");
		case "machine":
			return IC2Items.getItem("resource", "machine");
		case "mfsu":
			return IC2Items.getItem("te","mfsu");
		case "circuit":
			return IC2Items.getItem("crafting","circuit");
		case "iridium":
			return IC2Items.getItem("crafting","iridium");
		case "cable_glass":
			return IC2Items.getItem("cable","type:glass,insulation:0");
		case "matter_cell":
			return IC2Items.getItem("fluid_cell","ic2uu_matter");
		
		}
		return null;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		ElectricItem.manager.charge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		return stack;
	}

	@Override
	public boolean isWrench(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemToolWrench;
	}

	@Override
	public boolean isElectricItem(ItemStack stack) {
		return stack != null && stack.getItem() instanceof IElectricItem;
	}

	@Override
	public double dischargeItem(ItemStack stack, double needed) {
		IElectricItem ielectricitem = (IElectricItem) stack.getItem();
		if (ielectricitem.canProvideEnergy(stack))
			return ElectricItem.manager.discharge(stack, needed, 1, false, false, false);
		return 0;
	}
	
	/*@Override
	public void postModEvent(TileEntity te, String name) {
		if (name.equals("EnergyTileLoadEvent"))
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(te));
		if (name.equals("EnergyTileUnloadEvent"))
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
	}*/

	@Override
	public int getNuclearCellTimeLeft(ItemStack stack) {
		if (stack == null)
			return 0;
		Item item = stack.getItem();
		if (item instanceof ItemReactorUranium || item instanceof ItemReactorLithiumCell)
			return ((ICustomDamageItem)item).getMaxCustomDamage(stack) - ((ICustomDamageItem)item).getCustomDamage(stack);
		// Coaxium Mod
		if (item.getClass().getName().equals("com.sm.FirstMod.items.ItemCoaxiumRod") || item.getClass().getName().equals("com.sm.FirstMod.items.ItemCesiumRod"))
			return stack.getMaxDamage() - getCoaxiumDamage(stack);
		return 0;
	}

	private int getCoaxiumDamage(ItemStack stack) {
		if (!stack.hasTagCompound())
			return 0;
		return stack.getTagCompound().getInteger("fuelRodDamage");
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof IEnergyStorage) {
			NBTTagCompound tag = new NBTTagCompound();
			IEnergyStorage storage = (IEnergyStorage) te;
			tag.setString("euType", "EU");
			tag.setDouble("storage", storage.getStored());
			tag.setDouble("maxStorage", storage.getCapacity());
			return tag;
		}
		return null;
	}

	@Override
	public ItemStack getGeneratorCard(TileEntity te) {
		if (te instanceof TileEntityBaseGenerator /*|| te instanceof TileEntityConversionGenerator*/) { // TODO
			ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_GENERATOR);
			ItemStackHelper.setCoordinates(card, te.getPos());
			return card;
		}
		if (te instanceof TileEntityElectricKineticGenerator || te instanceof TileEntityManualKineticGenerator
				|| te instanceof TileEntitySteamKineticGenerator || te instanceof TileEntityStirlingKineticGenerator
				|| te instanceof TileEntityWaterKineticGenerator || te instanceof TileEntityWindKineticGenerator) {
			ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_GENERATOR_KINETIC);
			ItemStackHelper.setCoordinates(card, te.getPos());
			return card;
		}
		if (te instanceof TileEntityHeatSourceInventory) {
			ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_GENERATOR_HEAT);
			ItemStackHelper.setCoordinates(card, te.getPos());
			return card;
		}
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			boolean active = isActive(te);
			tag.setBoolean("active", active);
			tag.setString("euType", "EU");
			if (te instanceof TileEntityBaseGenerator) {
				tag.setInteger("type", 1);
				Energy energy = ((TileEntityBaseGenerator) te).getComponent(Energy.class);
				tag.setDouble("storage", energy.getEnergy());
				tag.setDouble("maxStorage", energy.getCapacity());
				if (te instanceof TileEntitySolarGenerator) {
					float light = ((TileEntitySolarGenerator)te).skyLight;
					active = light > 0 && energy.getEnergy() < energy.getCapacity();
					tag.setBoolean("active", active);
					if (active)
						tag.setDouble("production", light);
					else
						tag.setDouble("production", 0);
					return tag;
				}
				if (te instanceof TileEntityRTGenerator) {
					tag.setInteger("type", 4);
					int counter = 0;
					for (int i = 0; i < ((TileEntityRTGenerator) te).fuelSlot.size(); i++)
						if (!((TileEntityRTGenerator) te).fuelSlot.isEmpty(i))
							counter++;
					tag.setInteger("items", counter);
					if (counter == 0 || energy.getEnergy() >= energy.getCapacity()) {
						tag.setBoolean("active", false);
						tag.setDouble("production", 0);
						return tag;
					}
					tag.setBoolean("active", true);
					Field field = TileEntityRTGenerator.class.getDeclaredField("efficiency");
					field.setAccessible(true);
					tag.setDouble("multiplier", (float) field.get(te));
					tag.setDouble("production", Math.pow(2.0D, (counter - 1)) * (float) field.get(te));
					return tag;
				}
				if (te instanceof TileEntityWaterGenerator) {
					active = ((TileEntityWaterGenerator)te).water > 0 || ((TileEntityWaterGenerator)te).fuel > 0;
					tag.setBoolean("active", active);
					if (((TileEntityWaterGenerator) te).fuel <= 0) {
						Field field = TileEntityWaterGenerator.class.getDeclaredField("energyMultiplier");
						field.setAccessible(true);
						tag.setDouble("production", (Double) field.get(te) * ((TileEntityWaterGenerator) te).water / 100);
						return tag;
					}
				}
				if (active) {
					Field field = TileEntityBaseGenerator.class.getDeclaredField("production");
					field.setAccessible(true);
					tag.setDouble("production", (Double) field.get(te));
				} else
					tag.setDouble("production", 0);
				return tag;
			}
			/*if (te instanceof TileEntityConversionGenerator) { // TODO
				if (active) {
					Field field = TileEntityConversionGenerator.class.getDeclaredField("lastProduction");
					field.setAccessible(true);
					tag.setDouble("production", (Double) field.get(te));
				} else
					tag.setDouble("production", 0);
				if (te instanceof TileEntityStirlingGenerator) {
					tag.setInteger("type", 2);
					Field field = TileEntityStirlingGenerator.class.getDeclaredField("productionpeerheat");
					field.setAccessible(true);
					tag.setDouble("multiplier", (Double) field.get(te));
				}
				if (te instanceof TileEntityKineticGenerator) {
					tag.setInteger("type", 2);
					Field field = TileEntityKineticGenerator.class.getDeclaredField("euPerKu");
					field.setAccessible(true);
					tag.setDouble("multiplier", (Double) field.get(te));
				}
				return tag;
			}*/
		} catch (Throwable t) { }
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorKineticData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			if (te instanceof TileEntityManualKineticGenerator) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntityManualKineticGenerator)te).currentKU);
				tag.setDouble("maxStorage", ((TileEntityManualKineticGenerator)te).maxKU);
				return tag;
			}
			Boolean active = ((TileEntityBlock) te).getActive();
			if (te instanceof TileEntityWindKineticGenerator) {
				TileEntityWindKineticGenerator entity = ((TileEntityWindKineticGenerator) te);
				tag.setInteger("type", 5);
				tag.setDouble("output", entity.getKuOutput());
				Field field = TileEntityWindKineticGenerator.class.getDeclaredField("windStrength");
				field.setAccessible(true);
				tag.setDouble("wind", (Double) field.get(te));
				tag.setDouble("multiplier", entity.getEfficiency() * entity.outputModifier);
				tag.setInteger("height", entity.getPos().getY());
				 if (entity.rotorSlot.isEmpty())
					 tag.setInteger("health", -1);
				 else
					 tag.setDouble("health", 100.0F - entity.rotorSlot.get().getItemDamage() * 100.0F / entity.rotorSlot.get().getMaxDamage());
				return tag;
			}
			if (te instanceof TileEntityWaterKineticGenerator) {
				TileEntityWaterKineticGenerator entity = ((TileEntityWaterKineticGenerator) te);
				tag.setInteger("type", 6);
				tag.setDouble("output", entity.getKuOutput());
				Field field = TileEntityWaterKineticGenerator.class.getDeclaredField("waterFlow");
				field.setAccessible(true);
				tag.setDouble("wind", (Integer) field.get(te));
				field = TileEntityWaterKineticGenerator.class.getDeclaredField("outputModifier");
				field.setAccessible(true);
				tag.setDouble("multiplier", (double) (Float) field.get(te));
				tag.setInteger("height", entity.getPos().getY());
				 if (entity.rotorSlot.isEmpty())
					 tag.setInteger("health", -1);
				 else
					 tag.setDouble("health", 100.0F - entity.rotorSlot.get().getItemDamage() * 100.0F / entity.rotorSlot.get().getMaxDamage());
				return tag;
			}
			if (te instanceof TileEntityStirlingKineticGenerator) {
				//TODO
			}
			if (te instanceof TileEntitySteamKineticGenerator) {
				TileEntitySteamKineticGenerator entity = ((TileEntitySteamKineticGenerator) te);
				tag.setInteger("type", 7);
				if (!entity.hasTurbine())
					tag.setString("status", "ic2.SteamKineticGenerator.gui.error.noturbine");
				else if (entity.isTurbineBlockedByWater())
					tag.setString("status", "ic2.SteamKineticGenerator.gui.error.filledupwithwater");
				else if (entity.getActive())
					tag.setString("status", "ic2.SteamKineticGenerator.gui.aktive");
				else
					tag.setString("status", "ic2.SteamKineticGenerator.gui.waiting");
				tag.setDouble("output", entity.getKUoutput());
				Field field = TileEntitySteamKineticGenerator.class.getDeclaredField("outputModifier");
				field.setAccessible(true);
				tag.setDouble("multiplier", (double) (Float) field.get(te) * (1.0F - ((float) entity.getDistilledWaterTank().getFluidAmount()) / entity.getDistilledWaterTank().getCapacity()));
				field = TileEntitySteamKineticGenerator.class.getDeclaredField("condensationProgress");
				field.setAccessible(true);
				tag.setInteger("condProgress", (int) field.get(te)); 
				return tag;
			}
		} catch (Throwable ignored) {
		}
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorHeatData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			boolean active = ((TileEntityBlock)te).getActive();
			tag.setBoolean("active", active);
			if (te instanceof TileEntityHeatSourceInventory) {
				tag.setInteger("type", 1);
				if (active)
					tag.setInteger("output", ((TileEntityHeatSourceInventory)te).gettransmitHeat());
				else
					tag.setInteger("output", 0);
				if (te instanceof TileEntityElectricHeatGenerator) {
					Energy energy = ((TileEntityHeatSourceInventory) te).getComponent(Energy.class);
					tag.setDouble("storage", energy.getEnergy());
					tag.setDouble("maxStorage", energy.getCapacity());
					int count = 0;
					for (ItemStack stack : ((TileEntityElectricHeatGenerator) te).coilSlot)
						if (stack != null)
							count++;
					tag.setInteger("coils", count);
				}
				if (te instanceof TileEntityLiquidHeatExchanger) {
					FluidTank tank = ((TileEntityLiquidHeatExchanger) te).inputTank;
					tag.setDouble("storage", tank.getFluidAmount());
					tag.setDouble("maxStorage", tank.getCapacity());
					int count = 0;
					for (ItemStack stack : ((TileEntityLiquidHeatExchanger) te).heatexchangerslots)
						if (stack != null)
							count++;
					tag.setInteger("coils", count);
				}
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}

	private boolean isActive(TileEntity te) {
		if (te instanceof TileEntityGeoGenerator /*|| te instanceof TileEntityConversionGenerator*/ || te instanceof TileEntitySolarGenerator) // TODO
			return ((TileEntityBlock)te).getActive();
		if (te instanceof TileEntityBaseGenerator)
			return ((TileEntityBaseGenerator)te).isConverting();
		return false;
	}

	@Override
	public ItemStack getReactorCard(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockTileEntity))
			return null;

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityNuclearReactorElectric || te instanceof TileEntityReactorChamberElectric) {
			BlockPos position = IC2ReactorHelper.getTargetCoordinates(world, pos);
			if (position != null) {
				ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_REACTOR);
				ItemStackHelper.setCoordinates(card, position);
				return card;
			}
		} else if (te instanceof TileEntityReactorFluidPort || te instanceof TileEntityReactorRedstonePort
				|| te instanceof TileEntityReactorAccessHatch) {
			BlockPos position = IC2ReactorHelper.get5x5TargetCoordinates(world, pos);
			if (position != null) {
				ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_REACTOR5X5);
				ItemStackHelper.setCoordinates(card, position);
				return card;
			}
		}
		return null;
	}

	@Override
	public NBTTagCompound getReactorData(TileEntity te) {
		if (!(te instanceof TileEntityNuclearReactorElectric))
			return null;
		IReactor reactor = (IReactor) te;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("heat", reactor.getHeat());
		tag.setInteger("maxHeat", reactor.getMaxHeat());
		tag.setBoolean("reactorPoweredB", reactor.produceEnergy());
		tag.setInteger("output", (int) Math.round(reactor.getReactorEUEnergyOutput()));
		tag.setBoolean("isSteam", false);

		IInventory inventory = (IInventory) reactor;
		int slotCount = inventory.getSizeInventory();
		int dmgLeft = 0;
		for (int i = 0; i < slotCount; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null)
				dmgLeft = Math.max(dmgLeft, IC2ReactorHelper.getNuclearCellTimeLeft(stack));
		}
		tag.setInteger("timeLeft", dmgLeft * reactor.getTickRate() / 20);
		return tag;
	}

	@Override
	public NBTTagCompound getReactor5x5Data(TileEntity te) {
		if (!(te instanceof TileEntityNuclearReactorElectric))
			return null;
		IReactor reactor = (IReactor) te;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("heat", reactor.getHeat());
		tag.setInteger("maxHeat", reactor.getMaxHeat());
		tag.setBoolean("reactorPoweredB", reactor.produceEnergy());
		tag.setInteger("output", ((TileEntityNuclearReactorElectric)reactor).EmitHeat);

		IInventory inventory = (IInventory) reactor;
		int slotCount = inventory.getSizeInventory();
		int dmgLeft = 0;
		for (int i = 0; i < slotCount; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null)
				dmgLeft = Math.max(dmgLeft, IC2ReactorHelper.getNuclearCellTimeLeft(stack));
		}

		int timeLeft = dmgLeft * reactor.getTickRate() / 20;
		tag.setInteger("timeLeft", timeLeft);
		return tag;
	}

	@Override
	public int getReactorHeat(World world, BlockPos pos) {
		IReactor reactor = IC2ReactorHelper.getReactorAround(world, pos);
		if (reactor != null)
			return reactor.getHeat();
		reactor = IC2ReactorHelper.getReactor3x3(world, pos);
		if (reactor != null)
			return reactor.getHeat();
		return -1;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		if (!(te instanceof IFluidHandler))
			return null;

		FluidTankInfo[] list = ((IFluidHandler) te).getTankInfo(null);
		List<FluidInfo> result = new ArrayList<>();
		for (FluidTankInfo tank: list)
			result.add(new FluidInfo(tank.fluid, tank.capacity));

		return result;
	}

	/*	@Override
	public ItemStack getLiquidAdvancedCard(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockTileEntity))
			return ItemStack.EMPTY;
		
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityReactorFluidPort || te instanceof TileEntityReactorRedstonePort
				|| te instanceof TileEntityReactorAccessHatch) {
			BlockPos position = ReactorHelper.get5x5TargetCoordinates(world, pos);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_LIQUID_ADVANCED);
				ItemStackHelper.setCoordinates(sensorLocationCard, position);
				return sensorLocationCard;
			}
		}
		return ItemStack.EMPTY;
	}*/

	@Override
	public void registerItems(boolean isClient) {
		ModItems.itemAFB = CrossModLoader.getCrossMod(ModIDs.IC2).getItem("afb").setUnlocalizedName("afb").setRegistryName("afb");
		GameRegistry.register(ModItems.itemAFB);
		ModItems.itemAFSUUpgradeKit = CrossModLoader.getCrossMod(ModIDs.IC2).getItem("afsu_upgrade_kit").setUnlocalizedName("afsu_upgrade_kit").setRegistryName("afsu_upgrade_kit");
		GameRegistry.register(ModItems.itemAFSUUpgradeKit);
		if (isClient) {
			ModItems.registerItemModel(ModItems.itemAFB, 0, "afb");
			ModItems.registerItemModel(ModItems.itemAFSUUpgradeKit, 0, "afsu_upgrade_kit");
		}
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.blockAverageCounter, new Object[] {
			"LAL", "FTF",
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("cable","type:gold,insulation:0"),
				'T', IC2Items.getItem("te","mv_transformer"),
				'L', "plateLead" });

		Recipes.addShapedRecipe(ModItems.blockEnergyCounter, new Object[] {
			"IAI", "FTF", 
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("cable","type:gold,insulation:0"),
				'T', IC2Items.getItem("te","mv_transformer"),
				'I', "plateIron" });

		Recipes.addShapedRecipe(ModItems.blockAfsu, new Object[] {
			"MGM", "IAI", "MGM",
				'I', IC2Items.getItem("crafting","iridium"),
				'G', IC2Items.getItem("cable","type:glass,insulation:0"),
				'M', IC2Items.getItem("te","mfsu"),
				'A', new ItemStack(ModItems.itemAFB, 1, Short.MAX_VALUE) });

		Recipes.addShapedRecipe(ModItems.blockSeedAnalyzer, new Object[] {
			" C ", "WAW", "WBW",
				'C', IC2Items.getItem("cropnalyzer"),
				'W', "plankWood",
				'A', new ItemStack(ModItems.itemComponent, 1, ItemComponent.MACHINE_CASING),
				'B', "circuitBasic" });

		Recipes.addShapedRecipe(ModItems.blockSeedLibrary, new Object[] {
			"GGG", "CAC", "CSC",
				'G', "blockGlass",
				'C', Blocks.CHEST,
				'A', "circuitAdvanced",
				'S', ModItems.blockSeedAnalyzer });

		Recipes.addShapedRecipe(ModItems.itemThermometerDigital, 32767, new Object[] { 
			"RI ", "ITI", " IP",
				'R', "itemRubber",
				'T', ModItems.itemThermometer,
				'I', "plateIron",
				'P', IC2Items.getItem("crafting#small_power_unit") });

		Recipes.addShapedRecipe(ModItems.itemAFB, new Object[] {
				"GIG", "IUI", "GIG",
					'G', IC2Items.getItem("cable","type:glass,insulation:0"),
					'I', IC2Items.getItem("crafting","iridium"),
					'U', IC2Items.getItem("fluid_cell","ic2uu_matter") });

		Recipes.addShapedRecipe(ModItems.itemAFSUUpgradeKit, new Object[] {
			"MGM", "IAI", "MGM",
				'I', IC2Items.getItem("crafting","iridium"),
				'G', IC2Items.getItem("cable","type:glass,insulation:0"),
				'M', IC2Items.getItem("upgrade_kit","mfsu"),
				'A', new ItemStack(ModItems.itemAFB, 1, Short.MAX_VALUE) });

		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_GENERATOR,
			new Object[] { "CF", "PL", 'P', Items.PAPER, 'C', IC2Items.getItem("upgrade", "energy_storage"),
				'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'L', "dyeLightGray" });

		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_REACTOR,
			new Object[] { "DF", "PW", 'P', Items.PAPER, 'D', new ItemStack(ModItems.itemThermometerDigital, 1, 32767),
				'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'W', "dyeYellow" });

		Recipes.addKitRecipe(ItemCardType.KIT_GENERATOR, ItemCardType.CARD_GENERATOR);
		Recipes.addKitRecipe(ItemCardType.KIT_GENERATOR, ItemCardType.CARD_GENERATOR_KINETIC);
		Recipes.addKitRecipe(ItemCardType.KIT_GENERATOR, ItemCardType.CARD_GENERATOR_HEAT);
		Recipes.addKitRecipe(ItemCardType.KIT_REACTOR, ItemCardType.CARD_REACTOR);
		Recipes.addKitRecipe(ItemCardType.KIT_REACTOR, ItemCardType.CARD_REACTOR5X5);
	}
}
