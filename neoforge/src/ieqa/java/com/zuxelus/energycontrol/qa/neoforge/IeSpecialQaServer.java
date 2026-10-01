// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa.neoforge;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.wires.*;
import blusunrize.immersiveengineering.common.blocks.metal.*;
import com.zuxelus.energycontrol.port.*;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.card.*;
import com.zuxelus.energycontrol.qa.QaServer;
import dev.architectury.event.events.common.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import java.util.*;
/** Drives real IE wire transfer. Never writes the meter's sample buffer. */
public final class IeSpecialQaServer {
 public static final BlockPos PANEL=new BlockPos(0,1,0),METER=new BlockPos(0,0,5),INPUT=new BlockPos(-6,1,5),OUTPUT=new BlockPos(6,1,5),CAP=new BlockPos(6,0,5),THERMO=new BlockPos(5,0,1),HOT=THERMO.south();
 private static int ticks;private static boolean connected;private static final Set<UUID> joined=new HashSet<>();
 private static final boolean reload=System.getProperty("ec.qa.stage","").equals("ie-special-reload");
 private static net.minecraft.world.level.block.Block block(String id){return BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("immersiveengineering",id));}
 private static PanelBlockEntity panel(ServerLevel l){return (PanelBlockEntity)l.getBlockEntity(PANEL);}
 private static ItemStack card(Item item,BlockPos target,Direction side){var s=new ItemStack(item);var d=CardItem.data(s);CardTargets.bind(d,new CardTargets.Target("minecraft:overworld",target.asLong(),side.get3DDataValue()),false);d.putBoolean("showBars",true);CardItem.update(s,d);return s;}
 private static void check(boolean ok,String message){QaServer.check(ok,"IE specialized "+message);}
 public static void init(){LifecycleEvent.SERVER_STARTED.register(IeSpecialQaServer::setup);TickEvent.SERVER_POST.register(IeSpecialQaServer::tick);}
 private static void setup(MinecraftServer server){var l=server.overworld();QaServer.log("RUN_START IE-special server reload="+reload+" "+java.time.Instant.now());for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)l.setChunkForced(x,z,true);
  if(reload){check(l.getBlockEntity(METER) instanceof EnergyMeterBlockEntity&&l.getBlockEntity(THERMO) instanceof ThermoelectricGenBlockEntity,"actual machines restored from disk");check(panel(l).thickness()==8&&panel(l).getItem(0).is(EnergyControlPort.MACHINE.get()),"machine card and physical thickness restored from disk");return;}
  for(int x=-9;x<=10;x++)for(int z=-7;z<=9;z++)l.setBlockAndUpdate(new BlockPos(x,-1,z),Blocks.SMOOTH_STONE.defaultBlockState());
  l.setBlockAndUpdate(INPUT.below(),Blocks.SMOOTH_STONE.defaultBlockState());l.setBlockAndUpdate(CAP,block("capacitor_lv").defaultBlockState());
  for(var pos:List.of(INPUT,OUTPUT))l.setBlockAndUpdate(pos,block("connector_lv").defaultBlockState().setValue(IEProperties.FACING_ALL,Direction.DOWN));
  l.setBlockAndUpdate(METER,block("current_transformer").defaultBlockState());l.setBlockAndUpdate(METER.above(),block("current_transformer").defaultBlockState().setValue(IEProperties.MULTIBLOCKSLAVE,true));
  l.setBlockAndUpdate(THERMO,block("thermoelectric_generator").defaultBlockState());l.setBlockAndUpdate(THERMO.north(),Blocks.BLUE_ICE.defaultBlockState());
  for(var pos:List.of(HOT.east(),HOT.west(),HOT.south(),HOT.above()))l.setBlockAndUpdate(pos,Blocks.GLASS.defaultBlockState());l.setBlockAndUpdate(HOT,Blocks.LAVA.defaultBlockState());
  for(int x=0;x<3;x++)for(int y=0;y<2;y++)l.setBlockAndUpdate(PANEL.offset(x,y,0),(x==0&&y==0?EnergyControlPort.ADVANCED:EnergyControlPort.ADVANCED_EXTENDER).get().defaultBlockState().setValue(PanelBlock.FACING,Direction.NORTH));
  var p=panel(l);p.setItem(0,card(EnergyControlPort.MACHINE.get(),METER,Direction.NORTH));p.setItem(1,card(EnergyControlPort.MACHINE.get(),THERMO,Direction.NORTH));p.setItem(2,card(EnergyControlPort.ENERGY.get(),CAP,Direction.UP));p.setItem(3,new ItemStack(EnergyControlPort.TEXT.get()));p.setText(3,"REAL IE POWER\nWire meter + thermal potential");
  check(l.getBlockEntity(INPUT) instanceof EnergyConnectorBlockEntity&&l.getBlockEntity(OUTPUT) instanceof EnergyConnectorBlockEntity,"actual LV connectors initialized");check(l.getBlockEntity(METER) instanceof EnergyMeterBlockEntity,"actual current transformer initialized");
  l.setDayTime(6000);l.setWeatherParameters(100000,0,false,false);
 }
 private static void wire(ServerLevel l){var net=GlobalWireNetwork.getNetwork(l);net.addConnection(new Connection(WireType.COPPER,new ConnectionPoint(INPUT,0),new ConnectionPoint(METER,0),net));net.addConnection(new Connection(WireType.COPPER,new ConnectionPoint(METER,1),new ConnectionPoint(OUTPUT,0),net));connected=true;var connections=new HashSet<Connection>();for(int point=0;point<2;point++)connections.addAll(net.getLocalNet(METER).getConnections(new ConnectionPoint(METER,point)));check(connections.size()==3&&connections.stream().filter(Connection::isInternal).count()==1,"real copper connections include meter shunt");}
 private static void equality(ServerLevel l){int actual=((EnergyMeterBlockEntity)l.getBlockEntity(METER)).getAveragePower();var rows=CardDisplay.read(l,PANEL,panel(l).getItem(0),64,4,0);check(rows.contains(actual+" FE/t"),"card equals real meter getAveragePower");int potential=Math.max(0,l.getBlockEntity(THERMO).saveWithoutMetadata(l.registryAccess()).getInt("enegyOutput"));check(CardDisplay.read(l,PANEL,panel(l).getItem(1),64,4,0).contains(potential+" FE/t"),"card equals real thermal generation potential");}
 private static void tick(MinecraftServer server){var l=server.overworld();for(var player:server.getPlayerList().getPlayers())if(joined.add(player.getUUID())){server.getPlayerList().op(player.getGameProfile());player.teleportTo(l,player.getGameProfile().getName().equals("ECObserver")?-.5:1.5,.5,-4,0,0);player.getInventory().setItem(0,ItemStack.EMPTY);player.getInventory().setItem(1,new ItemStack(EnergyControlPort.MACHINE.get()));player.getInventory().selected=0;}
  if(server.getPlayerCount()<(reload?1:2))return;ticks++;
  if(!reload&&ticks==20)wire(l);
  if(reload||connected){int amount=reload?128:ticks<200?256:ticks<320?0:128;var cap=l.getCapability(Capabilities.EnergyStorage.BLOCK,INPUT,Direction.DOWN);if(cap!=null)cap.receiveEnergy(amount,false);}
  if(reload){if(ticks==80){check(((EnergyMeterBlockEntity)l.getBlockEntity(METER)).getAveragePower()>0,"saved real copper network resumes actual transfer");equality(l);check(panel(l).rows().stream().anyMatch(r->r.isBar()),"real capacitor bar resumes after restart");QaServer.log("ALL_IE_SPECIAL_RELOAD_SERVER_DONE");}return;}
  if(ticks==100){check(((EnergyMeterBlockEntity)l.getBlockEntity(METER)).getAveragePower()>0,"wire circuit carries real positive power");check(l.getBlockEntity(THERMO).saveWithoutMetadata(l.registryAccess()).getInt("enegyOutput")>0,"lava and blue ice create real thermal potential");equality(l);check(l.getCapability(Capabilities.EnergyStorage.BLOCK,CAP,Direction.UP).getEnergyStored()>0,"real sink capacitor receives wire-delivered energy");check(CardDisplay.read(l,PANEL,card(EnergyControlPort.MACHINE.get(),METER.above(),Direction.NORTH),64,4,0).contains(((EnergyMeterBlockEntity)l.getBlockEntity(METER)).getAveragePower()+" FE/t"),"upper dummy resolves real master meter reading");}
  if(ticks==200)l.setBlockAndUpdate(HOT,Blocks.AIR.defaultBlockState());
  if(ticks==280){check(((EnergyMeterBlockEntity)l.getBlockEntity(METER)).getAveragePower()==0,"meter average decays to zero after input stops");check(l.getBlockEntity(THERMO).saveWithoutMetadata(l.registryAccess()).getInt("enegyOutput")<=0,"removing real heat source removes potential");equality(l);}
  if(ticks==320)l.setBlockAndUpdate(HOT,Blocks.LAVA.defaultBlockState());
  if(ticks==400){check(((EnergyMeterBlockEntity)l.getBlockEntity(METER)).getAveragePower()>0,"restored FE input resumes real wire power");equality(l);check(panel(l).thickness()==8,"real client geometry edit reaches dedicated server");QaServer.log("ALL_IE_SPECIAL_SERVER_DONE");}
 }
}
