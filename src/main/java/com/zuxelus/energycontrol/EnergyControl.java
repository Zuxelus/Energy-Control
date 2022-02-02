package com.zuxelus.energycontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.ScreenManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EnergyControl.MODID)
public class EnergyControl {
	public static final String NAME = "Energy Control";
	public static final String MODID = "energycontrol";
	public static final String VERSION = "@VERSION@";

	public static EnergyControl INSTANCE;
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public ScreenManager screenManager = new ScreenManager();

	@OnlyIn(Dist.CLIENT)
	public List<String> availableAlarms; //on client
	@OnlyIn(Dist.CLIENT)
	public List<String> serverAllowedAlarms; // will be loaded from server
	public static Map<PlayerEntity, Boolean> altPressed = new HashMap<PlayerEntity, Boolean>();

	public static final ItemGroup ITEM_GROUP = new ItemGroup(MODID) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(ModItems.kit_energy.get());
		}
	};

	public EnergyControl() {
		INSTANCE = this;
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(SideProxy::commonSetup);
		ModItems.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
		ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
		ModItems.RECIPE_SERIALIZERS.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(ServerTickHandler.instance);
		ModLoadingContext.get().registerConfig(Type.COMMON, ConfigHandler.COMMON_CONFIG, "energycontrol-common.toml");
		CrossModLoader.init();
	}
}
