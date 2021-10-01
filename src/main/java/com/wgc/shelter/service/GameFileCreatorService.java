package com.wgc.shelter.service;

import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.config.game.GameFilesConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.TextStringBuilder;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.wgc.shelter.config.game.GameFilesConfiguration.Game;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GameFileCreatorService implements GameCreatorService {

    GameFilesConfiguration gameFilesConfiguration;
    MessageSource messageSource;

    @Override
    public Map<Long, String> createGame(Set<Long> players, Locale locale) {

        String language = locale.getLanguage();
        Game game = gameFilesConfiguration.getGame(language, new Random().nextInt(0, gameFilesConfiguration.countGames(language)));

        int[] playerIndex = {0};
        Map<Pair<Integer, Long>, List<String>> result = players.stream().collect(Collectors.toMap(id -> Pair.of(playerIndex[0]++, id), id -> new ArrayList<>()));

        fillWithRandomValue(players.size(), game.getProfessions(), result, messageSource.getMessage(MessageCode.PROFESSION.getCode(), null, locale));
        fillWithRandomValue(players.size(), game.getHealth(), result, messageSource.getMessage(MessageCode.HEALTH.getCode(), null, locale));

        fillBiologicalCharacteristics(players.size(), game.getBiologicalCharacteristics(), result, messageSource.getMessage(MessageCode.BIOLOGICAL_CHARACTERISTICS.getCode(), null, locale));

        fillWithRandomValue(players.size(), game.getHobby(), result, messageSource.getMessage(MessageCode.HOBBY.getCode(), null, locale));
        fillWithRandomValue(players.size(), game.getHumanQualities(), result, messageSource.getMessage(MessageCode.HUMAN_QUALITIES.getCode(), null, locale));
        fillWithRandomValue(players.size(), game.getPhobia(), result, messageSource.getMessage(MessageCode.PHOBIA.getCode(), null, locale));
        fillWithRandomValue(players.size(), game.getAdditionalSkills(), result, messageSource.getMessage(MessageCode.ADDITIONAL_SKILLS.getCode(), null, locale));
        fillWithRandomValue(players.size(), game.getLuggage(), result, messageSource.getMessage(MessageCode.LUGGAGE.getCode(), null, locale));

        fillSpecialAbilities(players.size(), game.getSpecialAbilities(), result, messageSource.getMessage(MessageCode.SPECIAL_ABILITIES.getCode(), null, locale));

        TextStringBuilder commonPart = new TextStringBuilder().append(game.getDisasterDescription())
                .appendNewLine().appendNewLine()
                .append(game.getShelterDescription().get(new Random().nextInt(0, game.getShelterDescription().size())))
                .appendNewLine().appendNewLine()
                .append("Person: ")
                .appendNewLine();

        Map<Long, String> finalResult = new HashMap<>();
        result.forEach((key, value) -> {
            TextStringBuilder text = new TextStringBuilder(messageSource.getMessage(MessageCode.PLAYER_NUMBER.getCode(), new Object[]{key.getKey() + 1}, locale))
                    .appendNewLine().appendNewLine()
                    .append(commonPart)
                    .append(String.join("\n", value));
            finalResult.put(key.getValue(), text.toString());
        });

        return finalResult;
    }

    private void fillBiologicalCharacteristics(int playersSize, GameFilesConfiguration.BiologicalCharacteristics characteristics,
                                               Map<Pair<Integer, Long>, List<String>> result, String characteristicName) {
        int sexualityListSize = characteristics.getSexuality().size();
        int genderListSize = characteristics.getGender().size();

        IntStream.range(0, playersSize)
                .forEach(index -> {
                    String sexuality = characteristics.getSexuality().get(new Random().nextInt(0, sexualityListSize));
                    String gender = characteristics.getGender().get(new Random().nextInt(0, genderListSize));

                    result.get(result.keySet().stream().filter(pair -> Objects.equals(pair.getKey(), index)).findFirst().get()).add(new TextStringBuilder(characteristicName)
                            .append(": ")
                            .append(gender)
                            .append(" ").append("/").append(" ")
                            .append(new Random().nextInt(18, 70))
                            .append(" ").append("/").append(" ")
                            .append(sexuality)
                            .toString());
                });
    }

    private void fillSpecialAbilities(int playersSize, List<String> characteristics,
                                      Map<Pair<Integer, Long>, List<String>> result, String characteristicName) {

        IntStream.range(0, playersSize)
                .forEach(index -> {
                    List<Integer> abilities = new Random().ints(0, characteristics.size())
                            .distinct()
                            .boxed()
                            .limit(2)
                            .collect(Collectors.toList());

                    result.get(result.keySet().stream().filter(pair -> Objects.equals(pair.getKey(), index)).findFirst().get()).add(new TextStringBuilder()
                            .appendNewLine()
                            .append(characteristicName)
                            .append(": ")
                            .appendNewLine()
                            .append("1. ")
                            .append(characteristics.get(abilities.get(0)))
                            .appendNewLine()
                            .append("2. ")
                            .append(characteristics.get(abilities.get(1)))
                            .toString());
                });
    }

    private void fillWithRandomValue(int playersSize, List<String> characteristics,
                                     Map<Pair<Integer, Long>, List<String>> result, String characteristicName) {

        List<Integer> randomListValues = new Random().ints(0, characteristics.size())
                .distinct()
                .boxed()
                .limit(playersSize)
                .collect(Collectors.toList());

        IntStream.range(0, playersSize)
                .forEach(index -> {
                    String characteristicValue = characteristics.get(randomListValues.get(index));
                    result.get(result.keySet().stream().filter(pair -> Objects.equals(pair.getKey(), index)).findFirst().get()).add(new TextStringBuilder(characteristicName)
                            .append(": ")
                            .append(characteristicValue)
                            .toString());
                });
    }
}
