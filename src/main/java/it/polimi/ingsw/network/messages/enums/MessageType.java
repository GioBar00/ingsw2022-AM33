package it.polimi.ingsw.network.messages.enums;

import it.polimi.ingsw.network.messages.IgnoreMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.client.*;
import it.polimi.ingsw.network.messages.server.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum that represents the type of message
 */
public enum MessageType {
    /**
     * Invalid message
     */
    INVALID(null),
    /**
     * Ignore message
     */
    IGNORE(IgnoreMessage.class),
    // server messages
    /**
     * Server message: PlayAssistantCard
     */
    PLAY_ASSISTANT_CARD(PlayAssistantCard.class),
    /**
     * Server message: MoveStudent
     */
    MOVE_STUDENT(MoveStudent.class),
    /**
     * Server message: SwapStudents
     */
    SWAP_STUDENTS(SwapStudents.class),
    /**
     * Server message: MultiplePossibleMoves
     */
    MULTIPLE_POSSIBLE_MOVES(MultiplePossibleMoves.class),
    /**
     * Server message: MoveMotherNature
     */
    MOVE_MOTHER_NATURE(MoveMotherNature.class),
    /**
     * Server message: ChooseCloud
     */
    CHOOSE_CLOUD(ChooseCloud.class),
    /**
     * Server message: ChooseIsland
     */
    CHOOSE_ISLAND(ChooseIsland.class),
    /**
     * Server message: ChooseStudentColor
     */
    CHOOSE_STUDENT_COLOR(ChooseStudentColor.class),
    /**
     * Server message: CommeMessage
     */
    COMM_MESSAGE(CommMessage.class),
    /**
     * Server message: CurrentTeams
     */
    CURRENT_TEAMS(CurrentTeams.class),
    /**
     * Server message: CurrentGameState
     */
    CURRENT_GAME_STATE(CurrentGameState.class),
    /**
     * Server message: AvailableWizards
     */
    AVAILABLE_WIZARDS(AvailableWizards.class),

    // client messages
    /**
     * Client message: PlayedAssistantCard
     */
    PLAYED_ASSISTANT_CARD(PlayedAssistantCard.class),
    /**
     * Client message: MovedStudent
     */
    MOVED_STUDENT(MovedStudent.class),
    /**
     * Client message: SwappedStudents
     */
    SWAPPED_STUDENTS(SwappedStudents.class),
    /**
     * Client message: MovedMotherNature
     */
    MOVED_MOTHER_NATURE(MovedMotherNature.class),
    /**
     * Client message: ChosenCloud
     */
    CHOSEN_CLOUD(ChosenCloud.class),
    /**
     * Client message: ChosenIsland
     */
    CHOSEN_ISLAND(ChosenIsland.class),
    /**
     * Client message: ChosenStudentColor
     */
    CHOSEN_STUDENT_COLOR(ChosenStudentColor.class),
    /**
     * Client message: ActivatedCharacterCard
     */
    ACTIVATED_CHARACTER_CARD(ActivatedCharacterCard.class),
    /**
     * Client message: ConcludeCharacterCardEffect
     */
    CONCLUDE_CHARACTER_CARD_EFFECT(ConcludeCharacterCardEffect.class),
    /**
     * Client message: ChosenGame
     */
    CHOSEN_GAME(ChosenGame.class),
    /**
     * Client message: ChosenTeam
     */
    CHOSEN_TEAM(ChosenTeam.class),
    /**
     * Client message: ChosenWizard
     */
    CHOSEN_WIZARD(ChosenWizard.class),
    /**
     * Client message: Login
     */
    LOGIN(Login.class),
    /**
     * Client message: StartGame
     */
    START_GAME(StartGame.class),

    /**
     * Winners message containing the nicknames of the winners
     */
    WINNERS(Winners.class),

    // internal messages
    /**
     * Internal message: the client is connected
     */
    CONNECTED(Connected.class),
    /**
     * Internal message: the client is disconnected
     */
    DISCONNECTED(Disconnected.class);

    /**
     * A map that contains the message type and the class of the message.
     */
    private static final Map<Class<? extends Message>, MessageType> LOOKUP_MAP;

    static {
        LOOKUP_MAP = new HashMap<>();
        for (MessageType m : MessageType.values())
            LOOKUP_MAP.put(m.getMessageClass(), m);
    }

    /**
     * The class of the message.
     */
    private final Class<? extends Message> messageClass;

    /**
     * Constructor
     *
     * @param c the class of the message
     */
    MessageType(Class<? extends Message> c) {
        messageClass = c;
    }

    /**
     * Returns the class of the message
     *
     * @return the class of the message
     */
    public Class<? extends Message> getMessageClass() {
        return messageClass;
    }

    /**
     * Returns the type of the message
     *
     * @param message the message
     * @return the type of the message
     */
    public static MessageType retrieveByMessage(Message message) {
        if (LOOKUP_MAP.containsKey(message.getClass()))
            return LOOKUP_MAP.get(message.getClass());
        return INVALID;
    }
}


