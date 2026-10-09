package com.zuxelus.energycontrol.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class AbstractBlockStateMixin {

	// since 1.20.5 using an item on a block goes through onUseWithItem before the block's own onUse
	@Inject(method = "useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
	protected void onUseWithItem(final ItemStack stack, final Level world, final Player player, final InteractionHand hand, final BlockHitResult hitResult, final CallbackInfoReturnable<InteractionResult> info) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemKitMain) {
			InteractionResult result = ((ItemKitMain) stack.getItem()).onItemUseFirst(world, player, hand, hitResult);
			if (result == InteractionResult.SUCCESS)
				info.setReturnValue(InteractionResult.SUCCESS);
		}
	}
}
