package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardIC2;
import com.zuxelus.energycontrol.items.kits.ItemKitIC2;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import ic2.api.blocks.IWrenchable;
import ic2.api.blocks.IWrenchable.WrenchRegistry;
import ic2.api.blocks.wrench.BaseWrenchHandler;
import ic2.api.blocks.wrench.HorizontalWrenchHandler;
import ic2.api.blocks.wrench.ObserverBlockWrenchHandler;
import ic2.api.reactor.IChamberReactor;
import ic2.api.reactor.IReactor;
import ic2.api.tiles.readers.IEUProducer;
import ic2.api.tiles.readers.IEUStorage;
import ic2.api.util.DirectionList;
import ic2.core.block.generators.tiles.ElectricNuclearReactorTileEntity;
import ic2.core.block.generators.tiles.ElectricReactorChamberTileEntity;
import ic2.core.block.generators.tiles.FuelGenTileEntity;
import ic2.core.block.generators.tiles.GeoGenTileEntity;
import ic2.core.block.generators.tiles.LiquidFuelGenTileEntity;
import ic2.core.block.generators.tiles.SolarPanelTileEntity;
import ic2.core.block.generators.tiles.ThermalGeneratorTileEntity;
import ic2.core.block.generators.tiles.WaterMillTileEntity;
import ic2.core.block.generators.tiles.WaveGenTileEntity;
import ic2.core.block.generators.tiles.WindTurbineTileEntity;
import ic2.core.block.machines.tiles.mv.RefineryTileEntity;
import ic2.core.item.reactor.ReactorUraniumRod;
import ic2.core.platform.wind.WindManager;
import ic2.core.utils.helpers.AABBUtil;
import ic2.core.utils.math.geometry.Box;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;

public class CrossIC2Classic extends CrossModBase {

