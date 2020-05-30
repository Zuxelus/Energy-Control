package com.zuxelus.energycontrol.crossmod.opencomputers;

import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;

import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import li.cil.oc.integration.ManagedTileEntityEnvironment;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class DriverEnergyCounter extends DriverSidedTileEntity {
	public static final String NAME = "energy_counter";

	@Override
	public Class<?> getTileEntityClass() {
		return TileEntityEnergyCounter.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection dir) {
		return new Environment((TileEntityEnergyCounter) world.getTileEntity(x, y, z));
	}

	public static final class Environment extends ManagedTileEntityEnvironment<TileEntityEnergyCounter> implements NamedBlock {
		public Environment(final TileEntityEnergyCounter tileentity) {
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

		@Callback(doc = "function():double -- Get counter.")
		public Object[] getCount(final Context context, final Arguments args) {
			return new Object[] { tileEntity.counter };
		}

		@Callback(doc = "function():double -- Reset counter.")
		public Object[] reset(final Context context, final Arguments args) {
			tileEntity.counter = 0;
			return null;
		}
	}
}
