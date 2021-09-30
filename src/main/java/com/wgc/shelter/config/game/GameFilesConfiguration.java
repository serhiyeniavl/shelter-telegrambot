package com.wgc.shelter.config.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Configuration
public class GameFilesConfiguration {

    private final Map<Config, Game> games = new HashMap<>();

    private static final String GAME_FILES_FOLDER = "/game";


    @PostConstruct
    void init() throws URISyntaxException {
        Arrays.stream(Objects.requireNonNull(new File(Objects.requireNonNull(getClass().getResource(GAME_FILES_FOLDER)).toURI()).listFiles()))
                .forEach(folderPath -> {
                    final int[] count = {0};
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

    public Game getGame(String languageCode, Integer number) {
        return games.get(new Config(languageCode, number));
    }

    public int countGames(String languageCode) {
        return (int) games.entrySet().stream()
                .filter(entry -> Objects.equals(languageCode, entry.getKey().getLanguageCode()))
                .count();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class Config {
        String languageCode;
        Integer number;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Game {
        String disasterDescription;
        List<String> shelterDescription;

        List<String> professions = new ArrayList<>();
        List<String> health;
        BiologicalCharacteristics biologicalCharacteristics;
        List<String> additionalSkills;
        List<String> humanQualities;
        List<String> hobby;
        List<String> phobia;
        List<String> luggage;

        List<String> specialAbilities;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BiologicalCharacteristics {
        List<String> gender;
        Short age;
        List<String> sexuality;
    }
}
