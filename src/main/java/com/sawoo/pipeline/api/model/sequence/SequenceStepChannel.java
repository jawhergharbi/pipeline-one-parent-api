package com.sawoo.pipeline.api.model.sequence;

public enum SequenceStepChannel {

    LINKED_IN(0),
    EMAIL(10),
    SMS(20),
    WHATSAPP(30);

    private final int channel;

    private SequenceStepChannel(int channel) {
        this.channel = channel;
    }

    public int getChannel() {
        return this.channel;
    }
}
