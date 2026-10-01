// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.card.*;
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
public final class IeSpecialQaClient {
 private static final BlockPos PANEL=new BlockPos(0,1,0),METER=new BlockPos(0,0,5);
 private static boolean connecting,sawZero;private static int ticks;
 private static final boolean observer=Boolean.getBoolean("ec.qa.observer"),reload=System.getProperty("ec.qa.stage","").equals("ie-special-reload");
 private static void click(Minecraft mc,String prefix){for(var w:mc.screen.children())if(w instanceof Button b&&b.active&&b.visible&&b.getMessage().getString().startsWith(prefix)){b.onPress();return;}throw new AssertionError("Missing button "+prefix);}
 private static boolean positive(PanelBlockEntity p){var lines=p.lines();int i=lines.indexOf("IE wire power (20-tick average)");return i>=0&&i+1<lines.size()&&Integer.parseInt(lines.get(i+1).split(" ")[0])>0;}
 public static void init(){QaServer.log("RUN_START IE-special client observer="+observer+" reload="+reload+" "+java.time.Instant.now());ClientTickEvent.CLIENT_POST.register(mc->{
  mc.options.pauseOnLostFocus=false;mc.options.getSoundSourceOptionInstance(net.minecraft.sounds.SoundSource.MASTER).set(0.0);org.lwjgl.glfw.GLFW.glfwHideWindow(mc.getWindow().getWindow());if(!connecting&&mc.getOverlay()==null&&mc.screen!=null){connecting=true;ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:25581"),new ServerData("Real IE power","127.0.0.1:25581",ServerData.Type.OTHER),false,null);}
  if(mc.level==null||mc.player==null||!(mc.level.getBlockEntity(PANEL) instanceof PanelBlockEntity p))return;if(!reload&&ticks==0&&mc.player.connection.getOnlinePlayers().size()<2)return;ticks++;mc.getToasts().clear();String role=observer?"observer":"editor";
  if(reload){if(ticks==120){QaServer.check(p.thickness()==8&&positive(p)&&p.rows().stream().anyMatch(r->r.isBar()),"IE-special resumed actual power and stored bar/case state reach client");StorageQaClient.shot(mc,"ie-special-reload.png");}if(ticks==150){QaServer.log("ALL_IE_SPECIAL_RELOAD_CLIENT_DONE");mc.stop();}return;}
  if(ticks==65){QaServer.check(positive(p)&&p.lines().contains("IE thermoelectric potential"),"both players see actual wire power and thermal potential");StorageQaClient.shot(mc,"ie-special-"+role+"-live.png");}
  if(ticks==80&&!observer)mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(PANEL),Direction.NORTH,PANEL,false));
  if(ticks==105&&!observer){QaServer.check(mc.player.containerMenu instanceof PanelMenu,"IE special panel normal menu opens");click(mc,"Card");click(mc,"Card");click(mc,"Bars");}
  if(ticks==135)QaServer.check(p.rows().stream().noneMatch(r->r.isBar()),"real capacitor bar toggle synchronizes to both players");
  if(ticks==145&&!observer){click(mc,"Bars");click(mc,"Layout");for(int i=0;i<8;i++)click(mc,"Thickness");}
  if(ticks==180){QaServer.check(p.thickness()==8&&p.rows().stream().anyMatch(r->r.isBar()),"restored real storage bar and thickness synchronize to both players");StorageQaClient.shot(mc,"ie-special-"+role+"-controls.png");if(!observer)mc.player.closeContainer();}
  if(ticks>240&&ticks<315&&p.lines().stream().filter(s->s.equals("0 FE/t")).count()==2)sawZero=true;
  if(ticks==310){QaServer.check(sawZero,"both clients see actual wire stop and removed heat source as zero");StorageQaClient.shot(mc,"ie-special-"+role+"-stopped.png");}
  if(ticks==410){QaServer.check(positive(p),"both clients see restored actual power transfer");StorageQaClient.shot(mc,"ie-special-"+role+"-restored.png");if(!observer){mc.player.connection.sendCommand("tp @s 0 0.5 2 0 0");mc.player.getInventory().selected=1;}}
  if(ticks==445&&!observer){StorageQaClient.shift(mc,true);var pos=METER.above();mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos),Direction.NORTH,pos,false));}
  if(ticks==450&&!observer)StorageQaClient.shift(mc,false);
  if(ticks==475&&!observer){var targets=CardTargets.read(CardItem.data(mc.player.getMainHandItem()));QaServer.check(targets.size()==1&&targets.getFirst().position()==METER.above().asLong(),"actual sneak-use binds machine card to meter dummy block");StorageQaClient.shot(mc,"ie-special-wire-circuit.png");}
  if(ticks==490&&!observer){mc.player.connection.sendCommand("gamemode survival @s");mc.player.getInventory().selected=2;}
  if(ticks==515&&!observer){var pos=METER.above();mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos),Direction.NORTH,pos,false));}
  if(ticks==555&&!observer){QaServer.check(mc.player.getInventory().getItem(2).getCount()==1,"machine kit consumes exactly one on actual IE meter in survival");int cards=0;for(var stack:mc.player.getInventory().items)if(stack.getItem() instanceof CardItem c&&c.kind()==CardItem.Kind.MACHINE&&CardTargets.read(CardItem.data(stack)).stream().anyMatch(t->t.position()==METER.above().asLong()))cards+=stack.getCount();QaServer.check(cards==2,"IE machine kit drops second correctly bound machine card");StorageQaClient.shot(mc,"ie-machine-kit-survival.png");}
  if(ticks==620){QaServer.log("ALL_IE_SPECIAL_CLIENT_DONE "+role);mc.stop();}
 });}
}
