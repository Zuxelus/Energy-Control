package com.zuxelus.energycontrol.crossmod.jei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.zuxelus.energycontrol.containers.ContainerKitAssembler;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * JEI's basic transfer puts one item per slot per craft, but Kit Assembler recipes need several
 * items in one slot (e.g. 3 iron ingots). This moves count x crafts items into each input slot
 * with ordinary inventory clicks, so the server validates every move.
 */
public class KitAssemblerTransferHandler implements IRecipeTransferHandler<ContainerKitAssembler, KitAssemblerRecipe> {
	private static final int FIRST_INPUT = 1; // menu slots 1-3 are the recipe inputs
	private static final int INPUTS = 3;

	private final IRecipeTransferHandlerHelper helper;

	public KitAssemblerTransferHandler(IRecipeTransferHandlerHelper helper) {
		this.helper = helper;
	}

	@Override
	public Class<? extends ContainerKitAssembler> getContainerClass() {
		return ContainerKitAssembler.class;
	}

	@Override
	public Optional<MenuType<ContainerKitAssembler>> getMenuType() {
		return Optional.of(ModContainerTypes.kit_assembler.get());
	}

	@Override
	public RecipeType<KitAssemblerRecipe> getRecipeType() {
		return KitAssemblerRecipeCategory.recipeType;
	}

	@Override
	@SuppressWarnings("removal") // the replacement is a default method that calls this one
	public IRecipeTransferError transferRecipe(ContainerKitAssembler menu, KitAssemblerRecipe recipe, IRecipeSlotsView slots, Player player, boolean maxTransfer, boolean doTransfer) {
		int[] counts = { recipe.count1, recipe.count2, recipe.count3 };
		List<IRecipeSlotView> inputs = slots.getSlotViews(RecipeIngredientRole.INPUT);
		List<Slot> inventory = getPlayerSlots(menu);

		// items the player can use: the inventory plus whatever is already in the input slots
		Map<Item, Integer> available = new HashMap<>();
		for (Slot slot : inventory)
			addStack(available, slot.getItem());
		for (int i = 0; i < INPUTS; i++)
			addStack(available, menu.getSlot(FIRST_INPUT + i).getItem());

		List<IRecipeSlotView> missing = new ArrayList<>();
		Item[] chosen = allocate(inputs, counts, 1, available, missing);
		if (chosen == null)
			return helper.createUserErrorForMissingSlots(Component.translatable("jei.tooltip.transfer.missing.materials"), missing);
		if (!doTransfer)
			return null;

		// shift: as many crafts as both the materials and the slot sizes allow
		int crafts = 1;
		if (maxTransfer) {
			int max = Integer.MAX_VALUE;
			for (int i = 0; i < INPUTS; i++)
				if (counts[i] > 0 && chosen[i] != null)
					max = Math.min(max, Math.min(menu.getSlot(FIRST_INPUT + i).getMaxStackSize(), chosen[i].getDefaultMaxStackSize()) / counts[i]);
			for (int k = max; k > 1; k--) {
				Item[] items = allocate(inputs, counts, k, available, new ArrayList<>());
				if (items != null) {
					chosen = items;
					crafts = k;
					break;
				}
			}
		}

		Minecraft mc = Minecraft.getInstance();
		for (int i = 0; i < INPUTS; i++) {
			Slot input = menu.getSlot(FIRST_INPUT + i);
			if (input.hasItem())
				click(mc, menu, input.index, 0, ClickType.QUICK_MOVE);
		}
		for (int i = 0; i < INPUTS; i++)
			if (chosen[i] != null)
				fillSlot(mc, menu, inventory, menu.getSlot(FIRST_INPUT + i), chosen[i], counts[i] * crafts);
		return null;
	}

	// Picks an item for every input so that all inputs fit into the available items at once. Returns null if something is missing.
	private static Item[] allocate(List<IRecipeSlotView> inputs, int[] counts, int crafts, Map<Item, Integer> available, List<IRecipeSlotView> missing) {
		Map<Item, Integer> left = new HashMap<>(available);
		Item[] chosen = new Item[INPUTS];
		boolean ok = true;
		for (int i = 0; i < INPUTS && i < inputs.size(); i++) {
			List<Item> items = getItems(inputs.get(i));
			if (items.isEmpty())
				continue;
			int need = counts[i] * crafts;
			Item best = null;
			for (Item item : items)
				if (left.getOrDefault(item, 0) >= need && (best == null || left.get(item) > left.get(best)))
					best = item;
			if (best == null) {
				missing.add(inputs.get(i));
				ok = false;
				continue;
			}
			left.put(best, left.get(best) - need);
			chosen[i] = best;
		}
		return ok ? chosen : null;
	}

	// Moves exactly 'amount' items of 'item' from the player inventory into 'target'.
	private static void fillSlot(Minecraft mc, ContainerKitAssembler menu, List<Slot> inventory, Slot target, Item item, int amount) {
		for (Slot source : inventory) {
			if (amount <= 0)
				return;
			if (!source.getItem().is(item))
				continue;
			click(mc, menu, source.index, 0, ClickType.PICKUP); // pick up the whole stack
			int before = target.getItem().getCount();
			if (menu.getCarried().getCount() <= amount)
				click(mc, menu, target.index, 0, ClickType.PICKUP); // place all of it
			else
				for (int j = 0; j < amount; j++)
					click(mc, menu, target.index, 1, ClickType.PICKUP); // place one at a time
			amount -= target.getItem().getCount() - before;
			if (!menu.getCarried().isEmpty())
				click(mc, menu, source.index, 0, ClickType.PICKUP); // put the rest back
		}
	}

	private static void click(Minecraft mc, ContainerKitAssembler menu, int slotId, int button, ClickType action) {
		mc.gameMode.handleInventoryMouseClick(menu.containerId, slotId, button, action, mc.player);
	}

	private static List<Slot> getPlayerSlots(ContainerKitAssembler menu) {
		List<Slot> list = new ArrayList<>();
		for (Slot slot : menu.slots)
			if (slot.container instanceof Inventory)
				list.add(slot);
		return list;
	}

	private static List<Item> getItems(IRecipeSlotView view) {
		List<Item> list = new ArrayList<>();
		view.getItemStacks().forEach(stack -> {
			if (!list.contains(stack.getItem()))
				list.add(stack.getItem());
		});
		return list;
	}

	private static void addStack(Map<Item, Integer> map, ItemStack stack) {
		if (!stack.isEmpty())
			map.merge(stack.getItem(), stack.getCount(), Integer::sum);
	}
}
