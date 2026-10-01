// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa.neoforge;
import blusunrize.immersiveengineering.common.blocks.metal.CapacitorBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import com.zuxelus.energycontrol.port.*;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.card.*;
import com.zuxelus.energycontrol.qa.QaServer;
import dev.architectury.event.events.common.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import java.nio.file.*;
import java.util.*;
/** Acceptance against approved, unmodified IE12.4.2-194. No mock providers. */
public final class IeQaServer {
 public static final BlockPos PANEL=new BlockPos(0,1,0), A=new BlockPos(4,0,0), B=new BlockPos(6,0,0), T1=new BlockPos(5,0,4),T2=new BlockPos(10,0,4),REMOTE=new BlockPos(196,0,4),RT=new BlockPos(200,0,4);
 private static int ticks,unloadAt;private static boolean detached,replaced,unloaded,finished;
 private static final boolean reload=System.getProperty("ec.qa.stage","").equals("ie-reload");
 private static final Set<UUID> joined=new HashSet<>();private static int remoteEnergy,remoteFluid;
 private static void check(boolean v,String s){QaServer.check(v,"IE "+s);}
 private static net.minecraft.world.level.block.Block block(String id){return BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("immersiveengineering",id));}
 private static IEnergyStorage energy(ServerLevel l,BlockPos p){return l.getCapability(Capabilities.EnergyStorage.BLOCK,p,Direction.UP);}
 private static BlockPos io(BlockPos origin){return origin.offset(1,0,1);}
 private static IFluidHandler fluid(ServerLevel l,BlockPos origin){return l.getCapability(Capabilities.FluidHandler.BLOCK,io(origin),Direction.NORTH);}
 private static void tank(ServerLevel l,BlockPos origin){
  var mb=IEMultiblocks.SHEETMETAL_TANK;
  for(var part:mb.getStructure(l))l.setBlockAndUpdate(origin.offset(part.pos()),part.state());
  check(mb.createStructure(l,origin.offset(1,1,2),Direction.SOUTH,null),"official multiblock recognition/forms "+origin.toShortString());
  check(fluid(l,origin)!=null&&fluid(l,origin).getTankCapacity(0)==512000,"formed tank exposes real512000mB capacity");
 }
 private static ItemStack card(Item item,BlockPos... positions){var s=new ItemStack(item);var d=CardItem.data(s);for(var p:positions)CardTargets.bind(d,new CardTargets.Target("minecraft:overworld",p.asLong(),item==EnergyControlPort.ENERGY.get()||item==EnergyControlPort.ENERGY_ARRAY.get()?Direction.UP.get3DDataValue():Direction.NORTH.get3DDataValue()),((CardItem)item).array());d.putBoolean("hidePercent",true);CardItem.update(s,d);return s;}
 private static PanelBlockEntity panel(ServerLevel l){return (PanelBlockEntity)l.getBlockEntity(PANEL);}
 private static void force(ServerLevel l,boolean value){l.setChunkForced(12,0,value);if(value)l.getChunk(12,0);}
 public static void init(){LifecycleEvent.SERVER_STARTED.register(IeQaServer::setup);TickEvent.SERVER_POST.register(IeQaServer::tick);}
 private static void setup(MinecraftServer server){
  QaServer.log("RUN_START IE server reload="+reload+" "+java.time.Instant.now());var l=server.overworld();l.getGameRules().getRule(GameRules.RULE_SPAWN_CHUNK_RADIUS).set(0,server);l.setChunkForced(0,0,true);l.setChunkForced(0,-1,true);force(l,true);
  if(reload){
   try{var saved=new Properties();try(var r=Files.newBufferedReader(Path.of("ie-persist.properties"))){saved.load(r);}check(energy(l,A).getEnergyStored()==Integer.parseInt(saved.getProperty("energy")),"server restart retains actual capacitor energy");check(fluid(l,T1).getFluidInTank(0).getAmount()==Integer.parseInt(saved.getProperty("fluid")),"server restart retains real multiblock fluid");check(energy(l,REMOTE).getEnergyStored()==Integer.parseInt(saved.getProperty("remoteEnergy"))&&fluid(l,RT).getFluidInTank(0).getAmount()==Integer.parseInt(saved.getProperty("remoteFluid")),"server restart retains remote machines");check(CardTargets.read(CardItem.data(panel(l).getItem(4))).size()==2&&panel(l).range()==512,"server restart retains array bindings and upgrade");}
   catch(Exception e){throw new RuntimeException(e);}return;
  }
  for(int x=-4;x<16;x++)for(int z=-6;z<10;z++)l.setBlockAndUpdate(new BlockPos(x,-1,z),Blocks.SMOOTH_STONE.defaultBlockState());
  for(var p:List.of(A,B,REMOTE)){l.setBlockAndUpdate(p,block("capacitor_lv").defaultBlockState());check(l.getBlockEntity(p) instanceof CapacitorBlockEntity,"real IE capacitor block entity "+p.toShortString());for(int i=0;i<50;i++)energy(l,p).receiveEnergy(256,false);l.getBlockEntity(p).setChanged();}
  for(var o:List.of(T1,T2,RT)){tank(l,o);fluid(l,o).fill(new FluidStack(Fluids.WATER,12000),IFluidHandler.FluidAction.EXECUTE);}
  for(int x=0;x<3;x++)for(int y=0;y<2;y++)l.setBlockAndUpdate(PANEL.offset(x,y,0),(x==0&&y==0?EnergyControlPort.ADVANCED:EnergyControlPort.ADVANCED_EXTENDER).get().defaultBlockState().setValue(PanelBlock.FACING,Direction.NORTH));
  var p=panel(l);p.setItem(0,new ItemStack(EnergyControlPort.TEXT.get()));p.setText(0,"REAL IMMERSIVE ENGINEERING\nFE capacitors + 512-bucket tanks");p.setItem(1,card(EnergyControlPort.ENERGY.get(),A));p.setItem(2,card(EnergyControlPort.FLUID.get(),io(T1)));p.setItem(3,card(EnergyControlPort.ENERGY_ARRAY.get(),A,B));p.setItem(4,card(EnergyControlPort.FLUID_ARRAY.get(),io(T1),io(T2)));p.setItem(5,card(EnergyControlPort.ENERGY.get(),REMOTE));p.setItem(6,card(EnergyControlPort.FLUID.get(),io(RT)));p.setItem(8,new ItemStack(EnergyControlPort.RANGE.get(),3));
  check(EnergyControlPort.fluidProbe.read(l,T1.offset(0,2,0),Direction.NORTH).isEmpty(),"non-port tank face correctly reports no capability");
 }
 private static void tick(MinecraftServer server){
  var l=server.overworld();for(var player:server.getPlayerList().getPlayers())if(joined.add(player.getUUID())){player.teleportTo(l,2,.5,-4,0,0);player.getInventory().setItem(0,new ItemStack(EnergyControlPort.ENERGY.get()));player.getInventory().setItem(4,new ItemStack(block("capacitor_lv")));player.getInventory().setChanged();}
  if(server.getPlayerCount()==0||finished)return;if(reload){if(++ticks==80){QaServer.log("ALL_IE_RELOAD_SERVER_DONE");finished=true;}return;}ticks++;
  if(ticks%10==0&&ticks<650){for(var p:List.of(A,B)){var e=energy(l,p);if(e!=null){e.receiveEnergy(97,false);l.getBlockEntity(p).setChanged();}}var f=fluid(l,T1);if(f!=null)f.fill(new FluidStack(Fluids.WATER,113),IFluidHandler.FluidAction.EXECUTE);}
  if(ticks==100){
   var p=panel(l);var e=energy(l,A);var f=fluid(l,T1);
   check(CardDisplay.read(l,PANEL,p.getItem(1),512,4,0).stream().anyMatch(s->s.contains(e.getEnergyStored()+" / "+e.getMaxEnergyStored()+" FE")),"energy card equals real IE capability");
   check(CardDisplay.read(l,PANEL,p.getItem(2),512,4,0).stream().anyMatch(s->s.contains(f.getFluidInTank(0).getAmount()+" / 512000 mB")),"fluid card equals real IE multiblock");
   check(CardDisplay.read(l,PANEL,p.getItem(3),512,4,0).stream().anyMatch(s->s.contains((e.getEnergyStored()+energy(l,B).getEnergyStored())+" / "+(e.getMaxEnergyStored()+energy(l,B).getMaxEnergyStored())+" FE")),"array totals two real capacitors");
   check(CardDisplay.read(l,PANEL,p.getItem(4),512,4,0).stream().anyMatch(s->s.contains((f.getFluidInTank(0).getAmount()+fluid(l,T2).getFluidInTank(0).getAmount())+" / 1024000 mB")),"array totals two real tank structures");
  }
  if(ticks>100&&ticks<350){if(!detached&&l.getBlockState(A).isAir()){detached=true;check(CardDisplay.read(l,PANEL,panel(l).getItem(1),512,4,0).contains("No compatible energy storage"),"actual creative removal invalidates sensor");}if(detached&&!replaced&&l.getBlockEntity(A) instanceof CapacitorBlockEntity){replaced=true;check(energy(l,A)!=null,"actual placement restores fresh IE capability");}}
  if(ticks==360){check(detached&&replaced,"client removed and replaced actual capacitor");l.destroyBlock(T2.offset(0,1,0),false);}
  if(ticks==390){check(fluid(l,T2)==null,"tank disassembly invalidates real fluid capability");tank(l,T2);fluid(l,T2).fill(new FluidStack(Fluids.WATER,7000),IFluidHandler.FluidAction.EXECUTE);}
  if(ticks==430)check(CardDisplay.read(l,PANEL,panel(l).getItem(4),512,4,0).stream().anyMatch(s->s.contains("1024000 mB")),"tank reformation restores array capacity");
  if(ticks==660){remoteEnergy=energy(l,REMOTE).getEnergyStored();remoteFluid=fluid(l,RT).getFluidInTank(0).getAmount();force(l,false);unloadAt=ticks;QaServer.log("IE released remote chunk ticket");}
  if(unloadAt>0&&!unloaded&&ticks%20==0&&!l.hasChunkAt(REMOTE)){
   unloaded=true;var p=panel(l);check(CardDisplay.read(l,PANEL,p.getItem(5),512,4,0).contains("Target chunk unloaded")&&CardDisplay.read(l,PANEL,p.getItem(6),512,4,0).contains("Target chunk unloaded"),"unloaded real machine chunk is reported without force loading");check(!l.hasChunkAt(REMOTE),"sensor evaluation did not load target chunk");force(l,true);
   check(energy(l,REMOTE).getEnergyStored()==remoteEnergy&&fluid(l,RT).getFluidInTank(0).getAmount()==remoteFluid,"chunk reload retains real IE machine contents");
   var values=new Properties();values.setProperty("energy",""+energy(l,A).getEnergyStored());values.setProperty("fluid",""+fluid(l,T1).getFluidInTank(0).getAmount());values.setProperty("remoteEnergy",""+remoteEnergy);values.setProperty("remoteFluid",""+remoteFluid);
   try(var w=Files.newBufferedWriter(Path.of("ie-persist.properties"))){values.store(w,"Expected actual IE data at clean shutdown");}catch(Exception e){throw new RuntimeException(e);}
   p.setText(0,"IE_QA_DONE\nReal capacitors and formed tanks");QaServer.log("ALL_IE_SERVER_SCENARIOS_DONE");finished=true;
  }
  if(ticks==1800)check(false,"timeout waiting for actual chunk unload");
 }
}
