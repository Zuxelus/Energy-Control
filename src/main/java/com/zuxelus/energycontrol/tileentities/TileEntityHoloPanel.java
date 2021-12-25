package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.containers.ContainerHoloPanel;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class TileEntityHoloPanel extends TileEntityInfoPanel {
	private static final byte SLOT_CARD = 0;
	private static final byte SLOT_UPGRADE_RANGE = 1;
	private static final byte SLOT_UPGRADE_POWER = 2;

	public TileEntityHoloPanel(BlockPos pos, BlockState state) {
		super(ModTileEntityTypes.holo_panel, pos, state);
	}

	public int getPower() {
		ItemStack stack = getStack(SLOT_UPGRADE_POWER);
		if (stack.isEmpty())
			return 1;
		return stack.getCount() + 1;
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD:
			return ItemCardMain.isCard(stack);
		case SLOT_UPGRADE_RANGE:
			return stack.getItem().equals(ModItems.upgrade_range);
		case SLOT_UPGRADE_POWER:
			return stack.getItem().equals(ModItems.advanced_circuit);
		default:
			return false;
		}
	}

	/*@Override
	@Environment(EnvType.CLIENT)
	public AABB getRenderBoundingBox() {
		if (screen == null)
			return new AABB(pos.offset(0, 0, 0), pos.offset(1, 1, 1));
		return new AABB(new BlockPos(screen.minX, screen.minY, screen.minZ), new BlockPos(screen.maxX + 1, screen.maxY + getPower(), screen.maxZ + 1));
	}*/

	// NamedScreenHandlerFactory
	@Override
	public ScreenHandler createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerHoloPanel(windowId, inventory, this);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText(ModItems.holo_panel.getTranslationKey());
	}
}
