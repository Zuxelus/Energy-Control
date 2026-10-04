package com.zuxelus.energycontrol.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.zuxelus.energycontrol.items.kits.ItemKitMain;

import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

	// since 1.20.5 using an item on a block goes through onUseWithItem before the block's own onUse
	@Inject(method = "onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ItemActionResult;", at = @At("HEAD"), cancellable = true)
	protected void onUseWithItem(final ItemStack stack, final World world, final PlayerEntity player, final Hand hand, final BlockHitResult hitResult, final CallbackInfoReturnable<ItemActionResult> info) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemKitMain) {
			ActionResult result = ((ItemKitMain) stack.getItem()).onItemUseFirst(world, player, hand);
			if (result == ActionResult.SUCCESS)
				info.setReturnValue(ItemActionResult.SUCCESS);
		}
	}
}
