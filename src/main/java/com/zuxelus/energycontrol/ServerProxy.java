package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.*;
import com.zuxelus.energycontrol.items.cards.ItemCardHolder;
import com.zuxelus.energycontrol.tileentities.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.io.File;

public class ServerProxy implements IGuiHandler {

	public void loadConfig(FMLPreInitializationEvent event) {
		EnergyControl.config = new ConfigHandler();
		EnergyControl.config.init(event.getSuggestedConfigurationFile());
	}

	public void registerSpecialRenderers() { }

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case BlockDamages.GUI_PORTABLE_PANEL:
			return new ContainerPortablePanel(player);
		case BlockDamages.GUI_CARD_HOLDER:
			if (player.getHeldItemMainhand().getItem() instanceof ItemCardHolder)
				return new ContainerCardHolder(player);
		}
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
		case BlockDamages.DAMAGE_INFO_PANEL:
			return new ContainerInfoPanel(player, (TileEntityInfoPanel) te);
		case BlockDamages.DAMAGE_INFO_PANEL_EXTENDER:
			if (te instanceof TileEntityInfoPanelExtender) {
				TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
				if (panel != null)
					return new ContainerInfoPanel(player, panel);
			}
			return null;
		case BlockDamages.DAMAGE_ADVANCED_PANEL:
			return new ContainerAdvancedInfoPanel(player, (TileEntityAdvancedInfoPanel) te);
		case BlockDamages.DAMAGE_RANGE_TRIGGER:
			return new ContainerRangeTrigger(player, (TileEntityRangeTrigger) te);
		case BlockDamages.DAMAGE_REMOTE_THERMO:
			return new ContainerRemoteThermo(player, (TileEntityRemoteThermo) te);
		case BlockDamages.DAMAGE_AVERAGE_COUNTER:
			return new ContainerAverageCounter(player, (TileEntityAverageCounter) te);
		case BlockDamages.DAMAGE_ENERGY_COUNTER:
			return new ContainerEnergyCounter(player, (TileEntityEnergyCounter) te);
		case BlockDamages.GUI_KIT_ASSEMBER:
			return new ContainerKitAssembler(player, (TileEntityKitAssembler) te);
		case BlockDamages.DAMAGE_AFSU:
			return new ContainerAFSU(player, (TileEntityAFSU) te);
		case BlockDamages.DAMAGE_SEED_ANALYZER:
			if (te instanceof TileEntitySeedAnalyzer)
				return new ContainerSeedAnalyzer(player, (TileEntitySeedAnalyzer) te);
		case BlockDamages.DAMAGE_SEED_LIBRARY:
			if (te instanceof TileEntitySeedLibrary)
				return new ContainerSeedLibrary(player, (TileEntitySeedLibrary) te);
			break;
		case BlockDamages.DAMAGE_TIMER:
			if (te instanceof TileEntityTimer)
				return new ContainerTimer((TileEntityTimer) te);
			break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(ServerTickHandler.instance);
	}

	public void importSound(File configFolder) { }

	public String getItemName(ItemStack stack) {
		return stack.getItem().getUnlocalizedName();
	}
}
