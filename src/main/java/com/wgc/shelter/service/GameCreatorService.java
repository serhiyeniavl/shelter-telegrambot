package com.wgc.shelter.service;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface GameCreatorService {
    Map<Long, String> createGame(Set<Long> players, Locale locale);
}
