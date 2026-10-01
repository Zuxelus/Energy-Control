// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.card.CardItem;
import com.zuxelus.energycontrol.port.menu.*;
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
public final class GraphicsQaClient {
 private static boolean connecting;private static int ticks,sample=-1;
 private static final boolean observer=Boolean.getBoolean("ec.qa.observer"),reload=System.getProperty("ec.qa.stage","").equals("graphics-reload");
 private static void open(Minecraft mc){var pos=GraphicsQaServer.CORE;mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos),Direction.NORTH,pos,false));}
 private static void click(Minecraft mc,String prefix){for(var w:mc.screen.children())if(w instanceof Button b&&b.visible&&b.active&&b.getMessage().getString().startsWith(prefix)){b.onPress();return;}throw new AssertionError("Missing button "+prefix);}
 public static void init(int port){QaServer.log("RUN_START graphics-client observer="+observer+" reload="+reload+" "+java.time.Instant.now());ClientTickEvent.CLIENT_POST.register(mc->{
  mc.options.pauseOnLostFocus=false;org.lwjgl.glfw.GLFW.glfwHideWindow(mc.getWindow().getWindow());if(!connecting&&mc.getOverlay()==null&&mc.screen!=null){connecting=true;ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:"+port),new ServerData("Graphics QA","127.0.0.1:"+port,ServerData.Type.OTHER),false,null);}
  if(mc.level==null||mc.player==null||!(mc.level.getBlockEntity(GraphicsQaServer.CORE) instanceof PanelBlockEntity p))return;
  if(!reload&&ticks==0&&mc.player.connection.getOnlinePlayers().size()<2)return;ticks++;mc.getToasts().clear();
  String role=observer?"observer":"editor";
  if(reload){
   if(ticks==60){QaServer.check(p.thickness()==8&&p.rows().stream().filter(r->r.isBar()).count()==2,"reconnected client receives live bars and saved case");QaServer.check(mc.level.getBlockState(p.at(1,1)).getValue(PanelBlock.THICKNESS)==8,"reconnected client gets saved extender block state");StorageQaClient.shot(mc,"graphics-reload-world.png");mc.player.getInventory().selected=0;mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}
   if(ticks==100){var m=(PortableMenu)mc.player.containerMenu;QaServer.check(CardItem.data(m.contents.getItem(0)).getBoolean("showBars")&&m.barFills.stream().anyMatch(n->n>=0),"portable component bar setting and numeric payload survive disk reload");StorageQaClient.shot(mc,"graphics-reload-portable.png");mc.player.closeContainer();}
   if(ticks==130){QaServer.log("ALL_GRAPHICS_RELOAD_DONE");mc.stop();}return;
  }
  if(ticks==40)open(mc);
  if(ticks==65)QaServer.check(mc.player.containerMenu instanceof PanelMenu,"both players open actual normal panel menu");
  if(ticks==80&&!observer){click(mc,"Bars");click(mc,"Card");click(mc,"Bars");click(mc,"Layout");for(int i=0;i<8;i++)click(mc,"Thickness");}
  if(ticks==130){QaServer.check(p.rows().stream().filter(r->r.isBar()).count()==2,"both clients receive energy and fluid numeric bars");QaServer.check(p.thickness()==8&&mc.level.getBlockState(p.at(1,1)).getValue(PanelBlock.THICKNESS)==8,"both clients receive core and extender physical thickness");sample=p.rows().stream().filter(r->r.isBar()).findFirst().orElseThrow().fill();StorageQaClient.shot(mc,"graphics-"+role+"-menu.png");mc.player.closeContainer();}
  if(ticks==165){QaServer.check(p.rows().stream().filter(r->r.isBar()).findFirst().orElseThrow().fill()!=sample,"bar fill changes with real synchronized capability reading");StorageQaClient.shot(mc,"graphics-"+role+"-world.png");}
  if(ticks==190&&!observer)mc.gameMode.startDestroyBlock(p.at(1,1),Direction.NORTH);
  if(ticks==230)QaServer.check(mc.level.getBlockState(p.at(1,1)).isAir()&&p.bounds().height()==1,"ordinary creative break detaches thin extender on both clients");
  if(ticks==250&&!observer){mc.player.getInventory().selected=1;StorageQaClient.shift(mc,true);var support=p.at(1,0);mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(support).add(0,.5,0),Direction.UP,support,false));}
  if(ticks==255&&!observer)StorageQaClient.shift(mc,false);
  if(ticks==300){QaServer.check(p.bounds().height()==2&&mc.level.getBlockState(p.at(1,1)).getValue(PanelBlock.THICKNESS)==8,"normal replacement joins group and inherits physical thickness");StorageQaClient.shot(mc,"graphics-"+role+"-rebuilt.png");if(!observer){mc.player.getInventory().selected=0;mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}}
  if(ticks==330&&!observer){QaServer.check(mc.player.containerMenu instanceof PortableMenu,"portable screen opens normally");click(mc,"Bars");}
  if(ticks==370&&!observer){var m=(PortableMenu)mc.player.containerMenu;QaServer.check(m.barFills.stream().anyMatch(n->n>=0)&&m.lines.size()==m.barFills.size(),"portable receives matching typed bar and text rows");StorageQaClient.shot(mc,"graphics-portable-bars.png");mc.player.closeContainer();}
  if(!observer)for(int i=0;i<6;i++){
   if(ticks==430+i*75){var f=(PanelBlockEntity)mc.level.getBlockEntity(GraphicsQaServer.face(i));var face=Direction.values()[i];var center=Vec3.atCenterOf(f.getBlockPos()).add(f.right().getStepX()*.5+f.up().getStepX()*.5-face.getStepX()*.25,f.right().getStepY()*.5+f.up().getStepY()*.5-face.getStepY()*.25,f.right().getStepZ()*.5+f.up().getStepZ()*.5-face.getStepZ()*.25);var eye=center.add(face.getStepX()*4+f.right().getStepX()*2+f.up().getStepX()*.7,face.getStepY()*4+f.right().getStepY()*2+f.up().getStepY()*.7,face.getStepZ()*4+f.right().getStepZ()*2+f.up().getStepZ()*.7);var delta=center.subtract(eye);double yaw=Math.toDegrees(Math.atan2(-delta.x,delta.z)),pitch=-Math.toDegrees(Math.atan2(delta.y,Math.hypot(delta.x,delta.z)));mc.player.connection.sendCommand(String.format(Locale.ROOT,"tp @s %.3f %.3f %.3f %.3f %.3f",eye.x,eye.y-1.62,eye.z,yaw,pitch));}
   if(ticks==485+i*75){var f=(PanelBlockEntity)mc.level.getBlockEntity(GraphicsQaServer.face(i));var shape=f.getBlockState().getCollisionShape(mc.level,f.getBlockPos()).bounds();var axis=f.facing().getAxis();QaServer.check(f.thickness()==4&&f.bounds().width()==2&&Math.abs(shape.max(axis)-shape.min(axis)-.25)<1e-6,"thin solid model and collision synchronized facing "+f.facing());StorageQaClient.shot(mc,"graphics-face-"+f.facing()+".png");}
  }
  if(ticks==920){QaServer.log("ALL_GRAPHICS_CLIENT_DONE "+role);mc.stop();}
 });}
}
