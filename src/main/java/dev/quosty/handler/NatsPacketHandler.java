package dev.quosty.handler;

import dev.quosty.callback.NatsCallback;
import dev.quosty.listener.NatsListener;
import dev.quosty.packet.NatsPacket;

public interface NatsPacketHandler {

    <T extends NatsPacket> void registerListener(NatsListener<T> listener);

    <T extends NatsPacket> void sendPacket(String channel, T packet);
    <T extends NatsPacket> void sendCallbackPacket(String channel, NatsPacket packet, int duration, NatsCallback<T> request);

}
