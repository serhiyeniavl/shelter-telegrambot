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
    CANT_LEAVE_WISH_TO_DELETE("cant.leave.room.wish.to.delete"),
    INPUT_ROOM_NUMBER("input.room.number"),
    ROOM_LEFT("room.left"),
    NO_ACTIVE_ROOM("room.no.active"),
    WAIT_TO_JOIN_ALL_PLAYERS("wait.for.all.to.join"),
    START_ANYWAY("start.anyway"),
    NON_STARTED_ROOM_NOT_FOUND("non.started.room.not.found"),
    CANT_DO_ACTION_RIGHT_NOW_SEE_HELP("cant.do.action.see.help"),
    CHOOSE_LANG("choose.lang"),
    HELP("help"),
    DESCRIPTION("description"),
    ROOM_SUCCESSFULLY_CREATED_WAIT_FOR_OTHERS("room.successfully.created.wait.for.others"),
    QUANTITY_SHOULD_BE_BETWEEN("quantity.should.be.between");

    public String getCode() {
        return messageBundleCode;
    }

    private final String messageBundleCode;

    MessageCode(String messageBundleCode) {
        this.messageBundleCode = messageBundleCode;
    }
}
