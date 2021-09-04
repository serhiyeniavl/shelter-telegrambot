# shelter-telegrambot

## Main bot commands

***/start*** - save user data into database with status `NEW_USER`. Define user's locale (language).
If user already exists do nothing

***/create*** - create new room (set user as owner) (set status `CREATE_ROOM`). Check user status `NEW_USER` 
if it isn't then send appropriate message depend on user's status. Bot is waiting for players quantity.
Owner of a room should see ***/start_game*** button

***/start_game*** - could be invoked with status `WAITING_OTHERS_TO_JOIN` by chat owner only.
When all users have joined the room game owner is able to start the game.
Start the game means send for all participants of this room their roles in a game
and delete this room from database after particular amount of time (if error during
the message delivery from bot to user bot will retry sending while room exists) and set for all participants status `NEW_USER`

***/join*** - join the existing room by its number (set status `JOINING_ROOM`)

***/leave*** - abandon current room: during whe waiting others users to join the room something can go wrong,
then user input /leave (available from statuses `WAITING_OTHERS_TO_JOIN` or `IN_PLAY`)

***/destroy*** - delete room (only for room owner) (set status `DELETING_ROOM` and wait for user's confirmation)

***/help*** - all available commands with its brief description

***/description*** - game rules and description

***/change_lang*** - ability to change default language


## License

Distributed under [MIT License](https://opensource.org/licenses/MIT).
