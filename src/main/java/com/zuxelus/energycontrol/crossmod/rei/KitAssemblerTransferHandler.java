package com.zuxelus.energycontrol.crossmod.rei;

import java.util.List;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.shedaniel.rei.RoughlyEnoughItemsNetwork;
import me.shedaniel.rei.api.client.ClientHelper;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.transfer.RecipeFinder;
import me.shedaniel.rei.api.common.transfer.info.MenuInfo;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry;
import me.shedaniel.rei.api.common.transfer.info.MenuSerializationContext;
import me.shedaniel.rei.api.common.transfer.info.MenuTransferException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class KitAssemblerTransferHandler implements TransferHandler {

	@Override
	public Result handle(Context context) {
	    Display display = context.getDisplay();
	    HandledScreen<?> containerScreen = context.getContainerScreen();
	    if (containerScreen == null)
	      return TransferHandler.Result.createNotApplicable(); 
	    ScreenHandler menu = context.getMenu();
	    MenuInfoContext<ScreenHandler, PlayerEntity, Display> menuInfoContext = ofContext(menu, display);
	    MenuInfo<ScreenHandler, Display> menuInfo = MenuInfoRegistry.getInstance().getClient(display, (MenuSerializationContext)menuInfoContext, menu);
	    if (menuInfo == null)
	      return TransferHandler.Result.createNotApplicable(); 
	    try {
	      menuInfo.validate(menuInfoContext);
	    } catch (MenuTransferException e) {
	      if (e.isApplicable())
	        return TransferHandler.Result.createFailed(e.getError()); 
	      return TransferHandler.Result.createNotApplicable();
	    } 
	    List<List<ItemStack>> input = menuInfo.getInputs(menuInfoContext, false);
	    IntList intList = hasItems(menuInfoContext, menu, menuInfo, display, input);
	    if (!intList.isEmpty())
	      return TransferHandler.Result.createFailed((Text)new TranslatableText("error.rei.not.enough.materials")); 
	    if (!ClientHelper.getInstance().canUseMovePackets())
	      return TransferHandler.Result.createFailed((Text)new TranslatableText("error.rei.not.on.server")); 
	    if (!context.isActuallyCrafting())
	      return TransferHandler.Result.createSuccessful(); 
	    context.getMinecraft().setScreen((Screen)containerScreen);
	    /*if (containerScreen instanceof RecipeBookProvider) {
	      RecipeBookProvider listener = (RecipeBookProvider)containerScreen;
	      (listener.getRecipeBookWidget()).ghostSlots.reset();
	    } */
	    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
	    buf.writeIdentifier(display.getCategoryIdentifier().getIdentifier());
	    buf.writeBoolean(Screen.hasShiftDown());
	    buf.writeNbt(menuInfo.save((MenuSerializationContext)menuInfoContext, display));
	    NetworkManager.sendToServer(RoughlyEnoughItemsNetwork.MOVE_ITEMS_PACKET, buf);
	    return TransferHandler.Result.createSuccessful();
	}

	  private static MenuInfoContext<ScreenHandler, PlayerEntity, Display> ofContext(final ScreenHandler menu, final Display display) {
		    return new MenuInfoContext<ScreenHandler, PlayerEntity, Display>() {
		        public ScreenHandler getMenu() {
		          return menu;
		        }
		        
		        public PlayerEntity getPlayerEntity() {
		          return (PlayerEntity)(MinecraftClient.getInstance()).player;
		        }
		        
		        public CategoryIdentifier<Display> getCategoryIdentifier() {
		          return (CategoryIdentifier<Display>) display.getCategoryIdentifier();
		        }
		        
		        public Display getDisplay() {
		          return display;
		        }
		      };
		  }
	
	  public IntList hasItems(MenuInfoContext<ScreenHandler, PlayerEntity, Display> menuInfoContext, ScreenHandler menu, MenuInfo<ScreenHandler, Display> info, Display display, List<List<ItemStack>> inputs) {
		    RecipeFinder recipeFinder = new RecipeFinder();
		    info.getRecipeFinderPopulator().populate(menuInfoContext, recipeFinder);
		    IntArrayList intArrayList = new IntArrayList();
		    for (int i = 0; i < inputs.size(); i++) {
		      List<ItemStack> possibleStacks = inputs.get(i);
		      boolean done = possibleStacks.isEmpty();
		      for (ItemStack possibleStack : possibleStacks) {
		        if (!done) {
		          int invRequiredCount = possibleStack.getCount();
		          int key = RecipeFinder.getItemId(possibleStack);
		          while (invRequiredCount > 0 && recipeFinder.contains(key)) {
		            invRequiredCount--;
		            recipeFinder.take(key, 1);
		          } 
		          if (invRequiredCount <= 0) {
		            done = true;
		            break;
		          } 
		        } 
		      } 
		      if (!done)
		        intArrayList.add(i); 
		    } 
		    return (IntList)intArrayList;
		  }
	
	public double getPriority() {
		return 0.0D;
	}
}
