package com.zuxelus.zlib.containers.slots;

import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlotArmor extends Slot {
	private final EquipmentSlotType armorType;

	public SlotArmor(PlayerInventory inventory, EquipmentSlotType armorType, int x, int y) {
		super(inventory, 36 + armorType.getIndex(), x, y);
		this.armorType = armorType;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack.isEmpty())
			return false;
		Item item = stack.getItem();
		if (item == null)
			return false;
		return item.isValidArmor(stack, armorType, ((PlayerInventory) inventory).player);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
		return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.ARMOR_SLOT_TEXTURES[armorType.getIndex()]);
	}
}
