package com.zuxelus.energycontrol.crossmod.computercraft;

import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class PeripheralProvider implements IPeripheralProvider {

	@Override
	public LazyOptional<IPeripheral> getPeripheral(World world, BlockPos pos, Direction side) {
		if (world == null)
			return LazyOptional.empty();

		TileEntity te = world.getTileEntity(pos);
		if (te == null)
			return LazyOptional.empty();

		if (te instanceof TileEntityAdvancedInfoPanel)
			return LazyOptional.of(() -> new AdvancedInfoPanelPeripheral((TileEntityAdvancedInfoPanel) te));
		if (te instanceof TileEntityInfoPanel)
			return LazyOptional.of(() -> new InfoPanelPeripheral((TileEntityInfoPanel) te));
		return LazyOptional.empty();
	}
}
