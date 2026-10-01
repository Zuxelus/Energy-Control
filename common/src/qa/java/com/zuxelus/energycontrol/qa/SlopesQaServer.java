// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.*;
import com.zuxelus.energycontrol.port.block.*;
import dev.architectury.event.events.common.*;
import net.minecraft.core.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import java.util.*;
public final class SlopesQaServer {
 public static final BlockPos CORE=new BlockPos(0,1,0);
 private static final Set<UUID> players=new HashSet<>();private static int ticks;
 private static final boolean reload=System.getProperty("ec.qa.stage","").endsWith("reload");
 public static BlockPos face(int i){return new BlockPos(18+i*6,8,8);}
 private static PanelBlockEntity make(ServerLevel l,BlockPos pos,Direction facing){
  l.setBlockAndUpdate(pos,EnergyControlPort.ADVANCED.get().defaultBlockState().setValue(PanelBlock.FACING,facing));var p=(PanelBlockEntity)l.getBlockEntity(pos);
  for(int x=0;x<2;x++)for(int y=0;y<2;y++)if(x!=0||y!=0)l.setBlockAndUpdate(p.at(x,y),EnergyControlPort.ADVANCED_EXTENDER.get().defaultBlockState().setValue(PanelBlock.FACING,facing));
  p.setItem(0,new ItemStack(EnergyControlPort.TEXT.get()));p.setText(0,"SLOPED SCREEN\n"+facing+" | solid + live text");p.setItem(1,new ItemStack(EnergyControlPort.TIME.get()));return p;
 }
 public static void init(){LifecycleEvent.SERVER_STARTED.register(SlopesQaServer::setup);TickEvent.SERVER_POST.register(SlopesQaServer::tick);}
 private static void setup(MinecraftServer s){var l=s.overworld();QaServer.log("RUN_START slopes-server reload="+reload+" "+java.time.Instant.now());for(int x=-1;x<=4;x++)for(int z=-1;z<=1;z++)l.setChunkForced(x,z,true);
  if(reload){var p=(PanelBlockEntity)l.getBlockEntity(CORE);QaServer.check(p.slopeHorizontal()==8&&p.slopeVertical()==4&&p.thickness()==8,"slopes and thickness survive server disk restart");QaServer.check(l.getBlockState(p.at(1,1)).getValue(PanelBlock.SLOPED),"rebuilt sloped extender state survives restart");return;}
  for(int x=-10;x<12;x++)for(int z=-8;z<8;z++)l.setBlockAndUpdate(new BlockPos(x,-1,z),Blocks.SMOOTH_STONE.defaultBlockState());
  make(l,CORE,Direction.NORTH);
  for(int i=0;i<6;i++){var p=make(l,face(i),Direction.values()[i]);for(int n=0;n<8;n++)p.configure(15);for(int n=0;n<4;n++)p.configure(18);for(int n=0;n<13;n++)p.configure(19);}
  l.setDayTime(6000);l.setWeatherParameters(100000,0,false,false);
 }
 public static boolean continuous(PanelBlockEntity p){var r=(PanelBlockEntity)p.getLevel().getBlockEntity(p.at(1,0));var u=(PanelBlockEntity)p.getLevel().getBlockEntity(p.at(0,1));return r!=null&&u!=null&&Math.abs(p.caseSurface().depth(1,.5)-r.caseSurface().depth(0,.5))<1e-9&&Math.abs(p.caseSurface().depth(.5,1)-u.caseSurface().depth(.5,0))<1e-9;}
 private static void tick(MinecraftServer s){var l=s.overworld();for(var player:s.getPlayerList().getPlayers())if(players.add(player.getUUID())){
  s.getPlayerList().op(player.getGameProfile());player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);boolean observer=player.getGameProfile().getName().equals("ECObserver");player.teleportTo(l,observer?-2.5:-.5,.2,-3.5,0,0);player.getAbilities().mayfly=true;player.getAbilities().flying=true;player.onUpdateAbilities();player.getInventory().clearContent();player.getInventory().setItem(1,new ItemStack(EnergyControlPort.ADVANCED_EXTENDER.get()));player.getInventory().selected=0;player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket(0));
 }
 if(players.size()<(reload?1:2))return;ticks++;
 if(ticks==60){for(int i=0;i<6;i++){var p=(PanelBlockEntity)l.getBlockEntity(face(i));QaServer.check(p.bounds().width()==2&&p.bounds().height()==2&&continuous(p),"grouped wedge plane continuous across parts facing "+p.facing());QaServer.check(p.caseShape().toAabbs().size()>1,"actual solid collision slopes facing "+p.facing());}if(reload){QaServer.check(continuous((PanelBlockEntity)l.getBlockEntity(CORE)),"reloaded core and extenders retain same plane");QaServer.log("ALL_SLOPES_RELOAD_SERVER_DONE");}}
 if(!reload&&ticks==400){var p=(PanelBlockEntity)l.getBlockEntity(CORE);QaServer.check(p.slopeHorizontal()==8&&p.slopeVertical()==4&&p.thickness()==8,"normal GUI slope edits persist on server");QaServer.check(continuous(p),"normal break replacement restores continuous plane");QaServer.log("ALL_SLOPES_SERVER_DONE");}
 }
}
