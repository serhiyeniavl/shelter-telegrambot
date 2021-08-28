# shelter-telegrambot

## Main bot commands

***/create*** - create new room (set user as owner) (set status `CREATE_ROOM`). Bot is waiting for players quantity.
Owner of a room should see ***/start_game*** button

***/start_game*** - could be invoked in `WAITING_OTHERS_TO_JOIN` by chat owner only. If all users joined a room the start the game.
Start the game means send for all participants of this room their roles in a game
and delete this room from database after particular amount of time (if error during
the message delivery from bot to user bot will retry sending while room exists) and set for all participants status `NEW_USER`

***/join*** - join the existing room by its number (set status `JOINING_ROOM`)

***/leave*** - abandon current room: during whe waiting others users to join the room something can go wrong,
then user input /leave (available from statuses `WAITING_OTHERS_TO_JOIN` or `IN_PLAY`)

***/destroy*** - delete room (only for room owner) (set status `DELETING_ROOM` and wait for user's confirmation)

***/help*** - all available commands with its brief description

***/description*** - game rules and description


## License

Distributed under [MIT License](https://opensource.org/licenses/MIT).