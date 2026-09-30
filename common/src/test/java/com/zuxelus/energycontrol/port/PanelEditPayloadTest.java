package com.zuxelus.energycontrol.port;
import com.zuxelus.energycontrol.port.network.PanelEditPayload;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PanelEditPayloadTest {
    @BeforeAll static void bootstrap() { SharedConstants.tryDetectVersion(); Bootstrap.bootStrap(); }
    @Test void roundTripsUnicodeTextAndSelectsExactMenuAndCard() {
        var buffer=new RegistryFriendlyByteBuf(Unpooled.buffer(),RegistryAccess.EMPTY);
        try {
            var payload=new PanelEditPayload(27,7,"核电站\nEnergy: 你好 ⚡");
            PanelEditPayload.CODEC.encode(buffer,payload);
            assertEquals(payload,PanelEditPayload.CODEC.decode(buffer));
            assertEquals(0,buffer.readableBytes());
        } finally { buffer.release(); }
    }
    @Test void refusesOversizedOutgoingText() {
        var buffer=new RegistryFriendlyByteBuf(Unpooled.buffer(),RegistryAccess.EMPTY);
        try { assertThrows(EncoderException.class,()->PanelEditPayload.CODEC.encode(buffer,new PanelEditPayload(1,0,"x".repeat(513)))); }
        finally { buffer.release(); }
    }
    @Test void refusesOversizedIncomingText() {
        var buffer=new RegistryFriendlyByteBuf(Unpooled.buffer(),RegistryAccess.EMPTY);
        try {
            buffer.writeVarInt(1); buffer.writeVarInt(0); buffer.writeUtf("x".repeat(513),1024);
            assertThrows(DecoderException.class,()->PanelEditPayload.CODEC.decode(buffer));
        } finally { buffer.release(); }
    }
}
