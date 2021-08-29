package com.wgc.shelter.action.model;

public enum UserCommand {
    START("/start"), CREATE("/create"), DESTROY("/destroy");

    public String getCommand() {
        return command;
    }

    private final String command;

    UserCommand(String s) {
        this.command = s;
    }
}
