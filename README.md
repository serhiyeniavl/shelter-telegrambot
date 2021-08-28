# shelter-telegrambot

/create - create new room (set user as owner) (set status CREATE_ROOM). Bot is waiting for players quantity.
Owner of a room should see /start_game button which invokes all users joined check and starts the game.
Start the game means delete this room from database after particular amount of time (in case of error during
the message delivery from bot to user) and set from all participants status NEW_USER

/join - join the existing room by its number (set status JOINING_ROOM)

/leave - abandon current room: during whe waiting others users to join the room something can go wrong,
then user input /leave (available from statuses WAITING_OTHERS_TO_JOIN or IN_PLAY)

/destroy - delete room (only for room owner) (set status DELETING_ROOM and wait for user's confirmation)

/help - all available commands

/description - game rules and description