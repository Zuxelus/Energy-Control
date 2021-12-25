package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.KitAssembler;
import com.zuxelus.energycontrol.containers.ContainerKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;
import com.zuxelus.zlib.containers.EnergyStorage;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.TileEntityItemHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityKitAssembler extends TileEntityItemHandler implements ExtendedScreenHandlerFactory, ITilePacketHandler, ISlotItemFilter {
	public static final byte SLOT_INFO = 0;
	public static final byte SLOT_CARD1 = 1;
	public static final byte SLOT_ITEM = 2;
	public static final byte SLOT_CARD2 = 3;
	public static final byte SLOT_RESULT = 4;
	public static final byte SLOT_DISCHARGER = 5;
	private EnergyStorage storage;
	private int buffer;
	private static final long CONSUMPTION = 5;
	private KitAssemblerRecipe recipe;
	private int recipeTime; // client Only
	public static final long CAPACITY = 2000;
	private double production;
	private boolean active;

	public TileEntityKitAssembler(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		storage = new EnergyStorage(CAPACITY, 32, 32, 0);
		active = false;
		production = 0;
	}

	public TileEntityKitAssembler(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.kit_assembler, pos, state);
	}

	public double getEnergy() {
		return storage.getAmount();
	}

	public int getEnergyFactor() {
		return (int) Math.round(storage.getAmount() * 52.0F / CAPACITY);
	}

	public double getProduction() {
		return production;
	}

	public int getProductionFactor() {
		if (recipeTime == 0)
			return 0;
		return (int) Math.round(production * 24.0F / recipeTime);
	}

	public int getRecipeTime() {
		if (recipe == null)
			return 0;
		return recipe.time;
	}

	@Override
	public void onServerMessageReceived(NbtCompound tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 4:
			if (tag.contains("slot") && tag.contains("title")) {
				ItemStack itemStack = getStack(tag.getInt("slot"));
				if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemCardMain)
					new ItemCardReader(itemStack).setTitle(tag.getString("title"));
			}
			break;
		}
	}

	@Override
	public void onClientMessageReceived(NbtCompound tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("energy") && tag.contains("production")) {
				storage.setEnergy(tag.getLong("energy"));
				production = tag.getDouble("production");
			}
			if (tag.contains("time"))
				recipeTime = tag.getInt("time");
			else
				recipeTime = 0;
			break;
		}
	}

	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public void onDataPacket(BlockEntityUpdateS2CPacket pkt) {
		readProperties(pkt.getNbt());
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound tag = super.toInitialChunkDataNbt();
		tag = writeProperties(tag);
		updateActive();
		tag.putBoolean("active", active);
		return tag;
	}

	@Override
	protected void readProperties(NbtCompound tag) {
		super.readProperties(tag);
		if (tag.contains("energy"))
			storage.setEnergy(tag.getLong("energy"));
		if (tag.contains("buffer"))
			buffer = tag.getInt("buffer");
		if (tag.contains("production"))
			production = tag.getDouble("production");
		if (tag.contains("active"))
			active = tag.getBoolean("active");
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		readProperties(tag);
	}

	@Override
	protected NbtCompound writeProperties(NbtCompound tag) {
		tag = super.writeProperties(tag);
		tag.putLong("energy", storage.getAmount());
		tag.putInt("buffer", buffer);
		tag.putDouble("production", production);
		return tag;
	}

	@Override
	protected void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		writeProperties(tag);
	}

	public static void tickStatic(World level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityKitAssembler))
			return;
		TileEntityKitAssembler te = (TileEntityKitAssembler) be;
		te.tick();
	}

	protected void tick() {
		if (world.isClient)
			return;
		handleDischarger(SLOT_DISCHARGER);
		if (!active)
			return;
		if (storage.getAmount() >= CONSUMPTION) {
			storage.extract(CONSUMPTION, false);
			production += 1;
			if (recipe != null && production >= recipe.time) {
				ItemStack stack1 = getStack(SLOT_CARD1);
				ItemStack stack2 = getStack(SLOT_ITEM);
				ItemStack stack3 = getStack(SLOT_CARD2);
				ItemStack result = getStack(SLOT_RESULT);
				stack1.decrement(recipe.count1);
				if (stack1.getCount() == 0)
					removeStack(SLOT_CARD1);
				stack2.decrement(recipe.count2);
				stack3.decrement(recipe.count3);
				if (result.isEmpty())
					setStack(SLOT_RESULT, recipe.output.copy());
				else
					result.increment(recipe.output.getCount());
				production = 0;
				updateState();
			}
		} else {
			storage.setEnergy(0);
			production = 0;
			updateState();
		}
	}

	private void handleDischarger(int slot) {
		long needed = Math.min(32L, storage.getCapacity() - storage.getAmount());
		if (needed <= 0)
			return;
		if (buffer > 0)
			buffer -= storage.insert(buffer, false);
		needed = Math.min(32, storage.getCapacity() - storage.getAmount());
		ItemStack stack = getStack(slot);
		if (!stack.isEmpty() && needed > 0) {
			if (stack.getItem().equals(Items.LAVA_BUCKET)) {
				buffer += 2000;
				buffer -= storage.insert(buffer, false);
				setStack(slot, new ItemStack(Items.BUCKET));
				return;
			}
			/*IEnergyStorage stackStorage = getStackEnergyStorage(stack);
			if (stackStorage != null)
				if (storage.receiveEnergy(stackStorage.extractEnergy(needed, false), false) > 0)
					active = true;*/
		}
	}

	/*private IEnergyStorage getStackEnergyStorage(ItemStack stack) {
		LazyOptional<IEnergyStorage> cap = stack.getCapability(CapabilityEnergy.ENERGY);
		if(cap.isPresent())
			return cap.orElseThrow( NullPointerException::new );
		return null;
	}*/

	@Override
	public void markDirty() {
		super.markDirty();
		if (world == null || world.isClient)
			return;
		updateState();
	}

	private void updateActive() {
		active = false;
		if (storage.getAmount() < CONSUMPTION)
			return;
		KitAssemblerRecipe newRecipe;
		if (recipe == null) {
			newRecipe = KitAssemblerRecipeType.TYPE.findRecipe(this);
			if (newRecipe == null)
				return;
			recipe = newRecipe;
		} else if (!recipe.isSuitable(this)) {
			newRecipe = KitAssemblerRecipeType.TYPE.findRecipe(this);
			if (newRecipe == null) {
				recipe = null;
				return;
			}
			recipe = newRecipe;
		}
		active = true;
	}

	private void updateState() {
		boolean old = active;
		updateActive();
		if (active == old)
			return;

		production = 0;

		BlockState blockstate = world.getBlockState(pos);
		Block block = blockstate.getBlock();
		if (!(block instanceof KitAssembler) || blockstate.get(KitAssembler.ACTIVE) == active)
			return;
		BlockState newState = block.getDefaultState()
				.with(KitAssembler.FACING, blockstate.get(KitAssembler.FACING))
				.with(KitAssembler.ACTIVE, active);
		world.setBlockState(pos, newState, 3);
	}

	// ------- Inventory -------
	@Override
	public int size() {
		return 6;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return isItemValid(slot, stack);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) { // ISlotItemFilter
		switch (slot) {
		case SLOT_CARD1:
		case SLOT_CARD2:
		case SLOT_ITEM:
			return true;
		case SLOT_INFO:
			return stack.getItem() instanceof ItemCardMain;
		case SLOT_DISCHARGER:
			return /*getStackEnergyStorage(stack) != null ||*/ stack.getItem().equals(Items.LAVA_BUCKET);
		case SLOT_RESULT:
		default:
			return false;
		}
	}

	// NamedScreenHandlerFactory
	@Override
	public ScreenHandler createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerKitAssembler(windowId, inventory, this);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText(ModItems.kit_assembler.getTranslationKey());
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}
}
