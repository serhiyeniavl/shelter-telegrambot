package com.wgc.shelter.action.model;

import java.util.Arrays;
import java.util.Objects;

public enum UserCommand {
    START("/start", false),
    CREATE("/create", false),
    DESTROY("/destroy", false),
    INPUT("/input_players_quantity", true),
    LEAVE("/leave", false),
    JOIN("/join", false),
    START_GAME("/start_game", false),
    START_GAME_ANYWAY("/start_game_anyway", true),
    CHANGE_LANG("/change_lang", false),
    HELP("/help", false),
    DESCRIPTION("/description", false);

    UserCommand(String command, boolean isHidden) {
        this.command = command;
        this.isHidden = isHidden;
    }

    public String getCommand() {
        return command;
    }

    public boolean isHidden() {
        return isHidden;
    }

    private final String command;

    private final boolean isHidden;

    public static UserCommand fromValue(String command) {
        return Arrays.stream(values())
                .filter(c -> Objects.equals(c.getCommand(), command))
                .findFirst()
                .orElseThrow();
    }
}
