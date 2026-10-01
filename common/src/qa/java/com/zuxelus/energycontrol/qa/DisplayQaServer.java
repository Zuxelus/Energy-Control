// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.*;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.core.ProjectionSettings;
import dev.architectury.event.events.common.*;
import net.minecraft.core.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import java.util.*;
public final class DisplayQaServer {
 public static final BlockPos CORE=new BlockPos(0,1,0),HOLO=new BlockPos(-4,1,0);
 private static final Set<UUID> players=new HashSet<>();private static final boolean reload=System.getProperty("ec.qa.stage","").equals("pages-reload");
 public static BlockPos face(int i){return new BlockPos(20+i*6,8,8);}
 public static void init(){LifecycleEvent.SERVER_STARTED.register(DisplayQaServer::setup);TickEvent.SERVER_POST.register(DisplayQaServer::tick);}
 private static PanelBlockEntity make(ServerLevel l,BlockPos pos,boolean holo,Direction facing){
  l.setBlockAndUpdate(pos,(holo?EnergyControlPort.HOLO:EnergyControlPort.ADVANCED).get().defaultBlockState().setValue(PanelBlock.FACING,facing));var p=(PanelBlockEntity)l.getBlockEntity(pos);
  for(int x=0;x<2;x++)for(int y=0;y<2;y++)if(x!=0||y!=0)l.setBlockAndUpdate(p.at(x,y),(holo?EnergyControlPort.HOLO_EXTENDER:EnergyControlPort.ADVANCED_EXTENDER).get().defaultBlockState().setValue(PanelBlock.FACING,facing));return p;
 }
 private static void setup(MinecraftServer s){var l=s.overworld();QaServer.log("RUN_START display-server reload="+reload+" "+java.time.Instant.now());for(int x=-1;x<=4;x++)for(int z=-1;z<=1;z++)l.setChunkForced(x,z,true);
  if(reload){var p=(PanelBlockEntity)l.getBlockEntity(CORE);var h=(PanelBlockEntity)l.getBlockEntity(HOLO);QaServer.check(p.pageSize()==8&&p.pageTicks()==0,"display page size and manual mode survive server restart");QaServer.check(h.projection().equals(new ProjectionSettings(8,28,21))&&h.pageSize()==4,"display projection and holographic page size survive server restart");return;}
  for(int x=-10;x<10;x++)for(int z=-8;z<6;z++)l.setBlockAndUpdate(new BlockPos(x,-1,z),Blocks.SMOOTH_STONE.defaultBlockState());
  var p=make(l,CORE,false,Direction.NORTH);var h=make(l,HOLO,true,Direction.NORTH);
  for(int card=0;card<4;card++){var rows=new ArrayList<String>();for(int line=0;line<10;line++)rows.add("CARD "+card+" LINE "+line);for(var target:List.of(p,h)){target.setItem(card,new ItemStack(EnergyControlPort.TEXT.get()));target.setText(card,String.join("\n",rows));}}
  for(int i=0;i<6;i++){var f=make(l,face(i),true,Direction.values()[i]);f.setItem(0,new ItemStack(EnergyControlPort.TEXT.get()));f.setText(0,"HOLOGRAM "+Direction.values()[i]+"\nTilted projection\nDepth 8 / 16");for(int n=0;n<4;n++)f.configure(11);for(int n=0;n<3;n++)f.configure(12);for(int n=0;n<8;n++)f.configure(13);}
  QaServer.check(p.pageCount()==2&&p.lines().size()==32,"display forty source lines produce two pages without discarding tail");QaServer.check(!p.configure(11)&&p.projection().equals(ProjectionSettings.DEFAULT),"solid panel rejects hologram-only projection changes");
 }
 private static void tick(MinecraftServer s){for(var p:s.getPlayerList().getPlayers())if(players.add(p.getUUID())){s.getPlayerList().op(p.getGameProfile());p.teleportTo(s.overworld(),-2,.5,-4,0,0);p.getAbilities().mayfly=true;p.getAbilities().flying=true;p.onUpdateAbilities();p.getInventory().setItem(0,ItemStack.EMPTY);p.getInventory().selected=0;}}
}
