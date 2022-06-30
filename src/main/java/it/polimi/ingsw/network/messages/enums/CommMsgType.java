package it.polimi.ingsw.network.messages.enums;

/**
 * Enum that contains the possible communication message types.
 */
public enum CommMsgType {
    // COMMUNICATION MESSAGES FOR ERRORS
    /**
     * error message: the nickname chosen is not valid because it is null
     */
    ERROR_INVALID_NICKNAME("ERROR: the nickname cannot be blank and cannot be longer than 20 characters"),
    /**
     * error message: the nickname is not available because another player has already claimed it
     */
    ERROR_NICKNAME_UNAVAILABLE("ERROR: the nickname chosen is already in use"),
    /**
     * error message: the game is already at full capacity
     */
    ERROR_NO_SPACE("ERROR: the party is already full"),
    /**
     * error message: the action requested cannot be done because the client doesn't have the rights to request it
     */
    ERROR_NOT_MASTER("ERROR: you don't have the right to do that"),
    /**
     * error message: the game has already begun, no player can be added at this time
     */
    ERROR_GAME_STARTED("ERROR: game is already started"),
    /**
     * error message: the action requested cannot be done because it's not the client's turn
     */
    ERROR_NOT_YOUR_TURN("ERROR: you can't do that! it's not your turn!"),
    /**
     * error message: the move requested cannot be performed because it is against the rules
     */
    ERROR_IMPOSSIBLE_MOVE("ERROR: you can't move that now!"),
    /**
     * error message: the game cannot be started because there aren't enough players yet
     */
    ERROR_CANT_START("ERROR : wait for other players. You can't start now"),
    /**
     * error message: the server is not available
     */
    ERROR_SERVER_UNAVAILABLE("ERROR: server unavailable"),
    /**
     * error message: the message sent to the server is invalid
     */
    ERROR_INVALID_MESSAGE("ERROR: the message is not valid"),
    /**
     * error message: the disconnection timeout went off
     */
    ERROR_TIMEOUT("ERROR: the connection has timed out"),

    /**
     * waiting message: the server is waiting for other players
     */
    WAITING("Wait for other players..."),
    // COMMUNICATION MESSAGES FOR REQUESTS/ACKs
    /**
     * communication message: ping message
     */
    PING("Are you still alive?"),
    /**
     * communication message: the client (who is also the host) is asked to choose what kind of game they want to play
     */
    CHOOSE_GAME("Choose the difficulty of the game: EASY or EXPERT.\nChoose the number of players: TWO, THREE or FOUR."),
    /**
     * communication message: the game can be started because the current conditions allow it
     */
    CAN_START("You can start the party. Be quick things could change"),
    /**
     * communication message: pong message
     */
    PONG("acknowledgement"),
    /**
     * communication message: ack message for the moves
     */
    OK("move is valid");

    /**
     * The content of the message.
     */
    private final String message;

    /**
     * Constructor.
     *
     * @param message the content of the message.
     */
    CommMsgType(String message) {
        this.message = message;
    }

    /**
     * Gets the content of the message.
     *
     * @return the content of the message.
     */
    public String getMessage() {
        return message;
    }
}
