// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.*;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.card.*;
import com.zuxelus.energycontrol.port.menu.PortableInventory;
import dev.architectury.event.events.common.*;
import net.minecraft.core.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import java.util.*;
public final class StorageQaServer {
 public static final BlockPos CORE=new BlockPos(0,1,0),HOLO=new BlockPos(-4,1,0),SINGLE=new BlockPos(4,1,0),ARRAY=new BlockPos(6,1,0);
 private static int ticks;private static String previous;private static final Set<UUID> players=new HashSet<>();private static final Map<UUID,Integer> ages=new HashMap<>();
 public static BlockPos tank(int i){return new BlockPos(4+i*2,0,-2);}
 public static ItemStack bound(Item item,int count){var stack=new ItemStack(item);var data=CardItem.data(stack);for(int i=0;i<count;i++)CardTargets.bind(data,new CardTargets.Target("minecraft:overworld",(i==5?new BlockPos(100,0,0):tank(i)).asLong(),2),count>1);data.putBoolean("hidePercent",true);CardItem.update(stack,data);return stack;}
 public static void init(){LifecycleEvent.SERVER_STARTED.register(StorageQaServer::setup);TickEvent.SERVER_POST.register(StorageQaServer::tick);}
 private static void setup(MinecraftServer s){
  QaServer.log("RUN_START storage-server "+java.time.Instant.now());QaServer.log("NORMAL_BLOCK_BREAK no protection event registered");var l=s.overworld();
  for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)l.setChunkForced(x,z,true);l.setChunkForced(6,0,true);
  boolean existing=l.getBlockEntity(CORE) instanceof PanelBlockEntity;
  if(existing){var p=(PanelBlockEntity)l.getBlockEntity(CORE);QaServer.check(p.upgrades(UpgradeItem.Kind.RANGE)==1&&p.upgrades(UpgradeItem.Kind.CAPACITY)==1&&p.upgrades(UpgradeItem.Kind.PRECISION)==2,"storage upgrades persist on disk");QaServer.check(CardTargets.read(CardItem.data(p.getItem(1))).size()==6,"array targets persist on disk");}
  for(int x=-8;x<16;x++)for(int z=-6;z<6;z++)l.setBlockAndUpdate(new BlockPos(x,-1,z),Blocks.SMOOTH_STONE.defaultBlockState());
  for(int i=0;i<5;i++)l.setBlockAndUpdate(tank(i),Blocks.CHEST.defaultBlockState());l.setBlockAndUpdate(new BlockPos(100,0,0),Blocks.CHEST.defaultBlockState());
  if(!existing){
   for(int x=0;x<3;x++)for(int y=0;y<2;y++)l.setBlockAndUpdate(CORE.offset(x,y,0),(x==0&&y==0?EnergyControlPort.ADVANCED:EnergyControlPort.ADVANCED_EXTENDER).get().defaultBlockState().setValue(PanelBlock.FACING,Direction.NORTH));
   var p=(PanelBlockEntity)l.getBlockEntity(CORE);var text=new ItemStack(EnergyControlPort.TEXT.get());var d=CardItem.data(text);d.putString("text","STORAGE STAGE\nLive arrays + upgrades");CardItem.update(text,d);p.setItem(0,text);p.setItem(1,bound(EnergyControlPort.FLUID_ARRAY.get(),6));p.setItem(2,bound(EnergyControlPort.ENERGY_ARRAY.get(),6));
   l.setBlockAndUpdate(SINGLE,EnergyControlPort.BASIC.get().defaultBlockState().setValue(PanelBlock.FACING,Direction.NORTH));((PanelBlockEntity)l.getBlockEntity(SINGLE)).setItem(0,bound(EnergyControlPort.FLUID.get(),1));
   l.setBlockAndUpdate(ARRAY,EnergyControlPort.BASIC.get().defaultBlockState().setValue(PanelBlock.FACING,Direction.NORTH));((PanelBlockEntity)l.getBlockEntity(ARRAY)).setItem(0,bound(EnergyControlPort.ENERGY_ARRAY.get(),2));
   for(int x=0;x<2;x++)for(int y=0;y<2;y++)l.setBlockAndUpdate(HOLO.offset(x,y,0),(x==0&&y==0?EnergyControlPort.HOLO:EnergyControlPort.HOLO_EXTENDER).get().defaultBlockState().setValue(PanelBlock.FACING,Direction.NORTH));
   var h=(PanelBlockEntity)l.getBlockEntity(HOLO);h.setItem(0,bound(EnergyControlPort.FLUID.get(),1));var t=new ItemStack(EnergyControlPort.TEXT.get());d=CardItem.data(t);d.putString("text","HOLOGRAPHIC\nTransparent 2 x 2");CardItem.update(t,d);h.setItem(1,t);
  }
  var p=(PanelBlockEntity)l.getBlockEntity(CORE);for(int i=8;i<11;i++)p.setItem(i,ItemStack.EMPTY);
  l.setDayTime(6000);l.setWeatherParameters(100000,0,false,false);
 }
 private static void tick(MinecraftServer s){
  ticks++;var l=s.overworld();var p=(PanelBlockEntity)l.getBlockEntity(CORE);
  for(var player:s.getPlayerList().getPlayers())if(players.add(player.getUUID())){
   player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);player.teleportTo(l,2,.5,-4,Set.of(),0,0);player.getAbilities().mayfly=true;player.getAbilities().flying=true;player.onUpdateAbilities();
   if(!(player.getInventory().getItem(0).getItem() instanceof PortablePanelItem)){
    var portable=new ItemStack(EnergyControlPort.PORTABLE.get());var inv=new PortableInventory(portable);inv.setItem(0,bound(EnergyControlPort.FLUID_ARRAY.get(),2));inv.setItem(1,new ItemStack(EnergyControlPort.RANGE.get()));inv.setItem(2,new ItemStack(EnergyControlPort.CAPACITY.get()));inv.setItem(3,new ItemStack(EnergyControlPort.PRECISION.get(),2));player.getInventory().setItem(0,portable);
   }else QaServer.check(CardItem.data(new PortableInventory(player.getInventory().getItem(0)).getItem(0)).getString("title").equals("PORTABLE_V3"),"portable data survives player disk reload");
   player.getInventory().setItem(1,new ItemStack(EnergyControlPort.ENERGY_ARRAY.get()));player.getInventory().selected=0;QaServer.log("STORAGE_PLAYER "+player.getGameProfile().getName());
  }
  for(var player:s.getPlayerList().getPlayers())if(ages.merge(player.getUUID(),1,Integer::sum)==365)player.teleportTo(l,-3,.5,-4,Set.of(),0,0);
  if(ticks==80){QaServer.check(p.lines().stream().anyMatch(v->v.contains("2 inactive targets")),"capacity baseline limits active array targets");QaServer.check(((PanelBlockEntity)l.getBlockEntity(SINGLE)).lines().stream().anyMatch(v->v.contains("mB")),"real platform fluid lookup");previous=p.lines().toString();}
  if(ticks==100)p.setItem(9,new ItemStack(EnergyControlPort.CAPACITY.get()));
  if(ticks==140){QaServer.check(p.lines().stream().noneMatch(v->v.contains("inactive"))&&p.lines().stream().anyMatch(v->v.contains("Out of range")),"capacity upgrade enables far target but range still enforced");p.setItem(8,new ItemStack(EnergyControlPort.RANGE.get()));}
  if(ticks==180){QaServer.check(p.lines().stream().noneMatch(v->v.contains("Out of range")),"range upgrade enables 100-block provider");p.setItem(10,new ItemStack(EnergyControlPort.PRECISION.get(),2));}
  if(ticks==220){QaServer.check(p.lines().stream().anyMatch(v->v.matches(".*[0-9]\\.[0-9]{2} /.*mB")),"precision upgrade changes actual fluid formatting");QaServer.check(!previous.equals(p.lines().toString()),"live arrays refresh");var h=(PanelBlockEntity)l.getBlockEntity(HOLO);QaServer.check(h.holographic()&&h.bounds().width()==2&&h.bounds().height()==2,"holographic 2x2 owns matching extenders");QaServer.log("STORAGE_SERVER_DONE "+p.lines());}
 }
}
