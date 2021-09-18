package com.wgc.shelter.action.model;

import java.util.Arrays;
import java.util.Objects;

public enum UserCommand {
    START("/start"),
    CREATE("/create"),
    DESTROY("/destroy"),
    INPUT("/input_players_quantity"),
    LEAVE("/leave"),
    JOIN("/join"),
    START_GAME("/start_game"),
    START_GAME_ANYWAY("/start_game_anyway"),
    CHANGE_LANG("/change_lang"),
    HELP("/help"),
    DESCRIPTION("/description");

    public String getCommand() {
        return command;
    }

    private final String command;

    UserCommand(String s) {
        this.command = s;
    }

    public static UserCommand fromValue(String command) {
        return Arrays.stream(values())
                .filter(c -> Objects.equals(c.getCommand(), command))
                .findFirst()
                .orElseThrow();
    }
}
