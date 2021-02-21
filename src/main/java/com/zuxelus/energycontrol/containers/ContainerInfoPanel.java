package com.zuxelus.energycontrol.containers;

import java.util.Objects;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotColor;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.containers.slots.SlotTouch;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

public class ContainerInfoPanel extends ContainerBase<TileEntityInfoPanel> {

	public ContainerInfoPanel(int windowId, PlayerInventory player, PacketBuffer data) {
		this(windowId, player, getTileEntity(player, data));
	}

	public ContainerInfoPanel(int windowId, PlayerInventory player, TileEntityInfoPanel panel) {
		super(panel, ModContainerTypes.info_panel.get(), windowId, ModItems.info_panel.get(), IWorldPosCallable.of(panel.getWorld(), panel.getPos()));
		addSlot(new SlotCard(panel, 0, 8, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void onSlotChanged() {
				if (panel.getWorld().isRemote)
					ContainerInfoPanel.this.detectAndSendChanges();
			};
		});
		addSlot(new SlotRange(panel, 1, 8, 24 + 18 * 2));
		addSlot(new SlotColor(panel, 2, 8, 24 + 18 * 3) {
			@SuppressWarnings("resource")
			@Override
			public void onSlotChanged() {
				if (panel.getWorld().isRemote)
					ContainerInfoPanel.this.detectAndSendChanges();
			};
		});
		addSlot(new SlotTouch(panel, 3, 8, 24 + 18 * 4));
		// inventory
		addPlayerInventorySlots(player, 201);
	}

	private static TileEntityInfoPanel getTileEntity(PlayerInventory player, PacketBuffer data) {
		Objects.requireNonNull(player, "Player cannot be null!");
		Objects.requireNonNull(data, "Data cannot be null!");
		TileEntity te = player.player.world.getTileEntity(data.readBlockPos());
		if (te instanceof TileEntityInfoPanel)
			return (TileEntityInfoPanel) te;
		throw new IllegalStateException("TileEntity is not correct! " + te);
	}
}
