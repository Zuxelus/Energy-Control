// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.*;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.card.*;
import com.zuxelus.energycontrol.port.menu.PortableInventory;
import dev.architectury.event.events.common.*;
import net.minecraft.core.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import java.util.*;
public final class GraphicsQaServer {
 public static final BlockPos CORE=new BlockPos(0,1,0),BATTERY=new BlockPos(4,0,0),TANK=new BlockPos(8,0,0);
 private static final Set<UUID> players=new HashSet<>();private static int ticks;
 private static final boolean reload=System.getProperty("ec.qa.stage","").equals("graphics-reload");
 public static BlockPos face(int i){return new BlockPos(18+i*6,8,8);}
 public static ItemStack bound(Item item,BlockPos target,boolean bars){var stack=new ItemStack(item);var data=CardItem.data(stack);CardTargets.bind(data,new CardTargets.Target("minecraft:overworld",target.asLong(),Direction.NORTH.get3DDataValue()),false);data.putBoolean("showBars",bars);CardItem.update(stack,data);return stack;}
 private static PanelBlockEntity make(ServerLevel l,BlockPos pos,Direction facing){
  l.setBlockAndUpdate(pos,EnergyControlPort.ADVANCED.get().defaultBlockState().setValue(PanelBlock.FACING,facing));var p=(PanelBlockEntity)l.getBlockEntity(pos);
  for(int x=0;x<2;x++)for(int y=0;y<2;y++)if(x!=0||y!=0)l.setBlockAndUpdate(p.at(x,y),EnergyControlPort.ADVANCED_EXTENDER.get().defaultBlockState().setValue(PanelBlock.FACING,facing));return p;
 }
 public static void init(){LifecycleEvent.SERVER_STARTED.register(GraphicsQaServer::setup);TickEvent.SERVER_POST.register(GraphicsQaServer::tick);}
 private static void setup(MinecraftServer s){var l=s.overworld();QaServer.log("RUN_START graphics-server reload="+reload+" "+java.time.Instant.now());for(int x=-1;x<=4;x++)for(int z=-1;z<=1;z++)l.setChunkForced(x,z,true);
  if(reload){var p=(PanelBlockEntity)l.getBlockEntity(CORE);QaServer.check(p.thickness()==8,"case thickness survives actual server restart");QaServer.check(CardItem.data(p.getItem(0)).getBoolean("showBars")&&CardItem.data(p.getItem(1)).getBoolean("showBars"),"both card bar settings survive actual server restart");QaServer.check(l.getBlockState(p.at(1,1)).getValue(PanelBlock.THICKNESS)==8,"rebuilt extender thickness survives restart");return;}
  for(int x=-10;x<12;x++)for(int z=-8;z<8;z++)l.setBlockAndUpdate(new BlockPos(x,-1,z),Blocks.SMOOTH_STONE.defaultBlockState());
  l.setBlockAndUpdate(BATTERY,Blocks.CHEST.defaultBlockState());l.setBlockAndUpdate(TANK,Blocks.CHEST.defaultBlockState());
  var p=make(l,CORE,Direction.NORTH);p.setItem(0,bound(EnergyControlPort.ENERGY.get(),BATTERY,false));p.setItem(1,bound(EnergyControlPort.FLUID.get(),TANK,false));p.setItem(2,bound(EnergyControlPort.MACHINE.get(),BATTERY,false));p.setItem(3,new ItemStack(EnergyControlPort.TEXT.get()));p.setText(3,"CASE + BARS\nLive typed measurements");
  QaServer.check(p.rows().stream().noneMatch(r->r.isBar()),"bar display defaults off for existing cards");
  QaServer.check(p.lines().contains("No supported machine data"),"unsupported machine is explicit with no external mod");
  for(int i=0;i<6;i++){
   var f=make(l,face(i),Direction.values()[i]);f.setItem(0,new ItemStack(EnergyControlPort.TEXT.get()));f.setText(0,"CASE "+Direction.values()[i]+"\nThickness 4 / 16");f.setItem(1,bound(EnergyControlPort.ENERGY.get(),BATTERY,true));for(int n=0;n<12;n++)f.configure(15);
   var b=f.getBlockState().getCollisionShape(l,f.getBlockPos()).bounds();var axis=f.facing().getAxis();QaServer.check(Math.abs(b.max(axis)-b.min(axis)-.25)<1e-6,"actual block collision quarter thickness facing "+f.facing());
  }
  l.setDayTime(6000);l.setWeatherParameters(100000,0,false,false);
 }
 private static void tick(MinecraftServer s){ticks++;var l=s.overworld();for(var player:s.getPlayerList().getPlayers())if(players.add(player.getUUID())){
  s.getPlayerList().op(player.getGameProfile());player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);boolean observer=player.getGameProfile().getName().equals("ECObserver");player.teleportTo(l,observer?-2.5:-.5,.2,-3.5,0,0);player.getAbilities().mayfly=true;player.getAbilities().flying=true;player.onUpdateAbilities();
  if(!reload){var portable=new ItemStack(EnergyControlPort.PORTABLE.get());new PortableInventory(portable).setItem(0,bound(EnergyControlPort.ENERGY.get(),BATTERY,false));player.getInventory().setItem(0,portable);player.getInventory().setItem(1,new ItemStack(EnergyControlPort.ADVANCED_EXTENDER.get()));}player.getInventory().selected=0;
 }
 if(ticks==60){var p=(PanelBlockEntity)l.getBlockEntity(CORE);QaServer.check(p.bounds().width()==2&&p.bounds().height()==2,"solid group remains2x2 after state shape initialization");if(reload)QaServer.check(p.rows().stream().filter(r->r.isBar()).count()==2,"saved bars resume live evaluation after restart");}
 }
}
