package com.zuxelus.energycontrol.crossmod.rei;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.GuiKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Collections;
import java.util.List;

@REIPluginClient
public class CrossREI implements REIClientPlugin {

    public static final CategoryIdentifier<KitAssemblerDisplay> KIT_ASSEMBLER = CategoryIdentifier.of(new ResourceLocation(EnergyControl.MODID, "kit_assembler"));

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.addWorkstations(KIT_ASSEMBLER, EntryIngredients.of(ModItems.kit_assembler.get()));
        registry.add(new KitAssemblerCategory());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        RecipeManager RECIPES = Minecraft.getInstance().level.getRecipeManager();
        RECIPES.getAllRecipesFor(KitAssemblerRecipeType.TYPE).forEach(recipe -> registry.add(new KitAssemblerDisplay(recipe)));
        addDescription(registry, ModItems.white_lamp.get().asItem(), "ec.jei.blockLightWhite");
        addDescription(registry, ModItems.orange_lamp.get().asItem(), "ec.jei.blockLightOrange");
        addDescription(registry, ModItems.howler_alarm.get().asItem(), "ec.jei.blockHowlerAlarm");
        addDescription(registry, ModItems.industrial_alarm.get().asItem(), "ec.jei.blockIndustrialAlarm");
        addDescription(registry, ModItems.thermal_monitor.get().asItem(), "ec.jei.blockThermalMonitor");
        addDescription(registry, ModItems.info_panel.get().asItem(), "ec.jei.blockInfoPanel");
        addDescription(registry, ModItems.info_panel_extender.get().asItem(), "ec.jei.blockInfoPanelExtender");
        addDescription(registry, ModItems.info_panel_advanced.get().asItem(), "ec.jei.blockInfoPanelAdvanced");
        addDescription(registry, ModItems.info_panel_advanced_extender.get().asItem(), "ec.jei.blockInfoPanelAdvancedExtender");
        addDescription(registry, ModItems.holo_panel.get().asItem(), "ec.jei.blockHoloPanel");

        addDescription(registry, ModItems.kit_assembler.get().asItem(), "ec.jei.blockKitAssembler");

        addDescription(registry, ModItems.upgrade_color.get(), "ec.jei.upgradeColor");
        addDescription(registry, ModItems.upgrade_range.get(), "ec.jei.upgradeRange");
        addDescription(registry, ModItems.upgrade_touch.get(), "ec.jei.upgradeTouch");

        addDescription(registry, ModItems.kit_app_eng, "ec.jei.kitAppEng");
        addDescription(registry, ModItems.kit_big_reactors, "ec.jei.kitBigReactors");
        addDescription(registry, ModItems.kit_energy.get(), "ec.jei.kitEnergy");
        addDescription(registry, ModItems.kit_liquid.get(), "ec.jei.kitLiquid");
        addDescription(registry, ModItems.kit_liquid_advanced.get(), "ec.jei.kitLiquidAdv");
        addDescription(registry, ModItems.kit_toggle.get(), "ec.jei.kitToggle");

        List<ItemStack> cards = new ObjectArrayList<>();
        cards.add(ModItems.card_energy.get().getDefaultInstance());
        cards.add(ModItems.card_inventory.get().getDefaultInstance());
        cards.add(ModItems.card_liquid_advanced.get().getDefaultInstance());
        cards.add(ModItems.card_liquid.get().getDefaultInstance());
        cards.add(ModItems.card_redstone.get().getDefaultInstance());
        cards.add(ModItems.card_toggle.get().getDefaultInstance());
        DefaultInformationDisplay cardsDisplay = DefaultInformationDisplay.createFromEntries(EntryIngredients.ofItemStacks(cards), Component.empty());
        cardsDisplay.line(Component.translatable("ec.jei.cards"));
        registry.add(cardsDisplay);
    }

    private static void addDescription(DisplayRegistry registry, Item item, String information) {
        if (item != null) {
            DefaultInformationDisplay info = DefaultInformationDisplay.createFromEntry(EntryStacks.of(item),
                    item.getDescription());
            info.line(Component.translatable(information));
            registry.add(info);
        }
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(87, 35, 22, 15), GuiKitAssembler.class, KIT_ASSEMBLER);
    }

    public static class KitAssemblerCategory implements DisplayCategory<KitAssemblerDisplay> {

        @Override
        public CategoryIdentifier<? extends KitAssemblerDisplay> getCategoryIdentifier() {
            return KIT_ASSEMBLER;
        }

        @Override
        public List<Widget> setupDisplay(KitAssemblerDisplay display, Rectangle bounds) {
            List<Widget> widgets = new ObjectArrayList<>();
            widgets.add(Widgets.createRecipeBase(bounds));
            List<ItemStack> inputA = convertIngredientToStacks(display.recipe().input1, display.recipe().count1);
            List<ItemStack> inputB = convertIngredientToStacks(display.recipe().input2, display.recipe().count2);
            List<ItemStack> inputC = convertIngredientToStacks(display.recipe().input3, display.recipe().count3);

            widgets.add(Widgets.createArrow(new Point(bounds.getMinX() + 30, bounds.getCenterY() - 9)).animationDurationMS(2000));
            widgets.add(Widgets.createSlot(new Point(bounds.getMinX() + 7, bounds.getMinY() + 7)).entries(EntryIngredients.ofItemStacks(inputA)).markInput());
            widgets.add(Widgets.createSlot(new Point(bounds.getMinX() + 7, bounds.getMinY() + 7 + 18)).entries(EntryIngredients.ofItemStacks(inputB)).markInput());
            widgets.add(Widgets.createSlot(new Point(bounds.getMinX() + 7, bounds.getMinY() + 7 + 36)).entries(EntryIngredients.ofItemStacks(inputC)).markInput());
            widgets.add(Widgets.createResultSlotBackground(new Point(bounds.getMinX() + 60 + 5, bounds.getCenterY() - 8)));
            widgets.add(Widgets.createSlot(new Point(bounds.getMinX() + 60 + 5, bounds.getCenterY() - 8)).entries(EntryIngredients.of(display.recipe().output)).markOutput().disableBackground());
            return widgets;
        }

        @Override
        public int getDisplayWidth(KitAssemblerDisplay display) {
            return 93;
        }

        @Override
        public Component getTitle() {
            return ModItems.kit_assembler.get().getName();
        }

        @Override
        public Renderer getIcon() {
            return EntryStacks.of(ModItems.kit_assembler.get());
        }

        private List<ItemStack> convertIngredientToStacks(Ingredient ingredient, int count) {
            List<ItemStack> list = new ObjectArrayList<>();
            for (ItemStack stack : ingredient.getItems()) {
                if (count == 1) {
                    list.add(stack);
                } else {
                    ItemStack copy = stack.copy();
                    copy.setCount(count);
                    list.add(copy);
                }
            }
            return list;
        }
    }

    public static class KitAssemblerDisplay extends BasicDisplay {

        KitAssemblerRecipe RECIPE;

        public KitAssemblerDisplay(KitAssemblerRecipe recipe) {
            super(EntryIngredients.ofIngredients(recipe.getIngredients().stream().toList()),
                    Collections.singletonList(EntryIngredients.of(recipe.output)));
            this.RECIPE = recipe;
        }

        public KitAssemblerRecipe recipe() {
            return this.RECIPE;
        }

        @Override
        public CategoryIdentifier<?> getCategoryIdentifier() {
            return KIT_ASSEMBLER;
        }
    }
}
