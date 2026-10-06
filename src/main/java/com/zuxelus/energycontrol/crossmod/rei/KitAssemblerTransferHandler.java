package com.zuxelus.energycontrol.crossmod.rei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.containers.ContainerKitAssembler;

import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * REI's simple transfer puts one item per slot per craft, but Kit Assembler recipes need several
 * items in one slot (e.g. 3 iron ingots). This moves count x crafts items into each input slot
 * with ordinary inventory clicks, so the server validates every move.
 */
@Environment(EnvType.CLIENT)
public class KitAssemblerTransferHandler implements TransferHandler {
	private static final int FIRST_INPUT = 1; // menu slots 1-3 are the recipe inputs
	private static final int INPUTS = 3;

	@Override
	public Result handle(Context context) {
		if (!(context.getDisplay() instanceof KitAssemblerDisplay display) || !(context.getMenu() instanceof ContainerKitAssembler menu))
			return Result.createNotApplicable();

		int[] counts = { display.count1, display.count2, display.count3 };
		List<EntryIngredient> inputs = display.getInputEntries();
		List<Slot> inventory = getPlayerSlots(menu);

		// items the player can use: the inventory plus whatever is already in the input slots
		Map<Item, Integer> available = new HashMap<>();
		for (Slot slot : inventory)
			addStack(available, slot.getItem());
		for (int i = 0; i < INPUTS; i++)
			addStack(available, menu.getSlot(FIRST_INPUT + i).getItem());

		List<EntryIngredient> missing = new ArrayList<>();
		Item[] chosen = allocate(inputs, counts, 1, available, missing);
		if (chosen == null)
			return Result.createFailed(Component.translatable("error.rei.not.enough.materials")).tooltipMissing(missing);
		if (!context.isActuallyCrafting())
			return Result.createSuccessful();

		// shift: as many crafts as both the materials and the slot sizes allow
		int crafts = 1;
		if (context.isStackedCrafting()) {
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

		Minecraft mc = context.getMinecraft();
		mc.gui.setScreen(context.getContainerScreen());
		for (int i = 0; i < INPUTS; i++) {
			Slot input = menu.getSlot(FIRST_INPUT + i);
			if (input.hasItem())
				click(mc, menu, input.index, 0, ContainerInput.QUICK_MOVE);
		}
		for (int i = 0; i < INPUTS; i++)
			if (chosen[i] != null)
				fillSlot(mc, menu, inventory, menu.getSlot(FIRST_INPUT + i), chosen[i], counts[i] * crafts);
		return Result.createSuccessful();
	}

	// Picks an item for every input so that all inputs fit into the available items at once. Returns null if something is missing.
	private static Item[] allocate(List<EntryIngredient> inputs, int[] counts, int crafts, Map<Item, Integer> available, List<EntryIngredient> missing) {
		Map<Item, Integer> left = new HashMap<>(available);
		Item[] chosen = new Item[INPUTS];
		boolean ok = true;
		for (int i = 0; i < INPUTS && i < inputs.size(); i++) {
			if (inputs.get(i).isEmpty())
				continue;
			int need = counts[i] * crafts;
			Item best = null;
			for (Item item : getItems(inputs.get(i)))
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
			click(mc, menu, source.index, 0, ContainerInput.PICKUP); // pick up the whole stack
			int before = target.getItem().getCount();
			if (menu.getCarried().getCount() <= amount)
				click(mc, menu, target.index, 0, ContainerInput.PICKUP); // place all of it
			else
				for (int j = 0; j < amount; j++)
					click(mc, menu, target.index, 1, ContainerInput.PICKUP); // place one at a time
			amount -= target.getItem().getCount() - before;
			if (!menu.getCarried().isEmpty())
				click(mc, menu, source.index, 0, ContainerInput.PICKUP); // put the rest back
		}
	}

	private static void click(Minecraft mc, ContainerKitAssembler menu, int slotId, int button, ContainerInput action) {
		mc.gameMode.handleContainerInput(menu.containerId, slotId, button, action, mc.player);
	}

	private static List<Slot> getPlayerSlots(ContainerKitAssembler menu) {
		List<Slot> list = new ArrayList<>();
		for (Slot slot : menu.slots)
			if (slot.container instanceof Inventory)
				list.add(slot);
		return list;
	}

	private static List<Item> getItems(EntryIngredient ingredient) {
		List<Item> list = new ArrayList<>();
		for (EntryStack<?> entry : ingredient)
			if (entry.getType() == VanillaEntryTypes.ITEM) {
				Item item = ((ItemStack) entry.getValue()).getItem();
				if (!list.contains(item))
					list.add(item);
			}
		return list;
	}

	private static void addStack(Map<Item, Integer> map, ItemStack stack) {
		if (!stack.isEmpty())
			map.merge(stack.getItem(), stack.getCount(), Integer::sum);
	}
}
