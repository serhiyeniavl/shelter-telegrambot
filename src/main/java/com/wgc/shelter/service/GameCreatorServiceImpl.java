package com.wgc.shelter.service;

import com.wgc.shelter.config.game.GameFilesConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GameCreatorServiceImpl implements GameCreatorService {

    GameFilesConfiguration gameFilesConfiguration;

    @Override
    public Map<Long, String> createGame(Set<Long> players, Locale locale) {

        return null;
    }
}
