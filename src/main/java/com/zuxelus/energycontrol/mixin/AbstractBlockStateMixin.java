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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

	@Inject(method = "onUse(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
	protected void onUse(final World world, final PlayerEntity player, final Hand hand, final BlockHitResult hitResult, final CallbackInfoReturnable<ActionResult> info) {
		ItemStack stack = player.getMainHandStack();
		if (!stack.isEmpty() && stack.getItem() instanceof ItemKitMain) {
			ActionResult result = ((ItemKitMain) stack.getItem()).onItemUseFirst(world, player, hand);
			if (result == ActionResult.SUCCESS)
				info.setReturnValue(result);
		}
	}
}