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
import java.util.List;
import java.util.stream.Collectors;

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

        GameRoles roles;

        List<String> specialAbilities;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class GameRoles {
        List<Map<String, Integer>> professions;
        List<Map<String, Integer>> health;
        BiologicalCharacteristics biologicalCharacteristics;
        List<Map<String, Integer>> additionalSkills;
        List<Map<String, Integer>> humanQualities;
        List<Map<String, Integer>> hobby;
        List<Map<String, Integer>> phobia;
        List<Map<String, Integer>> luggage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BiologicalCharacteristics {
        List<String> gender;
        Short age;
        List<Map<String, Short>> sexuality;
    }
}
