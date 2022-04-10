package com.wgc.shelter.config.game;

import com.codepoetics.protonpack.StreamUtils;
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
import java.util.stream.Stream;

@Slf4j
@Configuration
public class GameFilesConfiguration {

    private final Map<Config, Disaster> disasters = new HashMap<>();
    private final Map<Config, Cards> cards = new HashMap<>();

    private static final String DISASTERS_PATH = "/game/disasters";
    private static final String CARDS_PATH = "/game/cards";


    @PostConstruct
    void init() throws URISyntaxException {
        loadDisasters();
        loadCards();
    }

    private void loadDisasters() throws URISyntaxException {
        StreamUtils.zipWithIndex(streamFiles(DISASTERS_PATH))
                .forEach(indexedFolderPath -> {
                    try {
                        Files.walk(Path.of(indexedFolderPath.getValue().getPath()))
                                .filter(Files::isRegularFile)
                                .forEach(filePath -> {
                                    try {
                                        Disaster disaster = new ObjectMapper(
                                                new YAMLFactory()).readValue(filePath.toFile(), Disaster.class);
                                        disasters.put(new Config(indexedFolderPath.getValue().getName(),
                                                Math.toIntExact(indexedFolderPath.getIndex())), disaster);
                                    } catch (IOException e) {
                                        log.error("Error occurred during load game files", e);
                                        throw new RuntimeException(e);
                                    }
                                });
                    } catch (IOException e) {
                        log.error("Error occurred during load disaster files", e);
                        throw new RuntimeException(e);
                    }
                });
    }

    private void loadCards() throws URISyntaxException {
        StreamUtils.zipWithIndex(streamFiles(CARDS_PATH))
                .forEach(indexedFolderPath -> {
                    try {
                        Files.walk(Path.of(indexedFolderPath.getValue().getPath()))
                                .filter(Files::isRegularFile)
                                .forEach(filePath -> {
                                    try {
                                        Cards cards = new ObjectMapper(
                                                new YAMLFactory()).readValue(filePath.toFile(), Cards.class);
                                        this.cards.put(new Config(indexedFolderPath.getValue().getName()), cards);
                                    } catch (IOException e) {
                                        log.error("Error occurred during load cards files", e);
                                        throw new RuntimeException(e);
                                    }
                                });
                    } catch (IOException e) {
                        log.error("Error occurred during load game files", e);
                        throw new RuntimeException(e);
                    }
                });
    }

    private Stream<File> streamFiles(String cardsPath) throws URISyntaxException {
        return Arrays.stream(Objects.requireNonNull(
                new File(Objects.requireNonNull(getClass().getResource(cardsPath)).toURI())
                        .listFiles()));
    }

    public Disaster getDisaster(String languageCode, Integer number) {
        return disasters.get(new Config(languageCode, number));
    }

    public Cards getCards(String languageCode) {
        return this.cards.get(new Config(languageCode));
    }

    public int countDisasters(String languageCode) {
        return (int) disasters.entrySet().stream()
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

        public Config(String languageCode) {
            this.languageCode = languageCode;
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Disaster {
        String disasterDescription;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Cards {
        List<String> shelterDescription;

        List<String> professions = new ArrayList<>();
        List<String> health;
        BiologicalCharacteristics biologicalCharacteristics;
        List<String> additionalSkills;
        List<String> hobby;
        List<String> threat;
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
