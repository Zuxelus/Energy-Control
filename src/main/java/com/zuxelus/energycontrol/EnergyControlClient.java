package com.zuxelus.energycontrol;

import org.lwjgl.glfw.GLFW;

import com.zuxelus.energycontrol.containers.*;
import com.zuxelus.energycontrol.gui.*;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.renderers.InfoPanelBlockEntityRenderer;
import com.zuxelus.energycontrol.renderers.InfoPanelExtenderBERenderer;
import com.zuxelus.energycontrol.utils.SoundHelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class EnergyControlClient implements ClientModInitializer {
	public static FabricKeyBinding modeSwitchKey;
	public static boolean modeSwitchKeyPressed;
	public static boolean altPressed;

	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.INSTANCE.register(ModItems.INFO_PANEL_BLOCK_ENTITY, InfoPanelBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(ModItems.INFO_PANEL_EXTENDER_BLOCK_ENTITY, InfoPanelExtenderBERenderer::new);
		ScreenProviderRegistry.INSTANCE.<InfoPanelContainer>registerFactory(ModItems.INFO_PANEL,
				(container) -> new InfoPanelScreen(container, MinecraftClient.getInstance().player.inventory));
		ScreenProviderRegistry.INSTANCE.<CardHolderContainer>registerFactory(ModItems.CARD_HOLDER,
				(container) -> new CardHolderScreen(container, MinecraftClient.getInstance().player));
		ScreenProviderRegistry.INSTANCE.<PortablePanelContainer>registerFactory(ModItems.PORTABLE_PANEL,
				(container) -> new PortablePanelScreen(container, MinecraftClient.getInstance().player));

		ChannelHandler.initClient();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SoundHelper());
		KeyBindingRegistry.INSTANCE.addCategory("Energy Control");
		modeSwitchKey = FabricKeyBinding.Builder.create(new Identifier(EnergyControl.MODID, "mode_switch_key"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "Energy Control").build();
		KeyBindingRegistry.INSTANCE.register(modeSwitchKey);

		ClientTickCallback.EVENT.register(client -> {
			boolean mode = modeSwitchKey.isPressed();
			boolean alt = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_ALT);
			if (modeSwitchKeyPressed != mode || altPressed != alt) {
				NetworkHelper.updateSeverKeys(client.player, mode, alt);
				modeSwitchKeyPressed = mode;
				altPressed = alt;
			}
		});
	}
}
