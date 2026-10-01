// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.*;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.card.*;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.*;
public final class IeKitQaClient {
 private static boolean connecting;private static int ticks,phase,doneAt;
 private static final BlockPos PANEL=new BlockPos(0,1,0),TARGET=new BlockPos(0,1,5);
 public static void init(){QaServer.log("RUN_START IE-kit client "+java.time.Instant.now());ClientTickEvent.CLIENT_POST.register(mc->{
  mc.options.pauseOnLostFocus=false;mc.options.getSoundSourceOptionInstance(net.minecraft.sounds.SoundSource.MASTER).set(0.0);org.lwjgl.glfw.GLFW.glfwHideWindow(mc.getWindow().getWindow());if(!connecting&&mc.getOverlay()==null&&mc.screen!=null){connecting=true;ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:25581"),new ServerData("IE kit QA","127.0.0.1:25581",ServerData.Type.OTHER),false,null);}
  if(mc.screen!=null||mc.level==null||mc.player==null||!(mc.level.getBlockEntity(PANEL) instanceof PanelBlockEntity p))return;ticks++;mc.getToasts().clear();if(ticks>900)throw new AssertionError("IE kit scenario timed out waiting for server response");
  if(phase==0&&p.lines().contains("IE wire power (20-tick average)")&&p.lines().stream().anyMatch(s->s.endsWith(" FE/t")&&!s.equals("0 FE/t"))){QaServer.check(true,"actual IE machine rows reach client before kit interaction");mc.player.connection.sendCommand("tp @s 0.0 0.5 2.0 0 0");mc.player.connection.sendCommand("gamemode survival @s");phase=1;}
  if(phase==1&&Math.abs(mc.player.getZ()-2)<.1&&!mc.player.getAbilities().instabuild&&mc.player.getInventory().getItem(2).getCount()==2){mc.player.getInventory().selected=2;mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(TARGET),Direction.NORTH,TARGET,false));phase=2;}
  if(phase==2&&mc.player.getInventory().getItem(2).getCount()==1){var cards=mc.player.getInventory().items.stream().filter(stack->stack.is(EnergyControlPort.MACHINE.get())&&CardTargets.read(CardItem.data(stack)).stream().anyMatch(t->t.position()==TARGET.asLong())).toList();if(cards.size()==1){QaServer.check(true,"real IE meter kit consumes exactly1 in survival");QaServer.check(CardTargets.read(CardItem.data(cards.getFirst())).getFirst().side()==Direction.NORTH.get3DDataValue(),"dropped machine card retains actual IE target face");QaServer.check(mc.player.containerMenu==mc.player.inventoryMenu,"successful kit use does not open machine GUI");phase=3;doneAt=ticks;}}
  if(phase==3&&ticks-doneAt>=30){StorageQaClient.shot(mc,"ie-machine-kit-survival.png");phase=4;}
  if(phase==4&&ticks-doneAt>=50){QaServer.log("ALL_IE_KIT_CLIENT_DONE");mc.stop();}
 });}
}
