package com.zuxelus.energycontrol.crossmod.opencomputers;

import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;
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

public class DriverAverageCounter extends DriverSidedTileEntity {
	public static final String NAME = "average_counter";

	@Override
	public Class<?> getTileEntityClass() {
		return TileEntityAverageCounter.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(final World world, final BlockPos pos, final EnumFacing side) {
		return new Environment((TileEntityAverageCounter) world.getTileEntity(pos));
	}

	public static final class Environment extends ManagedTileEntityEnvironment<TileEntityAverageCounter> implements NamedBlock {
		public Environment(final TileEntityAverageCounter tileentity) {
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

		@Callback(doc = "function():number -- Get current average period (in seconds).")
		public Object[] getPeriod(final Context context, final Arguments args) {
			return new Object[] { ((int) tileEntity.period) };
		}

		@Callback(doc = "function():number -- Get average value.")
		public Object[] getAverage(final Context context, final Arguments args) {
			return new Object[] { tileEntity.getClientAverage() };
		}
	}
}
