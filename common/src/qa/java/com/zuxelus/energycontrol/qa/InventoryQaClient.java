// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import com.zuxelus.energycontrol.port.*;
import com.zuxelus.energycontrol.port.block.*;
import com.zuxelus.energycontrol.port.card.*;
import com.zuxelus.energycontrol.port.menu.*;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.phys.*;
public final class InventoryQaClient {
 private static final boolean offhand=System.getProperty("ec.qa.stage","").contains("offhand");
 private static void swapHand(Minecraft mc){mc.player.connection.send(new net.minecraft.network.protocol.game.ServerboundPlayerActionPacket(net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND,BlockPos.ZERO,Direction.DOWN));}
 private static int ticks;private static boolean connecting;private static String first;private static final boolean observer=Boolean.getBoolean("ec.qa.observer"),reload=System.getProperty("ec.qa.stage","").endsWith("reload");
 private static void click(Minecraft mc,String prefix){for(var w:mc.screen.children())if(w instanceof Button b&&b.active&&b.visible&&b.getMessage().getString().startsWith(prefix)){b.onPress();return;}throw new AssertionError("Missing button "+prefix);}
 private static void use(Minecraft mc,BlockPos pos){QaServer.log("INTERACTION slot="+mc.player.getInventory().selected+" held="+mc.player.getMainHandItem()+" target="+pos);mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos),Direction.NORTH,pos,false));}
 private static void move(Minecraft mc,int slot,ClickType type,int button){mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId,slot,button,type,mc.player);}
 private static String total(PanelBlockEntity p){return p.lines().stream().filter(v->v.startsWith("Total items:")).findFirst().orElse("");}
 public static void init(int port){QaServer.log("RUN_START inventory-client observer="+observer+" reload="+reload+" "+java.time.Instant.now());ClientTickEvent.CLIENT_POST.register(mc->{
  mc.options.pauseOnLostFocus=false;mc.options.getSoundSourceOptionInstance(net.minecraft.sounds.SoundSource.MASTER).set(0.0);org.lwjgl.glfw.GLFW.glfwHideWindow(mc.getWindow().getWindow());
  if(!connecting&&mc.getOverlay()==null&&mc.screen!=null){connecting=true;ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:"+port),new ServerData("Inventory acceptance","127.0.0.1:"+port,ServerData.Type.OTHER),false,null);}
  if(mc.level==null||mc.player==null||!(mc.level.getBlockEntity(InventoryQaServer.CORE) instanceof PanelBlockEntity p))return;if(!reload&&ticks==0&&mc.player.connection.getOnlinePlayers().size()<2)return;ticks++;mc.getToasts().clear();String role=observer?"observer":"editor";
  if(reload){if(ticks==65){QaServer.check(p.lines().stream().noneMatch(v->v.startsWith("Slot 1:"))&&!total(p).isEmpty(),"reloaded field settings and dynamic inventory reach client");mc.player.getInventory().selected=1;mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}if(ticks==95){QaServer.check(mc.player.containerMenu instanceof CardHolderMenu m&&m.contents.getItem(0).is(EnergyControlPort.TEXT.get()),"actual holder menu restores persisted card after server restart");StorageQaClient.shot(mc,"inventory-reload-holder.png");mc.player.closeContainer();mc.player.getInventory().selected=2;mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}if(ticks==125){QaServer.check(mc.player.containerMenu instanceof PortableMenu m&&m.lines.stream().noneMatch(v->v.startsWith("Slot 1:"))&&m.lines.stream().anyMatch(v->v.startsWith("Total items:")),"actual portable inventory data and fields survive restart");StorageQaClient.shot(mc,"inventory-reload-portable.png");mc.player.closeContainer();}if(ticks==150){StorageQaClient.shot(mc,"inventory-reload-world.png");if(!offhand){QaServer.log("ALL_INVENTORY_RELOAD_CLIENT_DONE");mc.stop();}else{mc.player.getInventory().selected=1;mc.player.connection.send(new net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket(1));swapHand(mc);}}
   if(offhand){
    if(ticks==175)mc.gameMode.useItem(mc.player,InteractionHand.OFF_HAND);
    if(ticks==200){QaServer.check(mc.player.containerMenu instanceof CardHolderMenu m&&m.contents.getItem(0).is(EnergyControlPort.TEXT.get()),"offhand holder opens with persisted card");move(mc,0,ClickType.SWAP,40);}
    if(ticks==225){QaServer.check(mc.player.getOffhandItem().is(EnergyControlPort.HOLDER.get())&&((CardHolderMenu)mc.player.containerMenu).contents.getItem(0).is(EnergyControlPort.TEXT.get()),"offhand holder swap cannot move parent or contents");StorageQaClient.shot(mc,"inventory-offhand-holder.png");mc.player.closeContainer();swapHand(mc);}
    if(ticks==250){QaServer.check(mc.player.getInventory().getItem(1).is(EnergyControlPort.HOLDER.get())&&mc.player.getOffhandItem().isEmpty(),"holder returns from offhand without duplicate or loss");mc.player.getInventory().selected=2;mc.player.connection.send(new net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket(2));swapHand(mc);}
    if(ticks==275)mc.gameMode.useItem(mc.player,InteractionHand.OFF_HAND);
    if(ticks==300){QaServer.check(mc.player.containerMenu instanceof PortableMenu m&&m.lines.stream().anyMatch(v->v.startsWith("Total items:")),"offhand portable shows live inventory rows");move(mc,0,ClickType.SWAP,40);}
    if(ticks==325){QaServer.check(mc.player.getOffhandItem().is(EnergyControlPort.PORTABLE.get())&&((PortableMenu)mc.player.containerMenu).contents.getItem(0).is(EnergyControlPort.INVENTORY.get()),"offhand portable swap cannot move parent or card");StorageQaClient.shot(mc,"inventory-offhand-portable.png");mc.player.closeContainer();swapHand(mc);}
    if(ticks==350){QaServer.check(mc.player.getInventory().getItem(2).is(EnergyControlPort.PORTABLE.get())&&mc.player.getOffhandItem().isEmpty(),"portable returns from offhand without duplicate or loss");QaServer.log("ALL_INVENTORY_OFFHAND_RELOAD_CLIENT_DONE");mc.stop();}
   }return;}
  if(ticks==60){first=total(p);QaServer.check(!first.isEmpty()&&p.lines().contains("Slots used: 3 / 27"),"both clients receive full actual inventory summary");}
  if(ticks==90){QaServer.check(!total(p).equals(first),"both clients receive changing real chest contents");StorageQaClient.shot(mc,"inventory-"+role+"-live.png");}
  if(ticks==100)use(mc,InventoryQaServer.CORE);
  if(ticks==130){QaServer.check(mc.player.containerMenu instanceof PanelMenu,"inventory configuration opens on both clients");if(!observer){click(mc,"Fields");click(mc,"Item details");}}
  if(ticks==165){QaServer.check(p.lines().stream().noneMatch(v->v.startsWith("Slot 1:"))&&com.zuxelus.energycontrol.port.inventory.InventorySnapshot.fields(CardItem.data(((PanelMenu)mc.player.containerMenu).getSlot(0).getItem()))==15,"inventory field toggle synchronizes menu card and world on both clients");StorageQaClient.shot(mc,"inventory-"+role+"-fields.png");mc.player.closeContainer();}
  if(!observer){
   if(ticks==185){mc.player.connection.sendCommand("tp @s 4.5 0 -2 0 0");mc.player.getInventory().selected=0;}
   if(ticks==210){mc.player.getInventory().selected=0;use(mc,InventoryQaServer.CHEST);}
   if(ticks==245){QaServer.log("KIT_RESULT slot="+mc.player.getInventory().selected+" stack0="+mc.player.getInventory().getItem(0)+" menu="+mc.player.containerMenu.getClass().getSimpleName());QaServer.check(mc.player.getInventory().getItem(0).getCount()==1&&mc.player.containerMenu==mc.player.inventoryMenu,"inventory kit consumes one before chest GUI opens");boolean found=false;for(var stack:mc.player.getInventory().items)if(stack.is(EnergyControlPort.INVENTORY.get()))found=CardTargets.read(CardItem.data(stack)).stream().anyMatch(t->t.position()==InventoryQaServer.CHEST.asLong()&&t.side()==Direction.NORTH.get3DDataValue());QaServer.check(found,"dropped inventory kit card is picked up with real bound face");mc.player.getInventory().selected=3;use(mc,InventoryQaServer.INVALID);}
   if(ticks==265){QaServer.check(mc.player.getInventory().getItem(3).getCount()==2,"unsupported energy target does not consume kit");use(mc,InventoryQaServer.CHEST);}
   if(ticks==290){QaServer.check(mc.player.getInventory().getItem(3).getCount()==1,"energy kit recognizes actual platform fixture capability");mc.player.getInventory().selected=4;use(mc,InventoryQaServer.CHEST);}
   if(ticks==315){QaServer.check(mc.player.getInventory().getItem(4).getCount()==1,"fluid kit recognizes actual platform fixture capability");mc.player.getInventory().selected=5;use(mc,InventoryQaServer.INVALID);}
   if(ticks==340){QaServer.check(mc.player.getInventory().getItem(5).getCount()==1,"redstone kit binds actual vanilla block");mc.player.getInventory().selected=6;use(mc,InventoryQaServer.INVALID);}
   if(ticks==365){QaServer.check(mc.player.getInventory().getItem(6).getCount()==2,"unsupported machine kit does not consume");mc.player.getInventory().selected=1;mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}
   if(ticks==395){QaServer.check(mc.player.containerMenu instanceof CardHolderMenu,"real held54-slot card holder opens");move(mc,54,ClickType.QUICK_MOVE,0);}
   if(ticks==420){var m=(CardHolderMenu)mc.player.containerMenu;QaServer.check(m.contents.getItem(0).is(EnergyControlPort.TEXT.get()),"normal shift-click inserts card into holder");move(mc,55,ClickType.QUICK_MOVE,0);move(mc,56,ClickType.QUICK_MOVE,0);move(mc,0,ClickType.SWAP,1);}
   if(ticks==450){var m=(CardHolderMenu)mc.player.containerMenu;QaServer.check(m.contents.getItem(1).isEmpty()&&mc.player.getInventory().getItem(10).is(net.minecraft.world.item.Items.DIAMOND)&&mc.player.getInventory().getItem(11).is(EnergyControlPort.HOLDER.get()),"holder rejects ordinary items and nested holder");QaServer.check(mc.player.getMainHandItem().is(EnergyControlPort.HOLDER.get())&&m.contents.getItem(0).is(EnergyControlPort.TEXT.get()),"holder hotbar swap cannot move held parent");StorageQaClient.shot(mc,"inventory-holder.png");mc.player.closeContainer();}
   if(ticks==475)mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);
   if(ticks==500){QaServer.check(((CardHolderMenu)mc.player.containerMenu).contents.getItem(0).is(EnergyControlPort.TEXT.get()),"holder close-reopen retains card");mc.player.closeContainer();mc.player.getInventory().selected=2;mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}
   if(ticks==535){var m=(PortableMenu)mc.player.containerMenu;QaServer.check(m.lines.stream().anyMatch(v->v.startsWith("Total items:")),"portable displays real live inventory data");click(mc,"Fields");click(mc,"Item details");}
   if(ticks==565){QaServer.check(((PortableMenu)mc.player.containerMenu).lines.stream().noneMatch(v->v.startsWith("Slot 1:")),"portable field controls update actual server rows");StorageQaClient.shot(mc,"inventory-portable-fields.png");click(mc,"Fields");}
   if(ticks==590){StorageQaClient.shot(mc,"inventory-portable-live.png");mc.player.closeContainer();mc.player.getInventory().selected=7;mc.player.connection.sendCommand("tp @s 1.5 0 -4 0 0");}
  }
  if(ticks==805){StorageQaClient.shot(mc,"inventory-"+role+"-final.png");QaServer.log("ALL_INVENTORY_CLIENT_DONE "+role);mc.stop();}
 });}
}
