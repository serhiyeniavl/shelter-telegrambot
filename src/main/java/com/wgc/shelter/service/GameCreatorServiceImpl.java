package com.wgc.shelter.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GameCreatorServiceImpl implements GameCreatorService {

    @Override
    public Map<Long, String> createGame(Set<Long> players, Locale locale) {
        return null;
    }
}
