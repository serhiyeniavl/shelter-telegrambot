package com.wgc.shelter.action.message;

public enum MessageCode {
    HELLO("hello"),
    INPUT_ROOM_PARTICIPANTS_QUANTITY("input.participants.quantity"),
    ALREADY_CREATED("already.created"),
    ANSWER_YES("answer.yes"),
    ANSWER_NO("answer.no"),
    CANT_DO_ACTION_WISH_TO_LEAVE("cant.do.action.wish.to.leave"),
    ROOM_SUCCESSFULLY_DELETED("room.deleted"),
    USER_DOESNT_HAVE_ROOM("user.doesnt.have.room"),
    INPUT_ROOM_NUMBER("input.room.number"),
    ROOM_LEFT("room.left"),
    NO_ACTIVE_ROOM("room.no.active");

    public String getCode() {
        return messageBundleCode;
    }

    private final String messageBundleCode;

    MessageCode(String messageBundleCode) {
        this.messageBundleCode = messageBundleCode;
    }
}
