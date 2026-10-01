// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.network;
import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.menu.PortableMenu;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.*;
public record PortableDataPayload(int menuId,List<String> lines,List<Integer> barFills) implements CustomPacketPayload {
 public PortableDataPayload {
  if(lines.size()>32 || lines.size()!=barFills.size() || lines.stream().anyMatch(s->s.length()>256)
    || barFills.stream().anyMatch(n->n < -1 || n > 10000))throw new IllegalArgumentException("Invalid display rows");
  lines=List.copyOf(lines);barFills=List.copyOf(barFills);
 }
 public PortableDataPayload(int menuId,List<String> lines){this(menuId,lines,Collections.nCopies(lines.size(),-1));}
 public static final Type<PortableDataPayload> TYPE=new Type<>(EnergyControlPort.id("portable_data"));
 public static final StreamCodec<RegistryFriendlyByteBuf,PortableDataPayload> CODEC=StreamCodec.of((b,p)->{
  b.writeVarInt(p.menuId);b.writeVarInt(p.lines.size());for(int i=0;i<p.lines.size();i++){b.writeUtf(p.lines.get(i),256);b.writeVarInt(p.barFills.get(i));}
 },b->{int id=b.readVarInt(),n=b.readVarInt();if(n<0||n>32)throw new IllegalArgumentException("Too many lines");
  var result=new ArrayList<String>();var fills=new ArrayList<Integer>();for(int i=0;i<n;i++){result.add(b.readUtf(256));fills.add(b.readVarInt());}return new PortableDataPayload(id,result,fills);
 });
 @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(){if(dev.architectury.platform.Platform.getEnv()==net.fabricmc.api.EnvType.SERVER){NetworkManager.registerS2CPayloadType(TYPE,CODEC);return;}
  NetworkManager.registerReceiver(NetworkManager.Side.S2C,TYPE,CODEC,(p,c)->c.queue(()->{if(c.getPlayer().containerMenu instanceof PortableMenu m&&m.containerId==p.menuId){m.lines=p.lines;m.barFills=p.barFills;}}));}
}
