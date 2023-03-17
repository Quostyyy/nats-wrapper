package dev.quosty;

import dev.quosty.callback.NatsCallback;
import dev.quosty.handler.NatsPacketHandler;
import dev.quosty.listener.NatsListener;
import dev.quosty.packet.NatsPacket;
import dev.quosty.serialization.NatsSerialization;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;

import java.io.IOException;
import java.time.Duration;

public class NatsWrapper implements NatsPacketHandler {

    private final Connection connection;
    public NatsWrapper(Connection connection) {
        this.connection = connection;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends NatsPacket> void registerListener(NatsListener<T> listener) {
        Dispatcher dispatcher = this.connection.createDispatcher(message -> {
            NatsPacket receivedPacket;

            try {
                receivedPacket = (NatsPacket) NatsSerialization.deserialize(message.getData());
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }

            if (!receivedPacket.getClass().isAssignableFrom(listener.getPacket())) {
                return;
            }

            if (message.getReplyTo() == null){
                listener.onReceive((T) receivedPacket, null);
                return;
            }

            listener.onReceive((T) receivedPacket, message.getReplyTo());
        });

        dispatcher.subscribe(listener.getChannel());
        System.out.println("[nats-wrapper] Subscribed channel " + listener.getChannel() + " with packet " + listener.getPacket().getSimpleName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends NatsPacket> void sendCallbackPacket(String channel, NatsPacket packet, int duration, NatsCallback<T> request) {
        Message reply;

        try {
            reply = this.connection.request(channel, NatsSerialization.serialize(packet), Duration.ofMillis(duration));
        } catch (InterruptedException | IOException exception) {
            request.exit();
            return;
        }

        if (reply == null) {
            request.exit();
            return;
        }

        try {
            request.onReceive((T) NatsSerialization.deserialize(reply.getData()));
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public <T extends NatsPacket> void sendPacket(String channel, T packet) {
        try {
            this.connection.publish(channel, NatsSerialization.serialize(packet));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
