// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.block.PanelBlockEntity;
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
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.phys.*;
public final class StorageQaClient {
 private static boolean connecting;private static int ticks;private static String sample;
 public static void shot(Minecraft mc,String name){Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),m->QaServer.log("SCREENSHOT "+m.getString()));}
 private static void use(Minecraft mc,BlockPos p){mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(p).add(0,0,-.5),Direction.NORTH,p,false));}
 public static void shift(Minecraft mc,boolean down){mc.player.setShiftKeyDown(down);mc.player.connection.send(new net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket(mc.player,down?net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY:net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY));}
 public static void init(int port){
  QaServer.log("RUN_START storage-client "+java.time.Instant.now());
  ClientTickEvent.CLIENT_POST.register(mc->{
   mc.options.pauseOnLostFocus=false;
   if(!connecting&&mc.getOverlay()==null&&mc.screen!=null){connecting=true;ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:"+port),new ServerData("Storage QA","127.0.0.1:"+port,ServerData.Type.OTHER),false,null);}
   if(mc.level==null||mc.player==null||!(mc.level.getBlockEntity(StorageQaServer.CORE) instanceof PanelBlockEntity panel))return;
   ticks++;mc.getToasts().clear();
   if(ticks==60){QaServer.check(panel.lines().stream().anyMatch(v->v.contains("mB")),"client sees actual fluid array");sample=panel.lines().toString();shot(mc,"v3-01-arrays.png");}
   if(ticks==100){QaServer.check(!sample.equals(panel.lines().toString()),"client receives changing fluid array packets");mc.player.getInventory().selected=1;shift(mc,true);use(mc,StorageQaServer.tank(0));}
   if(ticks==115)use(mc,StorageQaServer.tank(1));
   if(ticks==125)shift(mc,false);
   if(ticks==145){QaServer.check(CardTargets.read(CardItem.data(mc.player.getMainHandItem())).size()==2,"two actual sneak-use interactions build array");use(mc,StorageQaServer.CORE);}
   if(ticks==170){QaServer.check(mc.player.containerMenu instanceof PanelMenu,"upgraded panel menu opens");var m=(PanelMenu)mc.player.containerMenu;mc.gameMode.handleInventoryMouseClick(m.containerId,m.cardSlots+3+27+1,0,ClickType.QUICK_MOVE,mc.player);}
   if(ticks==195){QaServer.check(panel.getItem(3).getItem() instanceof CardItem c&&c.array()&&mc.player.getInventory().getItem(1).isEmpty(),"normal shift-click inserts array card");shot(mc,"v3-02-upgrade-slots.png");mc.player.closeContainer();mc.player.getInventory().selected=0;}
   if(ticks==220)mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);
   if(ticks==245){QaServer.check(mc.player.containerMenu instanceof PortableMenu,"actual held-item interaction opens portable menu");NetworkManager.sendToServer(new PanelEditPayload(mc.player.containerMenu.containerId,0,"PORTABLE_V3"));}
   if(ticks==275){var m=(PortableMenu)mc.player.containerMenu;QaServer.check(m.lines.contains("PORTABLE_V3")&&m.lines.stream().anyMatch(v->v.contains("mB")),"portable title and live fluid data arrive over network");mc.gameMode.handleInventoryMouseClick(m.containerId,4+27,0,ClickType.QUICK_MOVE,mc.player);sample=m.lines.toString();shot(mc,"v3-03-portable.png");}
   if(ticks==310){var m=(PortableMenu)mc.player.containerMenu;QaServer.check(!sample.equals(m.lines.toString()),"portable live fluid refresh");QaServer.check(mc.player.getMainHandItem().getItem() instanceof PortablePanelItem,"open portable cannot move into its own inventory");mc.player.closeContainer();}
   if(ticks==330)mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);
   if(ticks==355){var m=(PortableMenu)mc.player.containerMenu;QaServer.check(CardItem.data(m.contents.getItem(0)).getString("title").equals("PORTABLE_V3"),"portable close and reopen retains data");shot(mc,"v3-04-portable-reopened.png");mc.player.closeContainer();}
   if(ticks==380){var h=(PanelBlockEntity)mc.level.getBlockEntity(StorageQaServer.HOLO);QaServer.check(h.holographic()&&h.bounds().width()==2,"client receives holographic group");shot(mc,"v3-05-holographic.png");}
   if(ticks==410){QaServer.log("ALL_STORAGE_CLIENT_SCENARIOS_DONE");mc.stop();}
  });
 }
}
