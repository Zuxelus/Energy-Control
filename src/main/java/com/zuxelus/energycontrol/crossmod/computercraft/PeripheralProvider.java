package com.zuxelus.energycontrol.crossmod.computercraft;

import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PeripheralProvider implements IPeripheralProvider {

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		if (world == null)
			return null;

		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null)
			return null;

		if (te instanceof TileEntityAdvancedInfoPanel)
			return new AdvancedInfoPanelPeripheral((TileEntityAdvancedInfoPanel) te);
		if (te instanceof TileEntityInfoPanel)
			return new InfoPanelPeripheral((TileEntityInfoPanel) te);
		return null;
	}
}
