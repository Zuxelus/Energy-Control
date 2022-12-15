package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.maciej916.indreb.common.api.blockentity.IndRebBlockEntity;
import com.maciej916.indreb.common.api.fluid.FluidStorage;
import com.maciej916.indreb.common.api.recipe.BaseRecipe;
import com.maciej916.indreb.common.block.impl.battery_box.BlockEntityBatteryBox;
import com.maciej916.indreb.common.block.impl.charge_pad.BlockEntityChargePad;
import com.maciej916.indreb.common.block.impl.generator.generator.BlockEntityGenerator;
import com.maciej916.indreb.common.block.impl.generator.geo_generator.BlockEntityGeoGenerator;
import com.maciej916.indreb.common.block.impl.generator.reactor.BlockEntityReactorPart;
import com.maciej916.indreb.common.block.impl.generator.reactor.nuclear_reactor.BlockEntityNuclearReactor;
import com.maciej916.indreb.common.block.impl.generator.reactor.nuclear_reactor.BlockNuclearReactor;
import com.maciej916.indreb.common.block.impl.generator.semifluid_generator.BlockEntitySemifluidGenerator;
import com.maciej916.indreb.common.block.impl.generator.solar_panel.BlockEntitySolarPanel;
import com.maciej916.indreb.common.block.impl.generator.solar_panel.BlockSolarPanel;
import com.maciej916.indreb.common.block.impl.machine.t_advanced.matter_fabricator.BlockEntityMatterFabricator;
import com.maciej916.indreb.common.block.impl.machine.t_basic.canning_machine.BlockEntityCanningMachine;
import com.maciej916.indreb.common.block.impl.machine.t_basic.compressor.BlockEntityCompressor;
import com.maciej916.indreb.common.block.impl.machine.t_basic.crusher.BlockEntityCrusher;
import com.maciej916.indreb.common.block.impl.machine.t_basic.electric_furnace.BlockEntityElectricFurnace;
import com.maciej916.indreb.common.block.impl.machine.t_basic.extractor.BlockEntityExtractor;
import com.maciej916.indreb.common.block.impl.machine.t_basic.extruder.BlockEntityExtruder;
import com.maciej916.indreb.common.block.impl.machine.t_basic.fluid_enricher.BlockEntityFluidEnricher;
import com.maciej916.indreb.common.block.impl.machine.t_basic.metal_former.BlockEntityMetalFormer;
import com.maciej916.indreb.common.block.impl.machine.t_basic.recycler.BlockEntityRecycler;
import com.maciej916.indreb.common.block.impl.machine.t_basic.sawmill.BlockEntitySawmill;
import com.maciej916.indreb.common.block.impl.machine.t_standard.alloy_smelter.BlockEntityAlloySmelter;
import com.maciej916.indreb.common.block.impl.machine.t_standard.fermenter.BlockEntityFermenter;
import com.maciej916.indreb.common.block.impl.machine.t_standard.ore_washing_plant.BlockEntityOreWashingPlant;
import com.maciej916.indreb.common.block.impl.machine.t_standard.thermal_centrifuge.BlockEntityThermalCentrifuge;
import com.maciej916.indreb.common.block.impl.machine.t_super.replicator.BlockEntityReplicator;
import com.maciej916.indreb.common.block.impl.machine.t_super.scanner.BlockEntityScanner;
import com.maciej916.indreb.common.capability.scanner.ScannerResult;
import com.maciej916.indreb.common.config.impl.ServerConfig;
import com.maciej916.indreb.common.enums.MetalFormerMode;
import com.maciej916.indreb.common.multiblock.reactor.ReactorPartIndex;
import com.maciej916.indreb.common.recipe.ModRecipeType;
import com.maciej916.indreb.common.recipe.impl.*;
import com.maciej916.indreb.common.tag.ModBlockTags;
import com.maciej916.indreb.common.util.BlockStateHelper;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CrossIndustrialReborn extends CrossModBase {
	public static Map<BlockEntity, ArrayList<Double>> map = new HashMap<BlockEntity, ArrayList<Double>>();

	@Override
	public CompoundTag getEnergyData(BlockEntity te) {
		CompoundTag tag = new CompoundTag();
		if (te instanceof IndRebBlockEntity) {
			IndRebBlockEntity be = (IndRebBlockEntity) te;
			if (be.hasEnergyStorage()) {
				tag.putDouble(DataHelper.ENERGY, be.getEnergyStorage().energyStored());
				tag.putDouble(DataHelper.CAPACITY, be.getEnergyStorage().maxEnergy());
				return tag;
			}
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(BlockEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof BlockEntityGeoGenerator) {
			result.add(new FluidInfo(((BlockEntityGeoGenerator) te).lavaStorage));
			return result;
		}
		if (te instanceof BlockEntitySemifluidGenerator) {
			result.add(new FluidInfo(((BlockEntitySemifluidGenerator) te).fuelStorage));
			return result;
		}
		if (te instanceof BlockEntityExtruder) {
			result.add(new FluidInfo(((BlockEntityExtruder) te).firstTank));
			result.add(new FluidInfo(((BlockEntityExtruder) te).secondTank));
			return result;
		}
		if (te instanceof BlockEntityFluidEnricher) {
			result.add(new FluidInfo(((BlockEntityFluidEnricher) te).firstTank));
			result.add(new FluidInfo(((BlockEntityFluidEnricher) te).secondTank));
			return result;
		}
		if (te instanceof BlockEntityFermenter) {
			result.add(new FluidInfo(((BlockEntityFermenter) te).firstTank));
			result.add(new FluidInfo(((BlockEntityFermenter) te).secondTank));
			return result;
		}
		if (te instanceof BlockEntityOreWashingPlant) {
			result.add(new FluidInfo(((BlockEntityOreWashingPlant) te).firstTank));
			return result;
		}
		if (te instanceof BlockEntityReplicator) {
			result.add(new FluidInfo(((BlockEntityReplicator) te).firstTank));
			return result;
		}
		if (te instanceof BlockEntityMatterFabricator) {
			result.add(new FluidInfo(((BlockEntityMatterFabricator) te).firstTank));
			return result;
		}
		return null;
	}

	@Override
	public CompoundTag getCardData(BlockEntity te) {
		CompoundTag tag = new CompoundTag();
		boolean active = false;
		if (te instanceof IndRebBlockEntity) {
			IndRebBlockEntity be = (IndRebBlockEntity) te;
			if (be.hasEnergyStorage()) {
				tag.putDouble(DataHelper.ENERGY, be.getEnergyStorage().energyStored());
				tag.putDouble(DataHelper.CAPACITY, be.getEnergyStorage().maxEnergy());
			}
			if (te.getBlockState().hasProperty(BlockStateHelper.ACTIVE_PROPERTY)) {
				active = te.getBlockState().getValue(BlockStateHelper.ACTIVE_PROPERTY);
				tag.putBoolean(DataHelper.ACTIVE, active);
			}
		}
		if (te instanceof BlockEntityBatteryBox) {
			ArrayList<Double> values = map.get(te);
			if (values == null)
				map.put(te, null);
			else
				tag.putDouble(DataHelper.DIFF, ((Double) values.get(0) - (Double) values.get(20)) / 20);
			return tag;
		}
		if (te instanceof BlockEntityChargePad) {
			return tag;
		}
		if (te instanceof BlockEntityGenerator) {
			tag.putDouble(DataHelper.OUTPUT, active ? ((Integer)ServerConfig.generator_tick_generate.get()).intValue() : 0);
			return tag;
		}
		if (te instanceof BlockEntityGeoGenerator) {
			tag.putDouble(DataHelper.OUTPUT, active ? ((Integer)ServerConfig.geo_generator_tick_generate.get()).intValue() : 0);
			FluidInfo.addTank(DataHelper.TANK, tag, ((BlockEntityGeoGenerator) te).lavaStorage);
			return tag;
		}
		if (te instanceof BlockEntitySemifluidGenerator) {
			tag.putDouble(DataHelper.OUTPUT, active ? ((Integer)ServerConfig.semifluid_generator_tick_generate.get()).intValue() : 0);
			FluidInfo.addTank(DataHelper.TANK, tag, ((BlockEntitySemifluidGenerator) te).fuelStorage);
			return tag;
		}
		if (te instanceof BlockEntityElectricFurnace) {
			tag.putDouble(DataHelper.CONSUMPTION, active ? ((BlockEntityElectricFurnace) te).getUpgradesEnergyCost((ServerConfig.electric_furnace_tick_usage.get()).intValue()) : 0);
			return tag;
		}
		if (te instanceof BlockEntityCrusher) {
			BlockEntityCrusher crusher = (BlockEntityCrusher) te;
			CrushingRecipe recipe = te.getLevel().getRecipeManager().getRecipeFor(ModRecipeType.CRUSHING.get(),
				new SimpleContainer(new ItemStack[] { crusher.getBaseStorage().getStackInSlot(0) }), te.getLevel()).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? crusher.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			return tag;
		}
		if (te instanceof BlockEntityCompressor) {
			BlockEntityCompressor compressor = (BlockEntityCompressor) te;
			CompressingRecipe recipe = te.getLevel().getRecipeManager().getRecipeFor(ModRecipeType.COMPRESSING.get(),
				new SimpleContainer(new ItemStack[] { compressor.getBaseStorage().getStackInSlot(0) }), te.getLevel()).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? compressor.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			return tag;
		}
		if (te instanceof BlockEntityExtractor) {
			BlockEntityExtractor extractor = (BlockEntityExtractor) te;
			ExtractingRecipe recipe = te.getLevel().getRecipeManager().getRecipeFor(ModRecipeType.EXTRACTING.get(),
				new SimpleContainer(new ItemStack[] { extractor.getBaseStorage().getStackInSlot(0) }), te.getLevel()).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? extractor.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			return tag;
		}
		if (te instanceof BlockEntitySawmill) {
			BlockEntitySawmill sawmill = (BlockEntitySawmill) te;
			SawingRecipe recipe = te.getLevel().getRecipeManager().getRecipeFor(ModRecipeType.SAWING.get(),
				new SimpleContainer(new ItemStack[] { sawmill.getBaseStorage().getStackInSlot(0) }), te.getLevel()).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? sawmill.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			return tag;
		}
		if (te instanceof BlockEntityExtruder) {
			BlockEntityExtruder extruder = (BlockEntityExtruder) te;
			//List<FluidExtrudingRecipe> recipes = te.getLevel().getRecipeManager().getAllRecipesFor(ModRecipeType.FLUID_EXTRUDING.get());
			tag.putDouble(DataHelper.CONSUMPTION, active ? extruder.getUpgradesEnergyCost(8) : 0);
			FluidInfo.addTank(DataHelper.TANK, tag, extruder.firstTank);
			FluidInfo.addTank(DataHelper.TANK2, tag, extruder.secondTank);
			return tag;
		}
		if (te instanceof BlockEntityCanningMachine) {
			BlockEntityCanningMachine machine = (BlockEntityCanningMachine) te;
			CanningRecipe recipe = te.getLevel().getRecipeManager().getRecipeFor(ModRecipeType.CANNING.get(),
				new SimpleContainer(new ItemStack[] { machine.getBaseStorage().getStackInSlot(0), machine.getBaseStorage().getStackInSlot(1) }),
				te.getLevel()).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? machine.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			return tag;
		}
		if (te instanceof BlockEntityFluidEnricher) {
			BlockEntityFluidEnricher enricher = (BlockEntityFluidEnricher) te;
			FluidEnrichingRecipe recipe = getEnricherRecipe(te.getLevel(), enricher.getBaseStorage().getStackInSlot(0), enricher.firstTank).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? enricher.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			FluidInfo.addTank(DataHelper.TANK, tag, enricher.firstTank);
			FluidInfo.addTank(DataHelper.TANK2, tag, enricher.secondTank);
			return tag;
		}
		if (te instanceof BlockEntityRecycler) {
			BlockEntityRecycler recycler = (BlockEntityRecycler) te;
			RecyclingRecipe recipe = te.getLevel().getRecipeManager().getRecipeFor(ModRecipeType.RECYCLING.get(),
				new SimpleContainer(new ItemStack[] { recycler.getBaseStorage().getStackInSlot(0), recycler.getBaseStorage().getStackInSlot(1) }),
				te.getLevel()).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? recycler.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			return tag;
		}
		if (te instanceof BlockEntityFermenter) {
			BlockEntityFermenter fermenter = (BlockEntityFermenter) te;
			FermentingRecipe recipe = getFermenterRecipe(te.getLevel(), fermenter.firstTank).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? fermenter.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			FluidInfo.addTank(DataHelper.TANK, tag, fermenter.firstTank);
			FluidInfo.addTank(DataHelper.TANK2, tag, fermenter.secondTank);
			return tag;
		}
		if (te instanceof BlockEntityOreWashingPlant) {
			BlockEntityOreWashingPlant oreWashing = (BlockEntityOreWashingPlant) te;
			OreWashingRecipe recipe = getOreWashingRecipe(te.getLevel(), oreWashing.getBaseStorage().getStackInSlot(2), oreWashing.firstTank).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? oreWashing.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			FluidInfo.addTank(DataHelper.TANK, tag, oreWashing.firstTank);
			return tag;
		}
		if (te instanceof BlockEntityAlloySmelter) {
			BlockEntityAlloySmelter smelter = (BlockEntityAlloySmelter) te;
			AlloySmeltingRecipe recipe = getAlloySmeltingRecipe(te.getLevel(), new SimpleContainer(new ItemStack[] {
				smelter.getBaseStorage().getStackInSlot(0),
				smelter.getBaseStorage().getStackInSlot(1),
				smelter.getBaseStorage().getStackInSlot(2)
			})).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? smelter.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			return tag;
		}
		if (te instanceof BlockEntityMetalFormer) {
			BlockEntityMetalFormer former = (BlockEntityMetalFormer) te;
			BaseRecipe recipe = (BaseRecipe) getMetalFormerRecipe(te.getLevel(), former.getMode(), former.getBaseStorage().getStackInSlot(0)).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? former.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			return tag;
		}
		if (te instanceof BlockEntityThermalCentrifuge) {
			BlockEntityThermalCentrifuge centrifuge = (BlockEntityThermalCentrifuge) te;
			ThermalCentrifugingRecipe recipe = te.getLevel().getRecipeManager().getRecipeFor(ModRecipeType.THERMAL_CENTRIFUGING.get(),
				new SimpleContainer(new ItemStack[] { centrifuge.getBaseStorage().getStackInSlot(0) }), te.getLevel()).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? centrifuge.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			return tag;
		}
		if (te instanceof BlockEntityReplicator) {
			BlockEntityReplicator replicator = (BlockEntityReplicator) te;
			ScannerResult result = replicator.getResult();
			tag.putDouble(DataHelper.CONSUMPTION, active && result != null ? result.getEnergyCost() : 0);
			FluidInfo.addTank(DataHelper.TANK, tag, replicator.firstTank);
			return tag;
		}
		if (te instanceof BlockEntityMatterFabricator) {
			BlockEntityMatterFabricator fabricator = (BlockEntityMatterFabricator) te;
			int cost = Math.min((int) fabricator.progressRecipe.getProgressMax() - (int) fabricator.progressRecipe.getCurrentProgress(), fabricator.getEnergyStorage().energyStored());
			tag.putDouble(DataHelper.CONSUMPTION, active ? cost : 0);
			FluidInfo.addTank(DataHelper.TANK, tag, fabricator.firstTank);
			return tag;
		}
		if (te instanceof BlockEntityScanner) {
			BlockEntityScanner scanner = (BlockEntityScanner) te;
			ScanningRecipe recipe = te.getLevel().getRecipeManager().getRecipeFor(ModRecipeType.SCANNING.get(),
				new SimpleContainer(new ItemStack[] { scanner.getBaseStorage().getStackInSlot(0) }), te.getLevel()).orElse(null);
			tag.putDouble(DataHelper.CONSUMPTION, active && recipe != null ? scanner.getUpgradesEnergyCost(recipe.getTickEnergyCost()) : 0);
			return tag;
		}
		if (te instanceof BlockEntitySolarPanel) {
			Block block = te.getBlockState().getBlock();
			if (block instanceof BlockSolarPanel) {
				tag.putDouble(DataHelper.OUTPUT, active ? ((BlockSolarPanel) block).getSolarTier().getDayGenerate() : 0);
			}
			return tag;
		}
		if (te instanceof BlockEntityReactorPart) {
			BlockState state = te.getBlockState();
			if (state.is(ModBlockTags.REACTOR_PART) && state.getValue(BlockStateHelper.REACTOR_PART) != ReactorPartIndex.UNFORMED) {
				BlockPos controllerPos = BlockNuclearReactor.getControllerPos(te.getLevel(), te.getBlockPos());
				if (controllerPos != null) {
					BlockEntity be = te.getLevel().getBlockEntity(controllerPos);
					if (be instanceof BlockEntityNuclearReactor) {
						BlockEntityNuclearReactor reactor = (BlockEntityNuclearReactor) be;
						tag.putBoolean(DataHelper.ACTIVE, reactor.getReactor().getEnabled());
						tag.putDouble(DataHelper.HEAT, reactor.getReactor().getCurrentHeat());
						tag.putDouble(DataHelper.MAXHEAT, reactor.getReactor().getMaxHeat());
						tag.putDouble(DataHelper.OUTPUT, reactor.getReactor().getCurrentIEOutput() / 20);
						return tag;
					}
				}
			}
		}
		return null;
	}

	private Optional<FluidEnrichingRecipe> getEnricherRecipe(Level level, ItemStack input, FluidStorage firstTank) {
		List<FluidEnrichingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeType.FLUID_ENRICHING.get());
		for (FluidEnrichingRecipe recipe : recipes)
			if (recipe.matches(new SimpleContainer(new ItemStack[] { input }), level) && recipe.getFluidInput().getFluid() == firstTank.getFluid().getFluid())
				return Optional.of(recipe);
		return Optional.empty();
	}

	private Optional<FermentingRecipe> getFermenterRecipe(Level level, FluidStorage firstTank) {
		List<FermentingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeType.FERMENTING.get());
		for (FermentingRecipe recipe : recipes)
			if (recipe.getFluidInput().getFluid() == firstTank.getFluid().getFluid())
				return Optional.of(recipe);
		return Optional.empty();
	}

	private Optional<OreWashingRecipe> getOreWashingRecipe(Level level, ItemStack input, FluidStorage firstTank) {
		List<OreWashingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeType.ORE_WASHING.get());
		for (OreWashingRecipe recipe : recipes)
			if (recipe.matches(new SimpleContainer(new ItemStack[] { input }), level) && recipe.getFluidInput().getFluid() == firstTank.getFluid().getFluid())
				return Optional.of(recipe);
		return Optional.empty();
	}

	private Optional<AlloySmeltingRecipe> getAlloySmeltingRecipe(Level level, SimpleContainer container) {
		List<AlloySmeltingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeType.ALLOY_SMELTING.get());
		for (AlloySmeltingRecipe recipe : recipes)
			if (recipe.matches(container, level))
				return Optional.of(recipe);
		return Optional.empty();
	}

	private Optional<?> getMetalFormerRecipe(Level level, MetalFormerMode mode, ItemStack input) {
		if (level != null)
			switch (mode.getId()) {
			case 1:
				return level.getRecipeManager().getRecipeFor(ModRecipeType.CUTTING.get(), new SimpleContainer(new ItemStack[] { input }), level);
			case 2:
				return level.getRecipeManager().getRecipeFor(ModRecipeType.ROLLING.get(), new SimpleContainer(new ItemStack[] { input }), level);
			case 3:
				return level.getRecipeManager().getRecipeFor(ModRecipeType.EXTRUDING.get(), new SimpleContainer(new ItemStack[] { input }), level);
			}
		return Optional.empty();
	}
}
