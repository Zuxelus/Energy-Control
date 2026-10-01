// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.menu.PanelMenu;
import com.zuxelus.energycontrol.port.network.PanelEditPayload;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.*;
public final class QaClient {
    private static boolean connecting; private static int ticks; private static String sample;
    public static void init(int port) {
        QaServer.log("RUN_START client "+java.time.Instant.now());
        ClientTickEvent.CLIENT_POST.register(mc -> {
            // The user can keep working in other windows while these task-owned
            // runs capture frames; losing focus must not cover them with PauseScreen.
            mc.options.pauseOnLostFocus=false;
            if(!connecting && mc.getOverlay()==null && mc.screen!=null) {
                QaServer.log("CONNECT_FROM "+mc.screen.getClass().getSimpleName());
                connecting=true;
                ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:"+port),
                    new ServerData("Isolated Energy Control QA","127.0.0.1:"+port,ServerData.Type.OTHER),false,null);
            }
            if(mc.level==null || mc.player==null || !(mc.level.getBlockEntity(QaServer.CORE) instanceof PanelBlockEntity panel)) return;
            ticks++;
            mc.getToasts().clear();
            if(ticks==40) {
                mc.player.setShiftKeyDown(true);
                mc.player.connection.send(new net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket(mc.player,net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY));
                mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(QaServer.BATTERY).add(0,0,-.5),Direction.NORTH,QaServer.BATTERY,false));
            }
            if(ticks==42) {mc.player.setShiftKeyDown(false);mc.player.connection.send(new net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket(mc.player,net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY));}
            if(ticks==70) QaServer.check(com.zuxelus.energycontrol.port.card.CardItem.data(mc.player.getMainHandItem()).getLong("target")==QaServer.BATTERY.asLong(),"sneak-use binds card through actual client interaction");
            if(ticks==80) {
                QaServer.check(panel.bounds().width()==3 && panel.bounds().height()==2,"client receives multiblock bounds");
                sample=panel.lines().stream().filter(s->s.contains(" / 100000 ")).findFirst().orElse("");
                QaServer.check(!sample.isEmpty(),"client receives live energy lines");
            }
            if(ticks==120) { QaServer.check(!sample.equals(panel.lines().stream().filter(s->s.contains(" / 100000 ")).findFirst().orElse("")),"client receives changing energy packets"); shot(mc,"01-live-multiblock.png"); }
            if(ticks==160) mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(QaServer.CORE).add(0,0,-.5),Direction.NORTH,QaServer.CORE,false));
            if(ticks==185) {
                QaServer.check(mc.player.containerMenu instanceof PanelMenu,"real right-click opens network menu");
                editUsingWidgets(mc,"PERSISTED\nEnergy Control 1.21.1\nLIVE NETWORK EDIT");
            }
            if(ticks==210) { shot(mc,"02-card-menu.png"); QaServer.check(panel.lines().contains("PERSISTED"),"C2S edit updates S2C render data"); }
            if(ticks==240) mc.player.closeContainer();
            if(ticks==280) { shot(mc,"03-edited-multiblock.png"); QaServer.log("CLIENT_QA_DONE"); }
            if(ticks==320) {
                mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(QaServer.CORE),Direction.NORTH,QaServer.CORE,false));
            }
            if(ticks==340) {
                var menu=(PanelMenu)mc.player.containerMenu;
                if(panel.scalePercent()!=125) mc.gameMode.handleInventoryButtonClick(menu.containerId,2);
                if(panel.alignment()!=1) mc.gameMode.handleInventoryButtonClick(menu.containerId,3);
                if(!panel.uniformFont()) mc.gameMode.handleInventoryButtonClick(menu.containerId,4);
                if(panel.refreshTicks()!=40) mc.gameMode.handleInventoryButtonClick(menu.containerId,5);
                if(panel.background()!=0x303840) mc.gameMode.handleInventoryButtonClick(menu.containerId,6);
            }
            if(ticks==370) { QaServer.check(panel.scalePercent()!=100 && panel.uniformFont(),"display settings synchronized");shot(mc,"04-display-settings.png");mc.player.closeContainer(); }
            if(ticks==400) shot(mc,"05-styled-live-panel.png");
            if(ticks==410) mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(QaServer.CORE),Direction.NORTH,QaServer.CORE,false));
            if(ticks==430) {
                var menu=(PanelMenu)mc.player.containerMenu;
                NetworkManager.sendToServer(new PanelEditPayload(menu.containerId,1,"Capacitor A"));
                if(!com.zuxelus.energycontrol.port.card.CardItem.data(menu.getSlot(1).getItem()).getBoolean("hidePercent")) mc.gameMode.handleInventoryButtonClick(menu.containerId,103);
            }
            if(ticks==460) {QaServer.check(panel.lines().contains("Capacitor A") && panel.lines().stream().noneMatch(s->s.startsWith("Stored:")),"sensor card title and field mask synchronize");mc.player.closeContainer();}
            if(ticks==470) NetworkManager.sendToServer(new PanelEditPayload(-1,0,"INVALID CLOSED MENU"));
            if(ticks==490) QaServer.check(panel.lines().contains("PERSISTED"),"stale menu packet rejected");
            if(ticks>=530 && ticks<=930 && (ticks-530)%80==0) {
                int index=(ticks-530)/80;
                var facePanel=(PanelBlockEntity)mc.level.getBlockEntity(QaServer.facePos(index));
                QaServer.log("FACE_DATA "+Direction.values()[index]+" "+facePanel.lines());
                shot(mc,"face-"+Direction.values()[index].name()+".png");
            }
            if(ticks==970) {QaServer.log("ALL_CLIENT_SCENARIOS_DONE");mc.stop();}
        });
    }
    private static void editUsingWidgets(Minecraft mc,String value) {
        for(var child:mc.screen.children()) if(child instanceof net.minecraft.client.gui.components.MultiLineEditBox edit) edit.setValue(value);
        for(var child:mc.screen.children()) if(child instanceof net.minecraft.client.gui.components.Button button && button.getMessage().getString().equals("Save text")) button.onPress();
    }
    private static void shot(Minecraft mc,String name) {
        Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),message->QaServer.log("SCREENSHOT "+message.getString()));
    }
}
