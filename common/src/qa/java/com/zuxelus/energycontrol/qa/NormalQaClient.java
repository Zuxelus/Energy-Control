// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.*;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.card.*;
import com.zuxelus.energycontrol.port.menu.*;
import com.zuxelus.energycontrol.port.network.PanelEditPayload;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.*;
public final class NormalQaClient {
 private static boolean connecting,positioned;private static int ticks, positioningTicks;
 private static final boolean observer=Boolean.getBoolean("ec.qa.observer"),reload=System.getProperty("ec.qa.stage","").equals("reload");
 private static final String marker="NORMAL_SYNC_"+System.currentTimeMillis();private static String initial;
 private static void panel(Minecraft mc){mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(StorageQaServer.CORE),Direction.NORTH,StorageQaServer.CORE,false));}
 public static void init(int port){
  QaServer.log("RUN_START normal-client "+(observer?"observer":"editor")+" reload="+reload+" "+java.time.Instant.now());
  ClientTickEvent.CLIENT_POST.register(mc->{
   mc.options.pauseOnLostFocus=false;
   if(!connecting&&mc.getOverlay()==null&&mc.screen!=null){connecting=true;ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:"+port),new ServerData("Normal gameplay","127.0.0.1:"+port,ServerData.Type.OTHER),false,null);}
   if(mc.level==null||mc.player==null)return;
   if(!positioned || mc.player.position().distanceToSqr(new Vec3(2,.5,-4))>4){positioned=true;if(positioningTicks++%40==0)mc.player.connection.sendCommand("tp @s 2 0.5 -4 0 0");return;}
   if(!(mc.level.getBlockEntity(StorageQaServer.CORE) instanceof PanelBlockEntity p))return;
   if(!reload && ticks==0 && (mc.player.connection.getOnlinePlayers().size()<2 || mc.level.players().stream().filter(v->v.position().distanceToSqr(new Vec3(2,.5,-4))<4).count()<2))return;
   ticks++;mc.getToasts().clear();
   if(ticks==1)initial=CardItem.data(p.getItem(0)).getString("text");
   if(reload){
    if(ticks==60){QaServer.check(initial.startsWith("NORMAL_SYNC_"),"production server disk reload retains multiplayer edit");QaServer.check(p.bounds().width()==3&&p.bounds().height()==2&&p.range()==128,"production reload retains rebuilt group and upgrades");mc.player.getInventory().selected=0;mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}
    if(ticks==100){var m=(PortableMenu)mc.player.containerMenu;QaServer.check(CardItem.data(m.contents.getItem(0)).getString("title").equals("NORMAL_PORTABLE_V3"),"production player disk reload retains portable card title");StorageQaClient.shot(mc,"normal-reload-portable.png");mc.player.closeContainer();}
    if(ticks==130){StorageQaClient.shot(mc,"normal-reload-world.png");QaServer.log("ALL_NORMAL_RELOAD_DONE");mc.stop();}return;
   }
   if(ticks==40)panel(mc);
   if(ticks==65)QaServer.check(mc.player.containerMenu instanceof PanelMenu,"simultaneous players open normal panel menu");
   if(ticks==80&&!observer)QaClient.editUsingWidgets(mc,marker);
   if(ticks==120){String value=CardItem.data(((PanelMenu)mc.player.containerMenu).getSlot(0).getItem()).getString("text");QaServer.check(value.startsWith("NORMAL_SYNC_")&&!value.equals(initial)&&p.lines().contains(value),"simultaneous player receives real menu item and world text update");StorageQaClient.shot(mc,observer?"normal-observer-menu.png":"normal-editor-menu.png");mc.player.closeContainer();}
   if(ticks==150&&!observer){mc.gameMode.startDestroyBlock(StorageQaServer.CORE.offset(2,1,0),Direction.NORTH);mc.player.connection.sendCommand("give @s energycontrol:info_panel_advanced_extender");}
   if(ticks==185)QaServer.check(mc.level.getBlockState(StorageQaServer.CORE.offset(2,1,0)).isAir()&&p.bounds().height()==1,"normal creative break succeeds and synchronizes without protection");
   if(ticks==210&&!observer){
    for(int i=0;i<9;i++)if(mc.player.getInventory().getItem(i).is(EnergyControlPort.ADVANCED_EXTENDER.get().asItem())){mc.player.getInventory().selected=i;break;}
    StorageQaClient.shift(mc,true);var support=StorageQaServer.CORE.offset(2,0,0);mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(support).add(0,.5,0),Direction.UP,support,false));
   }
   if(ticks==215&&!observer)StorageQaClient.shift(mc,false);
   if(ticks==250){QaServer.check(p.bounds().height()==2&&mc.level.getBlockEntity(StorageQaServer.CORE.offset(2,1,0)) instanceof PanelBlockEntity,"normal block placement rebuilds group on both clients");StorageQaClient.shot(mc,observer?"normal-observer-world.png":"normal-editor-world.png");}
   if(!observer){
    if(ticks==270){mc.player.getInventory().selected=0;mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}
    if(ticks==295){QaServer.check(mc.player.containerMenu instanceof PortableMenu,"portable opens on server without QA entrypoints");NetworkManager.sendToServer(new PanelEditPayload(mc.player.containerMenu.containerId,0,"NORMAL_PORTABLE_V3"));}
    if(ticks==325){var m=(PortableMenu)mc.player.containerMenu;QaServer.check(m.lines.contains("NORMAL_PORTABLE_V3"),"normal portable edit synchronizes");mc.player.closeContainer();}
    if(ticks==345)mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);
    if(ticks==370){QaServer.check(CardItem.data(((PortableMenu)mc.player.containerMenu).contents.getItem(0)).getString("title").equals("NORMAL_PORTABLE_V3"),"normal portable close-reopen retains card");StorageQaClient.shot(mc,"normal-portable.png");mc.player.closeContainer();}
   }
   if(ticks==400){QaServer.log("ALL_NORMAL_MULTIPLAYER_DONE "+(observer?"observer":"editor"));mc.stop();}
  });
 }
}
