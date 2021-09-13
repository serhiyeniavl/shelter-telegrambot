package com.wgc.shelter.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GameCreatorServiceImpl implements GameCreatorService {
    @Override
    public List<String> createGame(Integer playersQuantity, Locale locale) {
        return null;
    }
}
