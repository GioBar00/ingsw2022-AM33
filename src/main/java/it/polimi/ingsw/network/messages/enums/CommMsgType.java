package it.polimi.ingsw.network.messages.enums;

public enum CommMsgType {
    // COMMUNICATION MESSAGES FOR ERRORS
    ERROR_NULL_NICKNAME ("ERROR: the nickname needs to be at least one character long"),
    ERROR_NICKNAME_UNAVIALABLE ("ERROR: the nickname chosen is already in use"),
    ERROR_NO_SPACE("ERROR: the party is already full"),
    ERROR_UNBALANCED_TEAMS ("ERROR: the game can be started only if the teams have the same number of players"),
    ERROR_NOT_YOUR_TURN ("ERROR: you can't do that! it's not your turn!"),
    ERROR_IMPOSSIBLE_MOVE("ERROR: you can't move that now!"),
    ERROR_GAME_SUSPENDEND ("ERROR: the other players have temporarily left; the game will resume when they reconnect..."),
    ERROR_INCORRECT_INPUT ("ERROR: the elements chosen are unavailable"),
    ERROR_SERVER ("ERROR: internal server error"),
    ERROR_SERVER_UNAVIALABLE ("ERROR: service unavailable"),

    // COMMUNICATION MESSAGES FOR REQUESTS/ACKs
    CHOOSE_NUM_PLAYERS ("how many players are gonna be in your party?"),
    OK ("acknowledgement");

    private final String message;

    CommMsgType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
