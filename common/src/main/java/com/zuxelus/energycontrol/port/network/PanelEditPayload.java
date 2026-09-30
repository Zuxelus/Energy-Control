// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.network;

import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.menu.PanelMenu;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Only edits a card in the player's currently open and distance-validated panel. */
public record PanelEditPayload(int menuId, int slot, String text) implements CustomPacketPayload {
    public static final Type<PanelEditPayload> TYPE = new Type<>(EnergyControlPort.id("edit_panel_text"));
    public static final StreamCodec<RegistryFriendlyByteBuf,PanelEditPayload> CODEC = StreamCodec.of(
            (buf,payload) -> { buf.writeVarInt(payload.menuId); buf.writeVarInt(payload.slot); buf.writeUtf(payload.text,512); },
            buf -> new PanelEditPayload(buf.readVarInt(),buf.readVarInt(),buf.readUtf(512)));
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,TYPE,CODEC,(payload,context) -> context.queue(() -> {
            var player = context.getPlayer();
            if(player.containerMenu instanceof PanelMenu menu && menu.containerId == payload.menuId)
                menu.editText(player,payload.slot,payload.text);
        }));
    }
}