	@Override
	public CompoundTag getEnergyData(BlockEntity te) {
		CompoundTag tag = new CompoundTag();
		tag.putString(DataHelper.EUTYPE, "EU");
		if (te instanceof IEUStorage) {
			tag.putDouble(DataHelper.ENERGY, ((IEUStorage) te).getStoredEU());
			tag.putDouble(DataHelper.CAPACITY, ((IEUStorage) te).getMaxEU());
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(BlockEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		return null;
	}

	@Override
	public CompoundTag getCardData(BlockEntity te) {
		CompoundTag tag = new CompoundTag();
		if (te instanceof IEUStorage) {
			tag.putDouble(DataHelper.ENERGY, ((IEUStorage) te).getStoredEU());
			tag.putDouble(DataHelper.CAPACITY, ((IEUStorage) te).getMaxEU());
			if (te instanceof IEUProducer) {
				tag.putDouble(DataHelper.OUTPUT, ((IEUProducer) te).getEUProduction());
			}
			if (te instanceof FuelGenTileEntity) {
				tag.putBoolean(DataHelper.ACTIVE, ((FuelGenTileEntity) te).fuel > 0);
				tag.putInt(DataHelper.FUEL, ((FuelGenTileEntity) te).fuel);
			}
			if (te instanceof GeoGenTileEntity) {
				tag.putBoolean(DataHelper.ACTIVE, ((GeoGenTileEntity) te).fuel > 0);
				tag.putInt(DataHelper.FUEL, ((GeoGenTileEntity) te).fuel);
			}
			
			if (te instanceof WindTurbineTileEntity) {
				tag.putInt("obstructedBlocks", 525 - AABBUtil.countBlocks(te.getLevel(), te.getBlockPos(), (new Box(-4, -2, -4, 4, 4, 4)).offset(te.getBlockPos()), ((WindTurbineTileEntity) te).FILTER, 0, DirectionList.ALL, 525));
				tag.putDouble("wind", 16.0D / 100.0D * Math.abs(WindManager.INSTANCE.getAirSpeed(te.getLevel(), (new AABB(-4.0D, -2.0D, -4.0D, 4.0D, 4.0D, 4.0D)).move(te.getBlockPos()), ((WindTurbineTileEntity) te).getFacing().toYRot(), 90.0F)));
			}
			if (te instanceof WaterMillTileEntity) {
				tag.putInt(DataHelper.FUEL, ((WaterMillTileEntity) te).fuel);
			}
			if (te instanceof LiquidFuelGenTileEntity) {
				tag.putInt(DataHelper.FUEL, ((LiquidFuelGenTileEntity) te).fuel);
				FluidInfo.addTank(DataHelper.TANK, tag, ((LiquidFuelGenTileEntity) te).tank);
			}
			if (te instanceof ThermalGeneratorTileEntity) {
				tag.putBoolean(DataHelper.ACTIVE, ((ThermalGeneratorTileEntity) te).isActive());
				if (!((ThermalGeneratorTileEntity) te).isActive())
					tag.putDouble(DataHelper.OUTPUT, 0);
				tag.putInt(DataHelper.FUEL, ((ThermalGeneratorTileEntity) te).getFuel());
			}
			if (te instanceof RefineryTileEntity) {
				FluidInfo.addTank(DataHelper.TANK, tag, ((RefineryTileEntity) te).firstTank);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((RefineryTileEntity) te).secondTank);
				FluidInfo.addTank(DataHelper.TANK3, tag, ((RefineryTileEntity) te).output);
			}
			return tag;
		}
		if (te instanceof SolarPanelTileEntity) {
			tag.putBoolean(DataHelper.ACTIVE, ((SolarPanelTileEntity) te).isActive());
			tag.putDouble(DataHelper.OUTPUT, ((SolarPanelTileEntity) te).isActive() ? ((SolarPanelTileEntity) te).getEUProduction() : 0);
			return tag;
		}
		if (te instanceof WaveGenTileEntity) {
			tag.putDouble(DataHelper.ENERGY, ((WaveGenTileEntity) te).getProvidedEnergy());
			tag.putDouble(DataHelper.CAPACITY, ((WaveGenTileEntity) te).getMaxEnergyOutput());
			tag.putDouble(DataHelper.OUTPUT, ((WaveGenTileEntity) te).getEUProduction());
			return tag;
		}
		if (te instanceof ElectricNuclearReactorTileEntity) {
			return getReactorData((ElectricNuclearReactorTileEntity) te);
		}
		if (te instanceof ElectricReactorChamberTileEntity) {
			IReactor reactor = ((ElectricReactorChamberTileEntity) te).getReactor();
			if (reactor instanceof ElectricNuclearReactorTileEntity)
				return getReactorData((ElectricNuclearReactorTileEntity) reactor);
		}
		return null;
	}

	private CompoundTag getReactorData(ElectricNuclearReactorTileEntity reactor) {
		if (reactor == null)
			return null;
		CompoundTag tag = new CompoundTag();
		tag.putInt(DataHelper.HEAT, reactor.getHeat());
		tag.putInt(DataHelper.MAXHEAT, reactor.getMaxHeat());
		tag.putBoolean(DataHelper.ACTIVE, reactor.isProducingEnergy());
		tag.putInt(DataHelper.OUTPUT, (int) Math.round(reactor.getEnergyOutput()));
		int dmgLeft = 0;
		for (int y = 0; y < reactor.getHeight(); y++)
			for (int x = 0; x < reactor.getWidth(); x++) {
				ItemStack stack = reactor.getStackInReactor(x, y);
				if (!stack.isEmpty())
					dmgLeft = Math.max(dmgLeft, getNuclearCellTimeLeft(stack));
			}
		tag.putInt("timeLeft", dmgLeft * reactor.getTickRate() / 20);
		return tag;
	}

	private int getNuclearCellTimeLeft(ItemStack stack) {
		if (stack.isEmpty())
			return 0;
		
		Item item = stack.getItem();
		if (item instanceof ReactorUraniumRod)
			return stack.getMaxDamage() - stack.getDamageValue();
		return 0;
	}

	@Override
	public int getReactorHeat(Level world, BlockPos pos) {
		IReactor reactor = IC2ReactorHelper.getReactorAround(world, pos);
		if (reactor != null)
			return reactor.getHeat();
		reactor = IC2ReactorHelper.getReactor3x3(world, pos);
		if (reactor != null)
			return reactor.getHeat();
		return -1;
	}

	@Override
	public void registerItems(RegisterHelper<Item> event) {
		ModItems.kit_ic2 = new ItemKitIC2();
		event.register("kit_ic2", ModItems.kit_ic2);
		ModItems.card_ic2 = new ItemCardIC2();
		event.register("card_ic2", ModItems.card_ic2);

		WrenchRegistry.INSTANCE.registerWrenchHandler(HorizontalHandler.INSTANCE, new Block[] {
				ModItems.holo_panel.get(), ModItems.holo_panel_extender.get(), ModItems.kit_assembler.get(), ModItems.range_trigger.get(), ModItems.remote_thermo.get() });

		WrenchRegistry.INSTANCE.registerWrenchHandler(FullHandler.INSTANCE, new Block[] {
				ModItems.info_panel.get(), ModItems.info_panel_extender.get(), ModItems.info_panel_advanced.get(), ModItems.info_panel_advanced_extender.get() });
	}

	public static class HorizontalHandler extends HorizontalWrenchHandler {
		public static final IWrenchable INSTANCE = new HorizontalHandler();

		@Override
		public boolean canRemoveBlock(BlockState state, Level world, BlockPos pos, Player player) {
			return true;
		}

		@Override
		public double getDropRate(BlockState state, Level world, BlockPos pos, Player player) {
			return 1.0D;
		}
	}

	public static class FullHandler extends BaseWrenchHandler {
		public static final IWrenchable INSTANCE = new FullHandler();

		@Override
		public Direction getFacing(BlockState state, Level world, BlockPos pos) {
			return state.getValue(FacingBlock.FACING);
		}

		@Override
		public boolean canSetFacing(BlockState state, Level world, BlockPos pos, Player player, Direction side) {
			BlockEntity be = world.getBlockEntity(pos);
			return be instanceof BlockEntityFacing && state.getValue(FacingBlock.FACING) != side;
		}

		@Override
		public boolean setFacing(BlockState state, Level world, BlockPos pos, Player player, Direction side) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof BlockEntityFacing) {
				((BlockEntityFacing) be).setFacing(side.get3DDataValue());
				((BlockEntityFacing) be).setRotation(side.get3DDataValue() > 1 ? Direction.DOWN : Direction.NORTH);
				return world.setBlockAndUpdate(pos, (BlockState) state.setValue(FacingBlock.FACING, side));
			}
			return false;
		}

		@Override
		public boolean canRemoveBlock(BlockState state, Level world, BlockPos pos, Player player) {
			return true;
		}

		@Override
		public double getDropRate(BlockState state, Level world, BlockPos pos, Player player) {
			return 1.0D;
		}
	}
}
