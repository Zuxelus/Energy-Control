package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.tileentities.TileEntitySeedLibrary;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;

public class ContainerSeedLibrary extends ContainerBase<TileEntitySeedLibrary> {

	public ContainerSeedLibrary(EntityPlayer player, TileEntitySeedLibrary te) {
		super(te);

		addSlotToContainer(new SlotFilter(te, TileEntitySeedLibrary.SLOT_DISCHARGER, 8, 110));
		for (int i = 1; i < 9; i++)
			addSlotToContainer(new SlotFilter(te, i, 8 + i * 18, 110));
		addSlotToContainer(new SlotFilter(te, 9, 38, 16));
		// inventory
		addPlayerInventorySlots(player, 224);
	}

	@Override
	public ItemStack slotClick(int i, int j, int k, EntityPlayer entityplayer) {
		if (i == 9) {
			// Clicked the "take a seed's type" slot.
			ItemStack seed = entityplayer.inventory.getItemStack();
			te.getGUIFilter().setCropFromSeed(seed);
			return null;
		}
		return super.slotClick(i, j, k, entityplayer);
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafter) {
		super.addCraftingToCrafters(crafter);

		te.updateGUIFilter();
		te.updateSeedCount();
	}
}
