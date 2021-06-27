package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.tileentities.TileEntitySeedLibrary;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotDischargeable;
import com.zuxelus.zlib.containers.slots.SlotFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;

public class ContainerSeedLibrary extends ContainerBase<TileEntitySeedLibrary> {

	public ContainerSeedLibrary(EntityPlayer player, TileEntitySeedLibrary te) {
		super(te);

		addSlotToContainer(new SlotDischargeable(te, TileEntitySeedLibrary.SLOT_DISCHARGER, 8, 110, TileEntitySeedLibrary.TIER));
		for (int i = 1; i < 9; i++)
			addSlotToContainer(new SlotFilter(te, i, 8 + i * 18, 110));
		addSlotToContainer(new SlotFilter(te, 9, 38, 16));
		// inventory
		addPlayerInventorySlots(player, 224);
	}

	@Override
	public ItemStack slotClick(int i, int j, ClickType k, EntityPlayer entityplayer) {
		if (i == 9) {
			// Clicked the "take a seed's type" slot.
			ItemStack seed = entityplayer.inventory.getItemStack();
			te.getGUIFilter().setCropFromSeed(seed);
			return ItemStack.EMPTY;
		}
		return super.slotClick(i, j, k, entityplayer);
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);

		te.updateGUIFilter();
		te.updateSeedCount();
	}
}
