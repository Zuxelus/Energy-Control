// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.block.*;
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
public final class SlopesQaClient {
 private static boolean connecting;private static int ticks;private static java.util.List<String> sample;
 private static final boolean observer=Boolean.getBoolean("ec.qa.observer"),reload=System.getProperty("ec.qa.stage","").endsWith("reload");
 private static void click(Minecraft mc,String prefix){for(var w:mc.screen.children())if(w instanceof Button b&&b.visible&&b.active&&b.getMessage().getString().startsWith(prefix)){b.onPress();return;}throw new AssertionError("Missing button "+prefix);}
 public static void init(int port){QaServer.log("RUN_START slopes-client observer="+observer+" reload="+reload+" "+java.time.Instant.now());ClientTickEvent.CLIENT_POST.register(mc->{
  mc.options.pauseOnLostFocus=false;mc.options.getSoundSourceOptionInstance(net.minecraft.sounds.SoundSource.MASTER).set(0.0);org.lwjgl.glfw.GLFW.glfwHideWindow(mc.getWindow().getWindow());if(!connecting&&mc.getOverlay()==null&&mc.screen!=null){connecting=true;ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:"+port),new ServerData("Slopes QA","127.0.0.1:"+port,ServerData.Type.OTHER),false,null);}
  if(mc.level==null||mc.player==null||!(mc.level.getBlockEntity(SlopesQaServer.CORE) instanceof PanelBlockEntity p))return;
  if(!reload&&ticks==0&&mc.player.connection.getOnlinePlayers().size()<2)return;ticks++;mc.getToasts().clear();String role=observer?"observer":"editor";
  if(reload){if(ticks==1)mc.player.connection.sendCommand("tp @s -0.5 0.2 -3.5 0 0");if(ticks==60){QaServer.check(p.slopeHorizontal()==8&&p.slopeVertical()==4&&p.thickness()==8,"reconnected client gets persisted slopes and thickness");QaServer.check(SlopesQaServer.continuous(p),"reconnected client gets continuous core and extender plane");QaServer.check(p.caseShape().toAabbs().size()>1,"reloaded collision remains sloped");sample=p.lines();StorageQaClient.shot(mc,"slopes-reload-world.png");}if(ticks==110){QaServer.check(!sample.equals(p.lines()),"reloaded sloped screen resumes real world time updates");QaServer.log("ALL_SLOPES_RELOAD_CLIENT_DONE");mc.stop();}return;}
  if(ticks==40)mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(p.getBlockPos()),Direction.NORTH,p.getBlockPos(),false));
  if(ticks==65)QaServer.check(mc.player.containerMenu instanceof PanelMenu,"both players open normal slope configuration menu");
  if(ticks==80&&!observer){click(mc,"Layout");for(int i=0;i<8;i++){click(mc,"Thickness");click(mc,"Slope H");}for(int i=0;i<4;i++)click(mc,"Slope V");}
  if(ticks==130){QaServer.check(p.thickness()==8&&p.slopeHorizontal()==8&&p.slopeVertical()==4,"both clients receive normal GUI slopes and thickness");QaServer.check(SlopesQaServer.continuous(p),"both clients receive continuous grouped plane");sample=p.lines();StorageQaClient.shot(mc,"slopes-"+role+"-menu.png");mc.player.closeContainer();}
  if(ticks==165){QaServer.check(!sample.equals(p.lines()),"sloped screen text updates with real synchronized world time");StorageQaClient.shot(mc,"slopes-"+role+"-world.png");}
  if(ticks==190&&!observer)mc.gameMode.startDestroyBlock(p.at(1,1),Direction.NORTH);
  if(ticks==230)QaServer.check(mc.level.getBlockState(p.at(1,1)).isAir()&&p.bounds().height()==1,"ordinary unprotected break detaches sloped extender on both clients");
  if(ticks==250&&!observer){mc.player.getInventory().selected=1;StorageQaClient.shift(mc,true);var support=p.at(1,0);mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(support).add(0,.5,0),Direction.UP,support,false));}
  if(ticks==255&&!observer)StorageQaClient.shift(mc,false);
  if(ticks==300){QaServer.check(p.bounds().height()==2&&mc.level.getBlockState(p.at(1,1)).getValue(PanelBlock.SLOPED)&&SlopesQaServer.continuous(p),"normal replacement inherits continuous solid slope");StorageQaClient.shot(mc,"slopes-"+role+"-rebuilt.png");}
  if(!observer)for(int i=0;i<6;i++){
   if(ticks==430+i*75){var f=(PanelBlockEntity)mc.level.getBlockEntity(SlopesQaServer.face(i));var face=Direction.values()[i];var center=Vec3.atCenterOf(f.getBlockPos()).add(f.right().getStepX()*.5+f.up().getStepX()*.5-face.getStepX()*.25,f.right().getStepY()*.5+f.up().getStepY()*.5-face.getStepY()*.25,f.right().getStepZ()*.5+f.up().getStepZ()*.5-face.getStepZ()*.25);var eye=center.add(face.getStepX()*4+f.right().getStepX()*2+f.up().getStepX()*.7,face.getStepY()*4+f.right().getStepY()*2+f.up().getStepY()*.7,face.getStepZ()*4+f.right().getStepZ()*2+f.up().getStepZ()*.7);var delta=center.subtract(eye);double yaw=Math.toDegrees(Math.atan2(-delta.x,delta.z)),pitch=-Math.toDegrees(Math.atan2(delta.y,Math.hypot(delta.x,delta.z)));mc.player.connection.sendCommand(String.format(Locale.ROOT,"tp @s %.3f %.3f %.3f %.3f %.3f",eye.x,eye.y-1.62,eye.z,yaw,pitch));}
   if(ticks==485+i*75){var f=(PanelBlockEntity)mc.level.getBlockEntity(SlopesQaServer.face(i));QaServer.check(f.thickness()==8&&f.slopeHorizontal()==4&&f.slopeVertical()==-4&&SlopesQaServer.continuous(f)&&f.caseShape().toAabbs().size()>1,"solid body text plane and collision synchronized facing "+f.facing());StorageQaClient.shot(mc,"slopes-face-"+f.facing()+".png");}
  }
  if(ticks==920){QaServer.log("ALL_SLOPES_CLIENT_DONE "+role);mc.stop();}
 });}
}
