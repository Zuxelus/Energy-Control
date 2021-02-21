package com.zuxelus.energycontrol.containers;

import java.util.Objects;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

public class ContainerAdvancedInfoPanel extends ContainerBase<TileEntityAdvancedInfoPanel> {

	public ContainerAdvancedInfoPanel(int windowId, PlayerInventory player, PacketBuffer data) {
		this(windowId, player, getTileEntity(player, data));
	}

	public ContainerAdvancedInfoPanel(int windowId, PlayerInventory player, TileEntityAdvancedInfoPanel panel) {
		super(panel, ModContainerTypes.info_panel_advanced.get(), windowId, ModItems.info_panel_advanced.get(), IWorldPosCallable.of(panel.getWorld(), panel.getPos()));
		addSlot(new SlotCard(panel, 0, 8, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void onSlotChanged() {
				if (panel.getWorld().isRemote)
					ContainerAdvancedInfoPanel.this.detectAndSendChanges();
			};
		});
		addSlot(new SlotCard(panel, 1, 8 + 18, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void onSlotChanged() {
				if (panel.getWorld().isRemote)
					ContainerAdvancedInfoPanel.this.detectAndSendChanges();
			};
		});
		addSlot(new SlotCard(panel, 2, 8 + 36, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void onSlotChanged() {
				if (panel.getWorld().isRemote)
					ContainerAdvancedInfoPanel.this.detectAndSendChanges();
			};
		});
		addSlot(new SlotRange(panel, 3, 8 + 54, 24 + 18));
		// inventory
		addPlayerInventorySlots(player, 223);
	}

	private static TileEntityAdvancedInfoPanel getTileEntity(PlayerInventory player, PacketBuffer data) {
		Objects.requireNonNull(player, "Player cannot be null!");
		Objects.requireNonNull(data, "Data cannot be null!");
		TileEntity te = player.player.world.getTileEntity(data.readBlockPos());
		if (te instanceof TileEntityAdvancedInfoPanel)
			return (TileEntityAdvancedInfoPanel) te;
		throw new IllegalStateException("TileEntity is not correct! " + te);
	}
}
