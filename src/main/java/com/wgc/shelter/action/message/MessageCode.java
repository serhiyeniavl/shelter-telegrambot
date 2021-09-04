package com.wgc.shelter.action.message;

public enum MessageCode {
    HELLO("hello"), INPUT_ROOM_PARTICIPANTS_QUANTITY("input.participants.quantity");

    public String getCode() {
        return messageBundleCode;
    }

    private final String messageBundleCode;

    MessageCode(String messageBundleCode) {
        this.messageBundleCode = messageBundleCode;
    }
}
