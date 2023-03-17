package dev.quosty.packet;

import java.io.Serializable;
import java.util.UUID;

public class NatsPacket implements Serializable {

    private String sender;

    public NatsPacket() {
        this.sender = UUID.randomUUID().toString().split("-")[0];
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }
}
