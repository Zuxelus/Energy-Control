package com.zuxelus.energycontrol;

import org.lwjgl.glfw.GLFW;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.renderers.InfoPanelBlockEntityRenderer;
import com.zuxelus.energycontrol.renderers.InfoPanelExtenderBERenderer;
import com.zuxelus.energycontrol.screen.CardHolderScreen;
import com.zuxelus.energycontrol.screen.InfoPanelScreen;
import com.zuxelus.energycontrol.screen.PortablePanelScreen;
import com.zuxelus.energycontrol.utils.SoundHelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class EnergyControlClient implements ClientModInitializer {
	public static KeyBinding modeSwitchKey;
	public static boolean modeSwitchKeyPressed;
	public static boolean altPressed;

	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.INSTANCE.register(ModItems.INFO_PANEL_BLOCK_ENTITY, InfoPanelBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(ModItems.INFO_PANEL_EXTENDER_BLOCK_ENTITY, InfoPanelExtenderBERenderer::new);
		ScreenRegistry.register(ModItems.INFO_PANEL_SCREEN_HANDLER, InfoPanelScreen::new);
		ScreenRegistry.register(ModItems.CARD_HOLDER_SCREEN_HANDLER, CardHolderScreen::new);
		ScreenRegistry.register(ModItems.PORTABLE_PANEL_SCREEN_HANDLER, PortablePanelScreen::new);
		ChannelHandler.initClient();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SoundHelper());

		modeSwitchKey = new KeyBinding("key.energycontrol.mode_switch_key", GLFW.GLFW_KEY_M, "Energy Control");
		KeyBindingHelper.registerKeyBinding(modeSwitchKey);
		ClientTickCallback.EVENT.register(client -> {
			boolean mode = modeSwitchKey.isPressed();
			boolean alt = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_ALT);
			if (modeSwitchKeyPressed != mode || altPressed != alt) {
				NetworkHelper.updateSeverKeys(client.player, mode, alt);
				modeSwitchKeyPressed = mode;
				altPressed = alt;
			}
		});

		FabricModelPredicateProviderRegistry.register(ModItems.NANO_BOW_ITEM, new Identifier("pull"), (stack, world, entity) -> {
			return entity == null ? 0.0F : entity.getActiveItem() != stack ? 0.0F : (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F; });
		FabricModelPredicateProviderRegistry.register(ModItems.NANO_BOW_ITEM, new Identifier("pulling"), (stack, world, entity) -> {
			return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F; });
	}
}
