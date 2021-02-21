package com.zuxelus.energycontrol.network;

import java.util.Map;
import java.util.function.Supplier;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketCardServer extends PacketCard {

	public PacketCardServer(BlockPos pos, int slot, String className, Map<String, Object> fields) {
		super(pos, slot, className, fields);
	}

	public static void handle(PacketCardServer message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerPlayerEntity player = ctx.getSender();
			if (player == null || player.world == null)
				return;
			TileEntity te = player.world.getTileEntity(message.pos);
			if (!(te instanceof TileEntityInfoPanel))
				return;
			
			TileEntityInfoPanel panel = (TileEntityInfoPanel) te;
			ItemStack stack = panel.getStackInSlot(message.slot);
			if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
				return;
			if (!stack.getItem().getClass().getName().equals(message.className)) {
				EnergyControl.LOGGER.warn("Class mismatch: '%s'!='%s'", message.className, stack.getItem().getClass().getName());
				return;
			}
			ItemCardReader helper = new ItemCardReader(stack);
			for (Map.Entry<String, Object> entry : message.fields.entrySet()) {
				String name = entry.getKey();
				Object value = entry.getValue();
				if (value instanceof Long) {
					helper.setLong(name, (Long) value);
				} else if (value instanceof Double) {
					helper.setDouble(name, (Double) value);
				} else if (value instanceof Integer) {
					helper.setInt(name, (Integer) value);
				} else if (value instanceof String) {
					helper.setString(name, (String) value);
				} else if (value instanceof Boolean) {
					helper.setBoolean(name, (Boolean) value);
				}
			}
			//helper.commit(panel, message.slot);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketCardServer pkt, PacketBuffer buf) {
		encodeCard(pkt, buf);
	}

	public static PacketCardServer decode(PacketBuffer buf) {
		return (PacketCardServer) decodeCard(buf);
	}
}
