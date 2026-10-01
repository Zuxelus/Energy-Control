// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.block.PanelBlockEntity;
import com.zuxelus.energycontrol.port.card.CardItem;
import com.zuxelus.energycontrol.port.menu.PanelMenu;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.*;
public final class IeQaClient {
 private static boolean connecting;private static int ticks,done;private static String before;
 private static final BlockPos PANEL=new BlockPos(0,1,0),CAP=new BlockPos(4,0,0);
 private static final boolean reload=System.getProperty("ec.qa.stage","").equals("ie-reload");
 public static void init(int port){QaServer.log("RUN_START IE client reload="+reload+" "+java.time.Instant.now());ClientTickEvent.CLIENT_POST.register(mc->{
  mc.options.pauseOnLostFocus=false;mc.options.getSoundSourceOptionInstance(net.minecraft.sounds.SoundSource.MASTER).set(0.0);if(!connecting&&mc.getOverlay()==null&&mc.screen!=null){connecting=true;ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:"+port),new ServerData("Real IE acceptance","127.0.0.1:"+port,ServerData.Type.OTHER),false,null);}
  if(mc.level==null||mc.player==null||!(mc.level.getBlockEntity(PANEL) instanceof PanelBlockEntity p))return;ticks++;mc.getToasts().clear();
  if(reload){if(ticks==80){QaServer.check(p.lines().contains("IE_QA_DONE")&&p.lines().stream().anyMatch(s->s.contains("1024000 mB")),"IE client sees persisted real tanks and array after server restart");StorageQaClient.shot(mc,"ie-reload.png");}if(ticks==110){QaServer.log("ALL_IE_RELOAD_CLIENT_DONE");mc.stop();}return;}
  if(ticks==50){QaServer.check(p.lines().stream().anyMatch(s->s.contains("FE"))&&p.lines().stream().anyMatch(s->s.contains("512000 mB")),"IE real capacitor and multiblock readings reach client");before=p.lines().toString();StorageQaClient.shot(mc,"ie-01-real-machines.png");}
  if(ticks==80){QaServer.check(!before.equals(p.lines().toString()),"IE actual storage mutations synchronize to rendered text");StorageQaClient.shift(mc,true);mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(CAP).add(0,.5,0),Direction.UP,CAP,false));}
  if(ticks==85)StorageQaClient.shift(mc,false);
  if(ticks==110){QaServer.check(CardItem.data(mc.player.getMainHandItem()).getLong("target")==CAP.asLong(),"IE real sneak-use binds capacitor face");mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(PANEL),Direction.NORTH,PANEL,false));}
  if(ticks==140){QaServer.check(mc.player.containerMenu instanceof PanelMenu,"IE sensor panel opens actual configuration menu");StorageQaClient.shot(mc,"ie-02-card-menu.png");mc.player.closeContainer();}
  if(ticks==180)mc.gameMode.startDestroyBlock(CAP,Direction.NORTH);
  if(ticks==210)QaServer.check(mc.level.getBlockState(CAP).isAir(),"IE normal creative capacitor break reaches client");
  if(ticks==230){mc.player.getInventory().selected=4;mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(CAP.below()).add(0,.5,0),Direction.UP,CAP.below(),false));}
  if(ticks==270){QaServer.check(!mc.level.getBlockState(CAP).isAir(),"IE normal capacitor replacement reaches client");StorageQaClient.shot(mc,"ie-03-replaced-capacitor.png");}
  if(p.lines().contains("IE_QA_DONE")){if(done++==20){QaServer.check(p.lines().stream().anyMatch(s->s.contains("1024000 mB")),"IE tank reformation and chunk restoration synchronized");StorageQaClient.shot(mc,"ie-04-restored-world.png");}if(done==50){QaServer.log("ALL_IE_CLIENT_SCENARIOS_DONE");mc.stop();}}
  if(ticks==2000)QaServer.check(false,"IE server completion timeout");
 });}
}
