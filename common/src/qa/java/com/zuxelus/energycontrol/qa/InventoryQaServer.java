// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.*;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.card.*;
import com.zuxelus.energycontrol.port.menu.*;
import dev.architectury.event.events.common.*;
import net.minecraft.core.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.*;
import java.util.*;
public final class InventoryQaServer {
 public static final BlockPos CORE=new BlockPos(0,1,0),CHEST=new BlockPos(4,0,0),FURNACE=new BlockPos(6,0,0),INVALID=new BlockPos(4,0,1);
 private static final boolean reload=System.getProperty("ec.qa.stage","").endsWith("reload");private static int ticks;private static final Set<UUID> joined=new HashSet<>();
 public static void init(){LifecycleEvent.SERVER_STARTED.register(InventoryQaServer::setup);TickEvent.SERVER_POST.register(InventoryQaServer::tick);}
 private static void check(boolean b,String s){QaServer.check(b,"inventory "+s);}
 private static PanelBlockEntity panel(ServerLevel l){return (PanelBlockEntity)l.getBlockEntity(CORE);}
 private static void setup(MinecraftServer server){var l=server.overworld();QaServer.log("RUN_START inventory-server reload="+reload+" "+java.time.Instant.now());for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)l.setChunkForced(x,z,true);
  if(reload){check(panel(l).getItem(0).is(EnergyControlPort.INVENTORY.get()),"inventory card survives disk restart");check(com.zuxelus.energycontrol.port.inventory.InventorySnapshot.fields(CardItem.data(panel(l).getItem(0)))==15,"five field mask persists after restart");return;}
  for(int x=-9;x<13;x++)for(int z=-8;z<8;z++)l.setBlockAndUpdate(new BlockPos(x,-1,z),Blocks.SMOOTH_STONE.defaultBlockState());
  l.setBlockAndUpdate(CHEST,Blocks.CHEST.defaultBlockState());var chest=(Container)l.getBlockEntity(CHEST);chest.setItem(0,new ItemStack(Items.STONE,16));chest.setItem(6,new ItemStack(Items.IRON_INGOT,32));chest.setItem(26,new ItemStack(Items.GOLD_INGOT,8));
  l.setBlockAndUpdate(FURNACE,Blocks.FURNACE.defaultBlockState());var furnace=(Container)l.getBlockEntity(FURNACE);furnace.setItem(0,new ItemStack(Items.RAW_IRON,5));furnace.setItem(1,new ItemStack(Items.DIAMOND,3));furnace.setItem(2,new ItemStack(Items.IRON_INGOT,7));l.setBlockAndUpdate(INVALID,Blocks.STONE.defaultBlockState());
  for(int x=0;x<3;x++)for(int y=0;y<2;y++)l.setBlockAndUpdate(CORE.offset(x,y,0),(x==0&&y==0?EnergyControlPort.ADVANCED:EnergyControlPort.ADVANCED_EXTENDER).get().defaultBlockState().setValue(PanelBlock.FACING,Direction.NORTH));
  panel(l).setItem(0,GraphicsQaServer.bound(EnergyControlPort.INVENTORY.get(),CHEST,true));
  l.setDayTime(6000);l.setWeatherParameters(100000,0,false,false);
 }
 private static void tick(MinecraftServer server){var l=server.overworld();for(var p:server.getPlayerList().getPlayers())if(joined.add(p.getUUID())){server.getPlayerList().op(p.getGameProfile());p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.teleportTo(l,p.getGameProfile().getName().equals("ECObserver")?-.5:1.5,0,-4,0,0);
  if(!reload){p.getInventory().clearContent();p.getInventory().setItem(0,new ItemStack(EnergyControlPort.KIT_INVENTORY.get(),2));p.getInventory().setItem(1,new ItemStack(EnergyControlPort.HOLDER.get()));var portable=new ItemStack(EnergyControlPort.PORTABLE.get());new PortableInventory(portable).setItem(0,GraphicsQaServer.bound(EnergyControlPort.INVENTORY.get(),CHEST,true));p.getInventory().setItem(2,portable);p.getInventory().setItem(3,new ItemStack(EnergyControlPort.KIT_ENERGY.get(),2));p.getInventory().setItem(4,new ItemStack(EnergyControlPort.KIT_FLUID.get(),2));p.getInventory().setItem(5,new ItemStack(EnergyControlPort.KIT_REDSTONE.get(),2));p.getInventory().setItem(6,new ItemStack(EnergyControlPort.KIT_MACHINE.get(),2));p.getInventory().setItem(9,new ItemStack(EnergyControlPort.TEXT.get()));p.getInventory().setItem(10,new ItemStack(Items.DIAMOND));p.getInventory().setItem(11,new ItemStack(EnergyControlPort.HOLDER.get()));}p.getInventory().selected=7;p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket(7));
  if(reload&&!p.getGameProfile().getName().equals("ECObserver")){check(new CardHolderInventory(p.getInventory().getItem(1)).getItem(0).is(EnergyControlPort.TEXT.get()),"holder card restored in player data");check(com.zuxelus.energycontrol.port.inventory.InventorySnapshot.fields(CardItem.data(new PortableInventory(p.getInventory().getItem(2)).getItem(0)))==15,"portable field configuration restored in player data");}}
  if(server.getPlayerCount()<(reload?1:2))return;ticks++;
  if(ticks%20==0)((Container)l.getBlockEntity(CHEST)).setItem(26,new ItemStack(Items.GOLD_INGOT,1+(ticks/20)%32));
  if(ticks==40){var snapshot=EnergyControlPort.inventoryProbe.read(l,CHEST,Direction.NORTH).orElseThrow();check(snapshot.scannedSlots()==27&&snapshot.usedSlots()==3,"actual chest exposes27 slots with3 used");check(snapshot.items().intValue()==48+((Container)l.getBlockEntity(CHEST)).getItem(26).getCount(),"complete real total includes slots6 and26 beyond preview");check(snapshot.preview().size()==6,"only first six slots form detail preview");check(EnergyControlPort.inventoryProbe.read(l,FURNACE,Direction.UP).orElseThrow().items().intValue()==5,"real furnace top reads input slot only");check(EnergyControlPort.inventoryProbe.read(l,FURNACE,Direction.NORTH).orElseThrow().items().intValue()==3,"real furnace side reads fuel slot only");check(EnergyControlPort.inventoryProbe.read(l,INVALID,Direction.NORTH).isEmpty(),"ordinary stone has no inventory");if(reload)QaServer.log("ALL_INVENTORY_RELOAD_SERVER_DONE");}
  if(!reload&&ticks==230)for(var p:server.getPlayerList().getPlayers())QaServer.log("KIT_SERVER "+p.getGameProfile().getName()+" slot="+p.getInventory().selected+" kit="+p.getInventory().getItem(0)+" menu="+p.containerMenu.getClass().getSimpleName());
  if(!reload&&ticks==780){var p=server.getPlayerList().getPlayers().stream().filter(v->v.getGameProfile().getName().equals("ECEditor")).findFirst().orElseThrow();check(p.getInventory().getItem(0).getCount()==1,"successful inventory kit consumes exactly one in survival");check(p.getInventory().getItem(3).getCount()==1&&p.getInventory().getItem(4).getCount()==1&&p.getInventory().getItem(5).getCount()==1,"generic supported kit counts synchronize");check(p.getInventory().getItem(6).getCount()==2,"unsupported machine kit never consumes");check(new CardHolderInventory(p.getInventory().getItem(1)).getItem(0).is(EnergyControlPort.TEXT.get()),"holder transfer persists on server");check(com.zuxelus.energycontrol.port.inventory.InventorySnapshot.fields(CardItem.data(panel(l).getItem(0)))==15,"inventory field edit reaches server");QaServer.log("ALL_INVENTORY_SERVER_DONE");}
 }
}
