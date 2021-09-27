package com.wgc.shelter.service;

import com.wgc.shelter.config.game.GameFilesConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.TextStringBuilder;
import org.checkerframework.common.value.qual.IntRange;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.wgc.shelter.config.game.GameFilesConfiguration.Game;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GameFileCreatorService implements GameCreatorService {

    GameFilesConfiguration gameFilesConfiguration;

    @Override
    public Map<Long, String> createGame(Set<Long> players, Locale locale) {

        String language = locale.getLanguage();
        Game game = gameFilesConfiguration.getGame(language, new Random().nextInt(0, gameFilesConfiguration.countGames(language)));

        TextStringBuilder commonPart = new TextStringBuilder();

        commonPart
                .append(game.getDisasterDescription())
                .appendNewLine()
                .appendNewLine()
                .append(game.getShelterDescription().get(new Random().nextInt(0, game.getShelterDescription().size())))
                .appendNewLine()
                .appendNewLine()
                .append("Person: ")
                .appendNewLine();

        List<Integer> randomProfessions = new Random().ints(0, game.getRoles().getProfessions().size())
                .limit(players.size())
                .distinct()
                .boxed()
                .collect(Collectors.toList());

        int[] playerIndex = {0};
        Map<Integer, Pair<Long, String>> result = players.stream().collect(Collectors.toMap(id -> playerIndex[0]++, id -> Pair.of(id, "")));
        IntStream.range(0, players.size())
                .forEach(index -> {
                    String profession = game.getRoles().getProfessions().get(randomProfessions.get(index)).keySet().stream().findFirst().get();
                    Long userId = result.get(index).getKey();
                    result.put(index, Pair.of(userId, new TextStringBuilder(commonPart).append("Профессия:").append(profession)
                            .toString()));
                });
        int i = 0;
        return null;
    }
}
