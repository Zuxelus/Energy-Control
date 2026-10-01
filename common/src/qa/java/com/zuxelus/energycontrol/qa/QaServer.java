// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;

import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.card.CardItem;
import dev.architectury.event.events.common.*;
import net.minecraft.core.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import java.nio.file.*;
import java.util.*;

/** Test fixture, compiled exclusively by -Pqa. Never shipped in release jars. */
public final class QaServer {
    public static final BlockPos CORE = new BlockPos(0,1,0), BATTERY = new BlockPos(4,0,0);
    private static int ticks;
    private static final Set<UUID> positioned = new HashSet<>();
    private static final Map<UUID,Integer> ages = new HashMap<>();
    public static BlockPos facePos(int index) { return new BlockPos(-10+index*4,6,10); }
    private static String previous;
    public static void log(String message) {
        System.out.println("[EC-QA] " + message);
        try { Files.writeString(Path.of("qa-results.txt"), message+"\n", StandardOpenOption.CREATE,StandardOpenOption.APPEND); }
        catch(Exception e) { throw new RuntimeException(e); }
    }
    public static void check(boolean ok, String name) { log((ok?"PASS ":"FAIL ")+name); if(!ok) throw new AssertionError(name); }
    public static void init() {
        if(System.getProperty("ec.qa.stage","").startsWith("pages")){DisplayQaServer.init();return;}
        if(System.getProperty("ec.qa.stage","").equals("storage")){StorageQaServer.init();return;}
        LifecycleEvent.SERVER_STARTED.register(QaServer::setup);
        TickEvent.SERVER_POST.register(QaServer::tick);
    }
    private static void setup(MinecraftServer server) {
        log("RUN_START server "+java.time.Instant.now());
        ServerLevel level=server.overworld();
        for(int x=-1;x<=1;x++) for(int z=-1;z<=1;z++) level.setChunkForced(x,z,true);
        try {
            if(Files.deleteIfExists(Path.of("qa-reset.flag"))) {
                log("RESET task-owned 3x2 fixture after interrupted test");
                for(int x=0;x<3;x++) for(int y=0;y<2;y++) level.removeBlock(CORE.offset(x,y,0),false);
            }
        } catch(java.io.IOException e) {throw new RuntimeException(e);}
        if(level.getBlockEntity(CORE) instanceof PanelBlockEntity panel) {
            if(CardItem.data(panel.getItem(0)).getString("text").contains("PERSISTED")) check(true,"disk reload retains client-edited text");
            else log("RELOAD original fixture (client edit not yet completed)");
            check(panel.bounds().width()==3 && panel.bounds().height()==2,"disk reload retains 3x2 bounds");
            log("RELOAD "+panel.getUpdateTag(level.registryAccess()));
            if(panel.uniformFont()) check(panel.scalePercent()==125 && panel.alignment()==1 && panel.refreshTicks()==40 && panel.background()==0x303840,"disk reload retains display settings");
            if(CardItem.data(panel.getItem(1)).contains("title")) check(CardItem.data(panel.getItem(1)).getString("title").equals("Capacitor A") && CardItem.data(panel.getItem(1)).getBoolean("hidePercent"),"disk reload retains sensor title and fields");
        } else {
            for(int x=-8;x<=10;x++) for(int z=-8;z<=5;z++) level.setBlockAndUpdate(new BlockPos(x,-1,z),Blocks.SMOOTH_STONE.defaultBlockState());
            level.setBlockAndUpdate(BATTERY,Blocks.CHEST.defaultBlockState());
            for(int x=0;x<3;x++) for(int y=0;y<2;y++) level.setBlockAndUpdate(CORE.offset(x,y,0),
                (x==0&&y==0?EnergyControlPort.ADVANCED:EnergyControlPort.ADVANCED_EXTENDER).get().defaultBlockState().setValue(PanelBlock.FACING,Direction.NORTH));
            var panel=(PanelBlockEntity)level.getBlockEntity(CORE);
            var text=new ItemStack(EnergyControlPort.TEXT.get()); var data=CardItem.data(text);
            data.putString("text","ENERGY CONTROL\n3 x 2 MULTIBLOCK\nLive platform energy"); CardItem.update(text,data); panel.setItem(0,text);
            var energy=new ItemStack(EnergyControlPort.ENERGY.get()); data=CardItem.data(energy);
            data.putLong("target",BATTERY.asLong()); data.putString("dimension",level.dimension().location().toString()); data.putInt("side",Direction.NORTH.get3DDataValue());
            CardItem.update(energy,data); panel.setItem(1,energy);
            panel.setItem(2,new ItemStack(EnergyControlPort.TIME.get()));
            int i=0;
            for(Direction facing:Direction.values()) {
                BlockPos pos=new BlockPos(-5+i*2,1,4); i++;
                level.setBlockAndUpdate(pos,EnergyControlPort.BASIC.get().defaultBlockState().setValue(PanelBlock.FACING,facing));
                var p=(PanelBlockEntity)level.getBlockEntity(pos);
                var t=new ItemStack(EnergyControlPort.TEXT.get()); var d=CardItem.data(t); d.putString("text",facing.name()+"\n123 ABC"); CardItem.update(t,d); p.setItem(0,t);
            }
            log("SETUP fresh fixture, real platform energy capability on test chest");
        }
        level.setDayTime(6000); level.setWeatherParameters(100000,0,false,false);
        for(Direction face:Direction.values()) for(int x=-2;x<=2;x++) for(int y=-2;y<=2;y++) for(int z=-2;z<=2;z++) level.removeBlock(facePos(face.ordinal()).offset(x,y,z),false);
        for(Direction face:Direction.values()) {
            var pos=facePos(face.ordinal());
            level.setBlockAndUpdate(pos,EnergyControlPort.BASIC.get().defaultBlockState().setValue(PanelBlock.FACING,face));
            var p=(PanelBlockEntity)level.getBlockEntity(pos);
            for(int x=0;x<2;x++) for(int y=0;y<2;y++) if(x!=0 || y!=0)
                level.setBlockAndUpdate(p.at(x,y),EnergyControlPort.EXTENDER.get().defaultBlockState().setValue(PanelBlock.FACING,face));
            var card=new ItemStack(EnergyControlPort.TEXT.get());var d=CardItem.data(card);d.putString("text",face.name()+"\n2 x 2 BASIC\n123 ABC");CardItem.update(card,d);p.setItem(0,card);
        }
    }
    private static void tick(MinecraftServer server) {
        ticks++;
        var level=server.overworld();
        // Rebuilding a container fixture drops cards. Remove only those test
        // item entities so they cannot fall in front of a screenshot surface.
        level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new net.minecraft.world.phys.AABB(-16,-64,-16,16,20,16)).forEach(net.minecraft.world.entity.Entity::discard);
        for(var player:server.getPlayerList().getPlayers()) if(positioned.add(player.getUUID())) {
            player.teleportTo(level,1.5,0.5,-3.8,Set.of(),0,0);
            player.getAbilities().mayfly=true; player.getAbilities().flying=true; player.onUpdateAbilities();
            player.getInventory().setItem(0,new ItemStack(EnergyControlPort.ENERGY.get()));
            log("CLIENT_CONNECTED "+player.getGameProfile().getName());
        }
        for(var player:server.getPlayerList().getPlayers()) {
            int age=ages.merge(player.getUUID(),1,Integer::sum);
            if(age>=500 && age<=900 && (age-500)%80==0) {
                int index=(age-500)/80; Direction face=Direction.values()[index];
                var p=(PanelBlockEntity)level.getBlockEntity(facePos(index));
                var center=net.minecraft.world.phys.Vec3.atCenterOf(p.getBlockPos()).add(p.right().getStepX()*.5,p.right().getStepY()*.5,p.right().getStepZ()*.5)
                    .add(p.up().getStepX()*.5,p.up().getStepY()*.5,p.up().getStepZ()*.5);
                var eye=center.add(face.getStepX()*3.5,face.getStepY()*3.5,face.getStepZ()*3.5);
                float yaw=switch(face){case SOUTH->180;case EAST->90;case WEST->-90;default->0;};
                float pitch=face==Direction.UP?90:face==Direction.DOWN?-90:0;
                player.teleportTo(level,eye.x,eye.y-1.62,eye.z,Set.of(),yaw,pitch);
                log("VIEW "+face);
            }
        }
        var panel=(PanelBlockEntity)level.getBlockEntity(CORE);
        if(ticks==80) {
            check(panel.bounds().width()==3 && panel.bounds().height()==2,"3x2 advanced screen ownership");
            check(CORE.equals(((PanelBlockEntity)level.getBlockEntity(CORE.offset(2,1,0))).owner()),"far extender owner");
            check(panel.lines().stream().anyMatch(s->s.startsWith("Energy: ")),"real platform energy lookup");
            previous=panel.lines().stream().filter(s->s.contains(" / 100000 ")).findFirst().orElseThrow(); log("SAMPLE_A "+previous);
        }
        if(ticks==120) {
            log("SAMPLE_B "+panel.lines());
            check(!panel.lines().stream().filter(s->s.contains(" / 100000 ")).findFirst().orElseThrow().equals(previous),"live readings change on server tick");
        }
        if(ticks==140) level.removeBlock(CORE.offset(2,1,0),false);
        if(ticks==180) {
            check(panel.bounds().height()==1,"removed corner shrinks rectangle without holes");
            level.setBlockAndUpdate(CORE.offset(2,1,0),EnergyControlPort.ADVANCED_EXTENDER.get().defaultBlockState().setValue(PanelBlock.FACING,Direction.NORTH));
        }
        if(ticks==220) {
            check(panel.bounds().height()==2,"replaced extender rebuilds rectangle");
            for(Direction face:Direction.values()) {
                var p=(PanelBlockEntity)level.getBlockEntity(facePos(face.ordinal()));
                check(p.bounds().width()==2&&p.bounds().height()==2,"basic 2x2 grouping "+face);
            }
        }
        if(ticks==240) {while(panel.powerMode()!=3) panel.togglePower();}
        if(ticks==260) {check(!panel.powered(),"power mode off");panel.togglePower();}
        if(ticks==280) {check(!panel.powered(),"redstone mode without signal");level.setBlockAndUpdate(CORE.south(),Blocks.REDSTONE_BLOCK.defaultBlockState());}
        if(ticks==300) {check(panel.powered(),"redstone mode with signal");panel.togglePower();}
        if(ticks==320) {check(!panel.powered(),"inverted mode with signal");level.removeBlock(CORE.south(),false);}
        if(ticks==340) {check(panel.powered(),"inverted mode without signal");panel.togglePower();}
    }
}
