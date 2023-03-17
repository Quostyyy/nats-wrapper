package dev.quosty.listener;

import dev.quosty.packet.NatsPacket;
public abstract class NatsListener<T extends NatsPacket> {

    private final String channel;
    private final Class<T> packet;

    public abstract void onReceive(T packet, String replyTo);

    public NatsListener(String channel, Class<T> packet) {
        this.channel = channel;
        this.packet = packet;
    }

    public String getChannel() {
        return channel;
    }

    public Class<T> getPacket() {
        return packet;
    }
}
