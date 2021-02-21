package com.zuxelus.energycontrol.network;

import java.util.Map;
import java.util.function.Supplier;

import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketCardClient extends PacketCard {

	public PacketCardClient(BlockPos pos, int slot, String className, Map<String, Object> fields) {
		super(pos, slot, className, fields);
	}

	public static void handle(PacketCardClient message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			@SuppressWarnings("resource")
			ClientWorld world = Minecraft.getInstance().world;
			if (world == null)
				return;
			TileEntity te = world.getTileEntity(message.pos);
			if (te == null || !(te instanceof TileEntityInfoPanel))
				return;
			TileEntityInfoPanel panel = (TileEntityInfoPanel) te;
			ItemStack stack = panel.getStackInSlot(message.slot);
			if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
				return;

			ItemCardReader reader = new ItemCardReader(stack);
			for (Map.Entry<String, Object> entry : message.fields.entrySet()) {
				String name = entry.getKey();
				Object value = entry.getValue();
				if (value instanceof Long) {
					reader.setLong(name, (Long) value);
				} else if (value instanceof Double) {
					reader.setDouble(name, (Double) value);
				} else if (value instanceof Integer) {
					reader.setInt(name, (Integer) value);
				} else if (value instanceof String) {
					reader.setString(name, (String) value);
				} else if (value instanceof Boolean) {
					reader.setBoolean(name, (Boolean) value);
				} else if (value instanceof CompoundNBT) {
					reader.setTag(name, (CompoundNBT) value);
				} else if (value == null)
					reader.removeField(name);
			}
			panel.resetCardData();
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketCardClient pkt, PacketBuffer buf) {
		encodeCard(pkt, buf);
	}

	public static PacketCardClient decode(PacketBuffer buf) {
		return (PacketCardClient) decodeCard(buf);
	}
}
