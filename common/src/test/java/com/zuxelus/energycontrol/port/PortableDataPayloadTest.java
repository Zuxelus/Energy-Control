// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port;
import com.zuxelus.energycontrol.port.network.PortableDataPayload;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class PortableDataPayloadTest {
 @Test void typedRowsRoundTripWithoutReinterpretingText(){var b=new RegistryFriendlyByteBuf(Unpooled.buffer(),RegistryAccess.EMPTY);try{var p=new PortableDataPayload(7,List.of("Water 25%","literal 90%"),List.of(2500,-1));PortableDataPayload.CODEC.encode(b,p);assertEquals(p,PortableDataPayload.CODEC.decode(b));assertEquals(0,b.readableBytes());}finally{b.release();}}
 @Test void refusesInconsistentOrOutOfRangeFractions(){assertThrows(IllegalArgumentException.class,()->new PortableDataPayload(1,List.of("x"),List.of()));assertThrows(IllegalArgumentException.class,()->new PortableDataPayload(1,List.of("x"),List.of(10001)));}
 @Test void refusesOversizedIncomingRowsBeforeAllocation(){var b=new RegistryFriendlyByteBuf(Unpooled.buffer(),RegistryAccess.EMPTY);try{b.writeVarInt(1);b.writeVarInt(33);assertThrows(IllegalArgumentException.class,()->PortableDataPayload.CODEC.decode(b));}finally{b.release();}}
}
