package com.wgc.shelter.action;

import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.common.BaseSpringBootTestClass;
import com.wgc.shelter.common.UpdateBotMessageSetup;
import com.wgc.shelter.model.Room;
import com.wgc.shelter.model.RoomState;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import com.wgc.shelter.service.GameCreatorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class StartGameAnywayCommandActionTest extends BaseSpringBootTestClass {

    @SpyBean
    GameCreatorService gameCreatorService;


    @Test
    @DisplayName("Start game anyway")
    void startGameAnyway() throws TelegramApiException {
        Long telegramUserId = 100L;

        User initialOwner = saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User player1 = saveUser(telegramUserId + 1, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User player2 = saveUser(telegramUserId + 2, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User player3 = saveUser(telegramUserId + 3, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());

        Set<Long> players = Set.of(initialOwner.getTelegramUserId(), player1.getTelegramUserId(), player2.getTelegramUserId(), player3.getTelegramUserId());
        Room initialRoom = saveRoom(initialOwner.getTelegramUserId(), players, 6, RoomState.WAITING_TO_JOIN, LocalDateTime.now());

        Map<Long, String> gameSetup = Map.of(initialOwner.getTelegramUserId(), "game setup1",
                player1.getTelegramUserId(), "game setup2",
                player2.getTelegramUserId(), "game setup3",
                player3.getTelegramUserId(), "game setup4");
        Mockito.doReturn(gameSetup)
                .when(gameCreatorService).createGame(players, new Locale(initialOwner.getLocale()));

        UpdateBotMessageSetup botMessageSetup1 = buildUpdateObject(initialOwner.getTelegramUserId(), "user", EN_US.toString(),
                UserCommand.START_GAME_ANYWAY.getCommand(), "ignore", null);
        UpdateBotMessageSetup botMessageSetup2 = buildUpdateObject(player1.getTelegramUserId(), "user", EN_US.toString(),
                "ignore", "ignore", null);
        UpdateBotMessageSetup botMessageSetup3 = buildUpdateObject(player2.getTelegramUserId(), "user", EN_US.toString(),
                "ignore", "ignore", null);
        UpdateBotMessageSetup botMessageSetup4 = buildUpdateObject(player3.getTelegramUserId(), "user", EN_US.toString(),
                "ignore", "ignore", null);

        botMessageSetup1.messageToSend().setText(gameSetup.get(initialOwner.getTelegramUserId()));
        botMessageSetup2.messageToSend().setText(gameSetup.get(player1.getTelegramUserId()));
        botMessageSetup3.messageToSend().setText(gameSetup.get(player2.getTelegramUserId()));
        botMessageSetup4.messageToSend().setText(gameSetup.get(player3.getTelegramUserId()));
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup1.messageToSend());
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup2.messageToSend());
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup3.messageToSend());
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup4.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup1.update()));

        User actualOwner = userRepository.findByTelegramUserId(initialOwner.getTelegramUserId()).get();
        User actualPlayer1 = userRepository.findByTelegramUserId(player1.getTelegramUserId()).get();
        User actualPlayer2 = userRepository.findByTelegramUserId(player2.getTelegramUserId()).get();
        User actualPlayer3 = userRepository.findByTelegramUserId(player3.getTelegramUserId()).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(initialOwner.setState(UserActionState.NEW_USER), actualOwner),
                () -> Assertions.assertEquals(player1.setState(UserActionState.NEW_USER), actualPlayer1),
                () -> Assertions.assertEquals(player2.setState(UserActionState.NEW_USER), actualPlayer2),
                () -> Assertions.assertEquals(player3.setState(UserActionState.NEW_USER), actualPlayer3),

                () -> Assertions.assertTrue(roomRepository.findByOwnerId(initialOwner.getTelegramUserId()).isEmpty())
        );
    }

}