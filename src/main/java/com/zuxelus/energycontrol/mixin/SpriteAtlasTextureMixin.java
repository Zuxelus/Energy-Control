package com.zuxelus.energycontrol.mixin;

import java.util.Set;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.zuxelus.energycontrol.containers.slots.SlotHandler;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin {

	@Inject(method = "stitch", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=extracting_frames", shift = At.Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onStitch(ResourceManager resourceManager, Stream<Identifier> idStream, Profiler profiler, int mipmapLevel, CallbackInfoReturnable<SpriteAtlasTexture.Data> cir, Set<Identifier> set) {
		SlotHandler.loadTextures((SpriteAtlasTexture) (Object) this, set);
	}
}