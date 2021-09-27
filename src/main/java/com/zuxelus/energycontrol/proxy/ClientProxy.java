package com.zuxelus.energycontrol.proxy;

import com.zuxelus.energycontrol.ClientTickHandler;
import com.zuxelus.energycontrol.ServerTickHandler;
import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.containers.*;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.gui.*;
import com.zuxelus.energycontrol.items.cards.ItemCardHolder;
import com.zuxelus.energycontrol.renderers.*;
import com.zuxelus.energycontrol.tileentities.*;
import com.zuxelus.energycontrol.utils.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.List;

public class ClientProxy implements IProxy {
	public static KeyBinding modeSwitchKey;

	@Override
	public void loadConfig(FMLPreInitializationEvent event) {
		if (!Loader.isModLoaded(ModIDs.IC2) && Loader.isModLoaded(ModIDs.TECH_REBORN)) {
			modeSwitchKey = new KeyBinding("Mode Switch Key", 50, "Energy Control");
			ClientRegistry.registerKeyBinding(modeSwitchKey);
		}
	}

	@Override
	public void registerSpecialRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityThermo.class, new TEThermoRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRemoteThermo.class, new TERemoteThermoRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanel.class, new TileEntityInfoPanelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanelExtender.class, new TEInfoPanelExtenderRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvancedInfoPanel.class, new TEAdvancedInfoPanelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvancedInfoPanelExtender.class, new TEAdvancedInfoPanelExtenderRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHoloPanel.class, new TileEntityHoloPanelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTimer.class, new TileEntityTimerRenderer());
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID)
		{
		case BlockDamages.GUI_PORTABLE_PANEL:
			return new GuiPortablePanel(new ContainerPortablePanel(player));
		case BlockDamages.GUI_CARD_HOLDER:
			if (player.getHeldItemMainhand().getItem() instanceof ItemCardHolder)
				return new GuiCardHolder(player);
		}
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
		case BlockDamages.DAMAGE_THERMAL_MONITOR:
			if (te instanceof TileEntityThermo)
				return new GuiThermalMonitor((TileEntityThermo) te);
			break;
		case BlockDamages.DAMAGE_HOWLER_ALARM:
			if (te instanceof TileEntityHowlerAlarm)
				return new GuiHowlerAlarm((TileEntityHowlerAlarm) te);
		case BlockDamages.DAMAGE_INDUSTRIAL_ALARM:
			if (te instanceof TileEntityIndustrialAlarm)
				return new GuiIndustrialAlarm((TileEntityIndustrialAlarm) te);
			break;
		case BlockDamages.DAMAGE_INFO_PANEL:
			if (te instanceof TileEntityInfoPanel)
				return new GuiInfoPanel(new ContainerInfoPanel(player, (TileEntityInfoPanel) te));
			break;
		case BlockDamages.DAMAGE_ADVANCED_PANEL:
			if (te instanceof TileEntityAdvancedInfoPanel)
				return new GuiAdvancedInfoPanel(new ContainerAdvancedInfoPanel(player, (TileEntityAdvancedInfoPanel) te));
			break;
		case BlockDamages.DAMAGE_HOLO_PANEL:
			if (te instanceof TileEntityHoloPanel)
				return new GuiHoloPanel(new ContainerHoloPanel(player, (TileEntityHoloPanel) te));
			break;
		case BlockDamages.DAMAGE_RANGE_TRIGGER:
			if (te instanceof TileEntityRangeTrigger)
				return new GuiRangeTrigger(new ContainerRangeTrigger(player, (TileEntityRangeTrigger) te));
			break;
		case BlockDamages.DAMAGE_REMOTE_THERMO:
			if (te instanceof TileEntityRemoteThermo)
				return new GuiRemoteThermo(new ContainerRemoteThermo(player, (TileEntityRemoteThermo) te));
			break;
		case BlockDamages.DAMAGE_AVERAGE_COUNTER:
			if (te instanceof TileEntityAverageCounter)
				return new GuiAverageCounter(new ContainerAverageCounter(player, (TileEntityAverageCounter) te));
			break;
		case BlockDamages.DAMAGE_ENERGY_COUNTER:
			if (te instanceof TileEntityEnergyCounter)
				return new GuiEnergyCounter(new ContainerEnergyCounter(player, (TileEntityEnergyCounter) te));
			break;
		case BlockDamages.GUI_KIT_ASSEMBER:
			if (te instanceof TileEntityKitAssembler)
				return new GuiKitAssembler(new ContainerKitAssembler(player, (TileEntityKitAssembler) te));
			break;
		case BlockDamages.DAMAGE_AFSU:
			if (te instanceof TileEntityAFSU)
				return new GuiAFSU(new ContainerAFSU(player, (TileEntityAFSU) te));
			break;
		case BlockDamages.DAMAGE_SEED_ANALYZER:
			if (te instanceof TileEntitySeedAnalyzer)
				return new GuiSeedAnalyzer(new ContainerSeedAnalyzer(player, (TileEntitySeedAnalyzer) te));
			break;
		case BlockDamages.DAMAGE_SEED_LIBRARY:
			if (te instanceof TileEntitySeedLibrary)
				return new GuiSeedLibrary(new ContainerSeedLibrary(player, (TileEntitySeedLibrary) te));
			break;
		case BlockDamages.DAMAGE_TIMER:
			if (te instanceof TileEntityTimer)
				return new GuiTimer(new ContainerTimer(player, (TileEntityTimer) te));
			break;
		}
		return null;
	}

	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(ClientTickHandler.instance);
		MinecraftForge.EVENT_BUS.register(ServerTickHandler.instance);
	}

	@Override
	public void importSound(File configFolder) {
		SoundHelper.initSound(configFolder);
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new SoundHelper.SoundLoader());
		SoundHelper.importSound();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemName(ItemStack stack) {
		List<String> list = stack.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL);
		if (list.size() == 0)
			return stack.getItem().getUnlocalizedName();
		return list.get(0);
	}
}
