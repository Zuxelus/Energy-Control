package com.zuxelus.energycontrol.screen.handlers;

import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class InfoPanelScreenHandler extends BaseScreenHandler<InfoPanelBlockEntity> {

	public InfoPanelScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
		this(syncId, playerInventory, getBlockEntity(playerInventory, buf));
	}

	public static InfoPanelBlockEntity getBlockEntity(PlayerInventory playerInventory, PacketByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		return (InfoPanelBlockEntity) playerInventory.player.world.getBlockEntity(pos);
	}

	public InfoPanelScreenHandler(int syncId, PlayerInventory playerInventory, InfoPanelBlockEntity panel) {
		super(ModItems.INFO_PANEL_SCREEN_HANDLER, syncId, playerInventory, panel);

		addSlot(new SlotFilter(panel, 0, 8, 24 + 18));
		// range upgrade
		addSlot(new SlotFilter(panel, 1, 8, 24 + 18 * 2));
		// color upgrade
		addSlot(new SlotFilter(panel, 2, 8, 24 + 18 * 3));
		// touch upgrade
		addSlot(new SlotFilter(panel, 3, 8, 24 + 18 * 4));
		// inventory
		addPlayerInventorySlots(playerInventory.player, 201);
	}
}
