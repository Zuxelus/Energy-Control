package com.zuxelus.energycontrol.crossmod.opencomputers;

import com.zuxelus.energycontrol.tileentities.TileEntityThermo;

import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import li.cil.oc.integration.ManagedTileEntityEnvironment;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class DriverThermalMonitor extends DriverSidedTileEntity {
	public static final String NAME = "thermal_monitor";

	@Override
	public Class<?> getTileEntityClass() {
		return TileEntityThermo.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection dir) {
		return new Environment((TileEntityThermo) world.getTileEntity(x, y, z));
	}

	public static final class Environment extends ManagedTileEntityEnvironment<TileEntityThermo> implements NamedBlock {
		public Environment(final TileEntityThermo tileentity) {
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

		@Callback(doc = "function():number -- Get status.")
		public Object[] getStatus(final Context context, final Arguments args) {
			return new Object[] { tileEntity.getStatus() };
		}

		@Callback(doc = "function():number -- Get reactor heat level.")
		public Object[] getHeatLevel(final Context context, final Arguments args) {
			return new Object[] { tileEntity.getHeatLevel() };
		}

		@Callback(doc = "function(number) -- Set reactor heat level.")
		public Object[] setHeatLevel(final Context context, final Arguments args) {
			int value = args.checkInteger(0);
			if (value > 0 && value < 1000000)
				tileEntity.setHeatLevel(value);
			return null;
		}
	}
}
