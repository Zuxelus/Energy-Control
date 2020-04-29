package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.*;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ServerProxy implements IGuiHandler {
	public void loadConfig(FMLPreInitializationEvent event) {
		EnergyControl.config = new ConfigHandler();
		EnergyControl.config.init(event.getSuggestedConfigurationFile());
	}
	
	public void registerSpecialRenderers() { }
	
	public void registerExtendedModels() { }

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == BlockDamages.GUI_PORTABLE_PANEL)
			return new ContainerPortablePanel(player);
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
		case BlockDamages.DAMAGE_INFO_PANEL:
			return new ContainerInfoPanel(player, (TileEntityInfoPanel) tileEntity);
		case BlockDamages.DAMAGE_RANGE_TRIGGER:
			return new ContainerRangeTrigger(player, (TileEntityRangeTrigger) tileEntity);
		case BlockDamages.DAMAGE_REMOTE_THERMO:
			return new ContainerRemoteThermo(player, (TileEntityRemoteThermo) tileEntity);
		case BlockDamages.DAMAGE_AVERAGE_COUNTER:
			return new ContainerAverageCounter(player, (TileEntityAverageCounter) tileEntity);
		case BlockDamages.DAMAGE_ENERGY_COUNTER:
			return new ContainerEnergyCounter(player, (TileEntityEnergyCounter) tileEntity);
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(ServerTickHandler.instance);
	}

	public void importSound() { }
}