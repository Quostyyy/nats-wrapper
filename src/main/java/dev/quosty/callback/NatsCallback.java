package dev.quosty.callback;

import dev.quosty.packet.NatsPacket;

public abstract class NatsCallback<T extends NatsPacket> {

    public abstract void onReceive(T packet);
    public abstract void exit();

}