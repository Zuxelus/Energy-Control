// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.network;
import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.menu.PortableMenu;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.*;
public record PortableDataPayload(int menuId,List<String> lines) implements CustomPacketPayload {
 public static final Type<PortableDataPayload> TYPE=new Type<>(EnergyControlPort.id("portable_data"));
 public static final StreamCodec<RegistryFriendlyByteBuf,PortableDataPayload> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.menuId);b.writeVarInt(p.lines.size());for(var s:p.lines)b.writeUtf(s,256);},b->{int id=b.readVarInt(),n=b.readVarInt();if(n<0||n>32)throw new IllegalArgumentException("Too many lines");var result=new ArrayList<String>();for(int i=0;i<n;i++)result.add(b.readUtf(256));return new PortableDataPayload(id,List.copyOf(result));});
 @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(){if(dev.architectury.platform.Platform.getEnv()==net.fabricmc.api.EnvType.SERVER){NetworkManager.registerS2CPayloadType(TYPE,CODEC);return;}NetworkManager.registerReceiver(NetworkManager.Side.S2C,TYPE,CODEC,(p,c)->c.queue(()->{if(c.getPlayer().containerMenu instanceof PortableMenu m&&m.containerId==p.menuId)m.lines=p.lines;}));}
}
