// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.core.ProjectionSettings;
import com.zuxelus.energycontrol.port.menu.PanelMenu;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.*;
import java.util.Locale;
public final class DisplayQaClient {
 private static boolean connecting,changed;private static int ticks,initialPage;
 private static final boolean reload=System.getProperty("ec.qa.stage","").equals("pages-reload");
 private static void open(Minecraft mc,BlockPos p){mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(p),Direction.NORTH,p,false));}
 private static void click(Minecraft mc,String prefix){for(var w:mc.screen.children())if(w instanceof Button b&&b.visible&&b.active&&b.getMessage().getString().startsWith(prefix)){b.onPress();return;}throw new AssertionError("Missing active button: "+prefix);}
 public static void init(int port){QaServer.log("RUN_START display-client reload="+reload+" "+java.time.Instant.now());ClientTickEvent.CLIENT_POST.register(mc->{
  mc.options.pauseOnLostFocus=false;mc.options.getSoundSourceOptionInstance(net.minecraft.sounds.SoundSource.MASTER).set(0.0);if(!connecting&&mc.getOverlay()==null&&mc.screen!=null){connecting=true;ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:"+port),new ServerData("Display acceptance","127.0.0.1:"+port,ServerData.Type.OTHER),false,null);}
  if(mc.level==null||mc.player==null||!(mc.level.getBlockEntity(DisplayQaServer.CORE) instanceof PanelBlockEntity p)||!(mc.level.getBlockEntity(DisplayQaServer.HOLO) instanceof PanelBlockEntity h))return;ticks++;mc.getToasts().clear();
  if(reload){if(ticks==60){QaServer.check(p.pageSize()==8&&p.pageTicks()==0&&h.projection().equals(new ProjectionSettings(8,28,21)),"display client receives persisted page and projection settings");StorageQaClient.shot(mc,"display-reload-world.png");open(mc,DisplayQaServer.HOLO);}if(ticks==90)click(mc,"Layout");if(ticks==100)StorageQaClient.shot(mc,"display-reload-menu.png");if(ticks==120){QaServer.log("ALL_DISPLAY_RELOAD_DONE");mc.stop();}return;}
  if(ticks==40)QaServer.check(p.pageCount()==2&&p.lines().size()==32,"display initial page metadata and32 lines reach client");
  if(ticks==60)open(mc,DisplayQaServer.CORE);
  if(ticks==85){QaServer.check(mc.player.containerMenu instanceof PanelMenu,"display normal configuration menu opens");click(mc,"Layout");}
  if(ticks==120)click(mc,"Next");
  if(ticks==145){QaServer.check(p.page()==1&&p.lines().contains("CARD 3 LINE 2"),"display next page reveals previously truncated source lines");StorageQaClient.shot(mc,"display-01-page-two.png");}
  if(ticks==160)click(mc,"Lines");
  if(ticks==190)QaServer.check(p.pageSize()==4&&p.pageCount()==10&&p.lines().size()==4,"display configurable page size recomputes page count");
  if(ticks==200)click(mc,"Auto");
  if(ticks==225)initialPage=p.page();if(ticks>225&&ticks<330&&p.page()!=initialPage)changed=true;
  if(ticks==330){QaServer.check(changed&&p.pageTicks()==40,"display server-driven auto paging changes client page");for(int i=0;i<3;i++)click(mc,"Auto");}
  if(ticks==350)click(mc,"Lines");
  if(ticks==380){QaServer.check(p.pageSize()==8&&p.pageTicks()==0,"display auto paging can stop with manual eight-line pages");mc.player.closeContainer();open(mc,DisplayQaServer.HOLO);}
  if(ticks==410){QaServer.check(mc.player.containerMenu instanceof PanelMenu m&&m.panel.holographic(),"display real holographic menu opens");click(mc,"Layout");}
  if(ticks==425){for(int i=0;i<4;i++)click(mc,"Pitch");for(int i=0;i<3;i++)click(mc,"Yaw");for(int i=0;i<8;i++)click(mc,"Depth");click(mc,"Lines");}
  if(ticks==460){QaServer.check(h.projection().equals(new ProjectionSettings(8,28,21))&&h.pageSize()==4,"display GUI changes actual synchronized holographic projection");StorageQaClient.shot(mc,"display-02-projection-menu.png");}
  if(ticks==480)mc.player.closeContainer();if(ticks==500)StorageQaClient.shot(mc,"display-03-tilted-group.png");
  for(int i=0;i<6;i++){
   if(ticks==540+i*70){var f=(PanelBlockEntity)mc.level.getBlockEntity(DisplayQaServer.face(i));var face=Direction.values()[i];var center=Vec3.atCenterOf(f.getBlockPos()).add(f.right().getStepX()*.5+f.up().getStepX()*.5,f.right().getStepY()*.5+f.up().getStepY()*.5,f.right().getStepZ()*.5+f.up().getStepZ()*.5);var eye=center.add(face.getStepX()*5,face.getStepY()*5,face.getStepZ()*5);int yaw=switch(face){case SOUTH->180;case EAST->90;case WEST->-90;default->0;};int pitch=face==Direction.UP?90:face==Direction.DOWN?-90:0;mc.player.connection.sendCommand(String.format(Locale.ROOT,"tp @s %.3f %.3f %.3f %d %d",eye.x,eye.y-1.62,eye.z,yaw,pitch));}
   if(ticks==590+i*70){var f=(PanelBlockEntity)mc.level.getBlockEntity(DisplayQaServer.face(i));QaServer.check(f.projection().equals(new ProjectionSettings(8,28,21))&&f.bounds().width()==2&&f.bounds().height()==2,"display tilted two-by-two projection facing "+Direction.values()[i]);StorageQaClient.shot(mc,"display-face-"+Direction.values()[i]+".png");}
  }
  if(ticks==980){QaServer.log("ALL_DISPLAY_CLIENT_DONE");mc.stop();}
 });}
}
