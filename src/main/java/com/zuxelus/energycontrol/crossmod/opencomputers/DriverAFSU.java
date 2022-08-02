package com.zuxelus.energycontrol.crossmod.opencomputers;

import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;

import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import li.cil.oc.integration.ManagedTileEntityEnvironment;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DriverAFSU extends DriverSidedTileEntity {
	public static final String NAME = "afsu";

	@Override
	public Class<?> getTileEntityClass() {
		return TileEntityAFSU.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(final World world, final BlockPos pos, final EnumFacing side) {
		return new Environment((TileEntityAFSU) world.getTileEntity(pos));
	}

	public static final class Environment extends ManagedTileEntityEnvironment<TileEntityAFSU> implements NamedBlock {
		public Environment(final TileEntityAFSU tileentity) {
			super(tileentity, NAME);
		}

		@Override
		public String preferredName() {
			return NAME;
		}

		@Override
		public int priority() {
			return 0;
		}

		@Callback(doc = "function():double -- Get capacity.")
		public Object[] getCapacity(Context context, Arguments args) {
			return new Object[] { tileEntity.CAPACITY };
		}

		@Callback(doc = "function():double -- Get energy.")
		public Object[] getEnergy(Context context, Arguments args) {
			return new Object[] { tileEntity.getEnergy() };
		}

		@Callback(doc = "function():double -- Get sink tier.")
		public Object[] getSinkTier(Context context, Arguments args) {
			return new Object[] { tileEntity.getSinkTier() };
		}

		@Callback(doc = "function():double -- Get source tier.")
		public Object[] getSourceTier(Context context, Arguments args) {
			return new Object[] { tileEntity.getSourceTier() };
		}
	}
}
