package it.polimi.ingsw.network.messages.enums;

public enum CommMsgType {
    // COMMUNICATION MESSAGES FOR ERRORS
    ERROR_NULL_NICKNAME ("ERROR: the nickname needs to be at least one character long"),
    ERROR_NICKNAME_UNAVAILABLE ("ERROR: the nickname chosen is already in use"),
    ERROR_NO_SPACE("ERROR: the party is already full"),
    ERROR_UNBALANCED_TEAMS ("ERROR: the game can be started only if the teams have the same number of players"),
    ERROR_NOT_MASTER("ERROR: you don't have the right"),
    ERROR_GAME_STARTED("ERROR: game is already started"),
    ERROR_NOT_YOUR_TURN ("ERROR: you can't do that! it's not your turn!"),
    ERROR_IMPOSSIBLE_MOVE("ERROR: you can't move that now!"),
    ERROR_GAME_SUSPENDED("ERROR: the other players have temporarily left; the game will resume when they reconnect..."),
    ERROR_INCORRECT_INPUT ("ERROR: the elements chosen are unavailable"),
    ERROR_SERVER ("ERROR: internal server error"),

    ERROR_CANT_START("ERROR : wait for other players. You can't start now"),
    ERROR_SERVER_UNAVAILABLE("ERROR: service unavailable"),
    ERROR_INVALID_MESSAGE("ERROR: the message is not valid"),
    ERROR_HOST_DISCONNECTED("ERROR: the host has left the party."),

    // COMMUNICATION MESSAGES FOR REQUESTS/ACKs
    PING("Are you still alive?"),
    CHOOSE_GAME("Choose the difficulty of the game: EASY or EXPERT.\nChoose the number of players: TWO, THREE or FOUR."),
    PONG("acknowledgement");

    private final String message;

    CommMsgType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
