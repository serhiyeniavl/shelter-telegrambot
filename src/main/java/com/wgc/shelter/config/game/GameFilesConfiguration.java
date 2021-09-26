package com.wgc.shelter.config.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Configuration
public class GameFilesConfiguration {

    @Getter
    private final Map<Config, Game> games = new HashMap<>();

    private static final String GAME_FILES_FOLDER = "/game";


    @PostConstruct
    void init() throws URISyntaxException {
        Arrays.stream(Objects.requireNonNull(new File(Objects.requireNonNull(getClass().getResource(GAME_FILES_FOLDER)).toURI()).listFiles()))
                .forEach(folderPath -> {
                    final short[] count = {0};
                    try {
                        Files.walk(Path.of(folderPath.getPath()))
                                .filter(Files::isRegularFile)
                                .forEach(filePath -> {
                                    try {
                                        Game game = new ObjectMapper(new YAMLFactory()).readValue(filePath.toFile(), Game.class);
                                        games.put(new Config(folderPath.getName(), count[0]++), game);
                                    } catch (IOException e) {
                                        log.error("Error occurred during load game files", e);
                                        throw new RuntimeException(e);
                                    }
                                });
                    } catch (IOException e) {
                        log.error("Error occurred during load game files", e);
                        throw new RuntimeException(e);
                    }
                });
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class Config {
        String languageCode;
        Short number;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Game {
        String disasterDescription;
        Set<String> shelterDescription;

        GameRoles roles;

        Set<String> specialAbilities;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class GameRoles {
        Set<Map<String, Short>> professions;
        Set<Map<String, Short>> health;
        BiologicalCharacteristics biologicalCharacteristics;
        Set<Map<String, Short>> additionalSkills;
        Set<Map<String, Short>> humanQualities;
        Set<Map<String, Short>> hobby;
        Set<Map<String, Short>> phobia;
        Set<Map<String, Short>> luggage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BiologicalCharacteristics {
        Set<String> gender;
        Short age;
        Set<Map<String, Short>> sexuality;
    }
}
