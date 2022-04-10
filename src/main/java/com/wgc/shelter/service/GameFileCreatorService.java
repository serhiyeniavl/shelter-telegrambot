package com.wgc.shelter.service;

import com.codepoetics.protonpack.StreamUtils;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.config.game.GameFilesConfiguration;
import com.wgc.shelter.config.game.GameFilesConfiguration.Cards;
import com.wgc.shelter.config.game.GameFilesConfiguration.Disaster;
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
import java.util.Set;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GameFileCreatorService implements GameCreatorService {

    GameFilesConfiguration gameFilesConfiguration;
    MessageSource messageSource;

    private static final RandomGenerator random = RandomGenerator.getDefault();

    @Override
    public Map<Long, String> createGame(Set<Long> players, Locale locale) {

        String language = locale.getLanguage();
        Cards cards = gameFilesConfiguration.getCards(language);
        Disaster disaster = gameFilesConfiguration.getDisaster(language,
                random.nextInt(0, gameFilesConfiguration.countDisasters(language)));

        Map<Pair<Integer, Long>, List<String>> result = StreamUtils
                .zip(players.stream(), IntStream.rangeClosed(0, players.size() - 1).boxed(),
                        (player, number) -> Pair.of(number, player))
                .collect(Collectors.toMap(Function.identity(), pair -> new ArrayList<>()));

        createResultMap(players, locale, cards, result);

        TextStringBuilder commonPart = new TextStringBuilder()
                .append(disaster.getDisasterDescription())
                .appendNewLine().appendNewLine()
                .append("Shelter description: ")
                .append(generateShelterDescription(cards.getShelterDescription()))
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

    private void createResultMap(Set<Long> players, Locale locale, Cards cards, Map<Pair<Integer, Long>, List<String>> result) {
        fillWithRandomValue(players.size(), cards.getProfessions(), result,
                messageSource.getMessage(MessageCode.PROFESSION.getCode(), null, locale));
        fillWithRandomValue(players.size(), cards.getHealth(), result,
                messageSource.getMessage(MessageCode.HEALTH.getCode(), null, locale));

        fillBiologicalCharacteristics(players.size(), cards.getBiologicalCharacteristics(), result,
                messageSource.getMessage(MessageCode.BIOLOGICAL_CHARACTERISTICS.getCode(), null, locale));

        fillWithRandomValue(players.size(), cards.getHobby(), result,
                messageSource.getMessage(MessageCode.HOBBY.getCode(), null, locale));
        fillWithRandomValue(players.size(), cards.getAdditionalSkills(), result,
                messageSource.getMessage(MessageCode.ADDITIONAL_SKILLS.getCode(), null, locale));
        fillWithRandomValue(players.size(), cards.getLuggage(), result,
                messageSource.getMessage(MessageCode.LUGGAGE.getCode(), null, locale));

        fillSpecialAbilities(players.size(), cards.getSpecialAbilities(), result,
                messageSource.getMessage(MessageCode.SPECIAL_ABILITIES.getCode(), null, locale));
    }

    private void fillBiologicalCharacteristics(int playersSize, GameFilesConfiguration.BiologicalCharacteristics characteristics,
                                               Map<Pair<Integer, Long>, List<String>> result, String characteristicName) {
        int sexualityListSize = characteristics.getSexuality().size();
        int genderListSize = characteristics.getGender().size();

        IntStream.range(0, playersSize)
                .forEach(index -> {
                    String sexuality = characteristics.getSexuality().get(random.nextInt(0, sexualityListSize));
                    String gender = characteristics.getGender().get(random.nextInt(0, genderListSize));

                    result.get(result.keySet().stream().filter(pair -> Objects.equals(pair.getKey(), index)).findFirst().get()).add(new TextStringBuilder(characteristicName)
                            .append(": ")
                            .append(gender)
                            .append(" ").append("/").append(" ")
                            .append(random.nextInt(18, 70))
                            .append(" ").append("/").append(" ")
                            .append(sexuality)
                            .toString());
                });
    }

    private void fillSpecialAbilities(int playersSize, List<String> characteristics,
                                      Map<Pair<Integer, Long>, List<String>> result, String characteristicName) {

        IntStream.range(0, playersSize)
                .forEach(index -> {
                    List<Integer> abilities = randomNumbers(characteristics, 2);

                    result.get(result.keySet().stream()
                                    .filter(pair -> Objects.equals(pair.getKey(), index))
                                    .findFirst().get())
                            .add(new TextStringBuilder()
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

        List<Integer> randomListValues = randomNumbers(characteristics, playersSize);

        IntStream.range(0, playersSize)
                .forEach(index -> {
                    String characteristicValue = characteristics.get(randomListValues.get(index));
                    result.get(result.keySet().stream().filter(pair -> Objects.equals(pair.getKey(), index)).findFirst().get()).add(new TextStringBuilder(characteristicName)
                            .append(": ")
                            .append(characteristicValue)
                            .toString());
                });
    }

    private String generateShelterDescription(List<String> characteristics) {

        List<Integer> randomListValues = randomNumbers(characteristics, 5);

        TextStringBuilder textStringBuilder = new TextStringBuilder();
        StreamUtils.zipWithIndex(randomListValues.stream()
                        .map(characteristics::get))
                .forEachOrdered(stringIndexed -> textStringBuilder
                        .appendNewLine()
                        .append(stringIndexed.getValue()));
        return textStringBuilder.toString();
    }

    private List<Integer> randomNumbers(List<String> characteristics, int limit) {
        return random.ints(0, characteristics.size())
                .distinct()
                .boxed()
                .limit(limit)
                .toList();
    }
}
