package com.zuxelus.energycontrol.crossmod.opencomputers;

import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;

import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import li.cil.oc.integration.ManagedTileEntityEnvironment;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class DriverHowlerAlarm extends DriverSidedTileEntity {
	public static final String NAME = "howler_alarm";

	@Override
	public Class<?> getTileEntityClass() {
		return TileEntityHowlerAlarm.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection dir) {
		return new Environment((TileEntityHowlerAlarm) world.getTileEntity(x, y, z));
	}

	public static final class Environment extends ManagedTileEntityEnvironment<TileEntityHowlerAlarm> implements NamedBlock {
		public Environment(final TileEntityHowlerAlarm tileentity) {
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

		@Callback(doc = "function():string -- Get sound name.")
		public Object[] getSoundName(final Context context, final Arguments args) {
			return new Object[] { tileEntity.getSoundName() };
		}

		@Callback(doc = "function():number -- Get range.")
		public Object[] getRange(final Context context, final Arguments args) {
			return new Object[] { tileEntity.getRange() };
		}
	}
}
