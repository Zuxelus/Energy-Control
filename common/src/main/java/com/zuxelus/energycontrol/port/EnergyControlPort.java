// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port;

import com.zuxelus.energycontrol.port.block.PanelBlock;
import com.zuxelus.energycontrol.port.block.PanelBlockEntity;
import com.zuxelus.energycontrol.port.card.CardItem;
import com.zuxelus.energycontrol.port.energy.EnergyProbe;
import com.zuxelus.energycontrol.port.menu.PanelMenu;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import java.util.List;

/** Independent 1.21.1 port. Original source and assets: Zuxelus and contributors. */
public final class EnergyControlPort {
    public static final String ID = "energycontrol";
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ID, Registries.BLOCK);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ID, Registries.ITEM);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(ID, Registries.BLOCK_ENTITY_TYPE);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ID, Registries.MENU);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(ID, Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<PanelBlock> BASIC = panel("info_panel", false, false);
    public static final RegistrySupplier<PanelBlock> ADVANCED = panel("info_panel_advanced", true, false);
    public static final RegistrySupplier<PanelBlock> EXTENDER = panel("info_panel_extender", false, true);
    public static final RegistrySupplier<PanelBlock> ADVANCED_EXTENDER = panel("info_panel_advanced_extender", true, true);
    public static final RegistrySupplier<PanelBlock> HOLO=panel("holo_panel",true,false,true);
    public static final RegistrySupplier<PanelBlock> HOLO_EXTENDER=panel("holo_panel_extender",true,true,true);
    public static final RegistrySupplier<Item> TEXT = ITEMS.register("card_text", () -> new CardItem(CardItem.Kind.TEXT));
    public static final RegistrySupplier<Item> ENERGY = ITEMS.register("card_energy", () -> new CardItem(CardItem.Kind.ENERGY));
    public static final RegistrySupplier<Item> MACHINE = ITEMS.register("card_machine", () -> new CardItem(CardItem.Kind.MACHINE));
    public static final RegistrySupplier<Item> TIME = ITEMS.register("card_time", () -> new CardItem(CardItem.Kind.TIME));
    public static final RegistrySupplier<Item> REDSTONE = ITEMS.register("card_redstone", () -> new CardItem(CardItem.Kind.REDSTONE));
    public static final RegistrySupplier<Item> FLUID = ITEMS.register("card_liquid", () -> new CardItem(CardItem.Kind.FLUID));
    public static final RegistrySupplier<Item> ENERGY_ARRAY = ITEMS.register("card_energy_array", () -> new CardItem(CardItem.Kind.ENERGY_ARRAY));
    public static final RegistrySupplier<Item> FLUID_ARRAY = ITEMS.register("card_liquid_array", () -> new CardItem(CardItem.Kind.FLUID_ARRAY));
    public static final RegistrySupplier<Item> RANGE = ITEMS.register("upgrade_range", () -> new com.zuxelus.energycontrol.port.card.UpgradeItem(com.zuxelus.energycontrol.port.card.UpgradeItem.Kind.RANGE));
    public static final RegistrySupplier<Item> CAPACITY = ITEMS.register("upgrade_capacity", () -> new com.zuxelus.energycontrol.port.card.UpgradeItem(com.zuxelus.energycontrol.port.card.UpgradeItem.Kind.CAPACITY));
    public static final RegistrySupplier<Item> PRECISION = ITEMS.register("upgrade_precision", () -> new com.zuxelus.energycontrol.port.card.UpgradeItem(com.zuxelus.energycontrol.port.card.UpgradeItem.Kind.PRECISION));
    public static final RegistrySupplier<Item> PORTABLE = ITEMS.register("portable_panel",com.zuxelus.energycontrol.port.card.PortablePanelItem::new);
    public static final RegistrySupplier<BlockEntityType<PanelBlockEntity>> PANEL_ENTITY = ENTITIES.register("info_panel",
            () -> BlockEntityType.Builder.of(PanelBlockEntity::new, BASIC.get(), ADVANCED.get(), EXTENDER.get(), ADVANCED_EXTENDER.get(),HOLO.get(),HOLO_EXTENDER.get()).build(null));
    public static final RegistrySupplier<MenuType<PanelMenu>> PANEL_MENU = MENUS.register("info_panel", () -> MenuRegistry.ofExtended(PanelMenu::fromNetwork));
    public static final RegistrySupplier<MenuType<com.zuxelus.energycontrol.port.menu.PortableMenu>> PORTABLE_MENU = MENUS.register("portable_panel",()->MenuRegistry.ofExtended(com.zuxelus.energycontrol.port.menu.PortableMenu::fromNetwork));
    public static final RegistrySupplier<CreativeModeTab> TAB = TABS.register("panels", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.literal("Energy Control — port preview")).icon(() -> new ItemStack(BASIC.get()))
            .displayItems((parameters, output) -> {
                for (var block : List.of(BASIC, ADVANCED, EXTENDER, ADVANCED_EXTENDER,HOLO,HOLO_EXTENDER)) output.accept(block.get());
                for (var item : List.of(TEXT, ENERGY, TIME, REDSTONE, FLUID, ENERGY_ARRAY, FLUID_ARRAY, MACHINE, RANGE, CAPACITY, PRECISION, PORTABLE)) output.accept(item.get());
            }).build());
    public static com.zuxelus.energycontrol.port.fluid.FluidProbe fluidProbe = (level,pos,side) -> java.util.Optional.empty();
    public static com.zuxelus.energycontrol.port.energy.MachineProbe machineProbe = (level,pos,side) -> java.util.Optional.empty();
    public static EnergyProbe energyProbe = (level, pos, side) -> java.util.Optional.empty();

    private static RegistrySupplier<PanelBlock> panel(String name, boolean advanced, boolean extender) {
        return panel(name,advanced,extender,false);
    }
    private static RegistrySupplier<PanelBlock> panel(String name,boolean advanced,boolean extender,boolean holo){
        var block = BLOCKS.register(name, () -> new PanelBlock(advanced, extender,holo));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ID, path); }
    public static void init(EnergyProbe probe) {
        energyProbe = probe;
        BLOCKS.register(); ITEMS.register(); ENTITIES.register(); MENUS.register(); TABS.register();
        com.zuxelus.energycontrol.port.network.PanelEditPayload.register();
        com.zuxelus.energycontrol.port.network.PortableDataPayload.register();
    }
}
