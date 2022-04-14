package it.polimi.ingsw.network.messages.enums;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.Move;
import it.polimi.ingsw.network.messages.client.*;
import it.polimi.ingsw.network.messages.messagesView.GameView;
import it.polimi.ingsw.network.messages.server.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum that represents the type of message
 */
public enum MessageType {
    INVALID(null),
    // server messages
    PLAY_ASSISTANT_CARD(PlayAssistantCard.class),
    MOVE_STUDENT(MoveStudent.class),
    SWAP_STUDENTS(SwapStudents.class),
    MULTIPLE_POSSIBLE_MOVES(MultiplePossibleMoves.class),
    MOVE_MOTHER_NATURE(MoveMotherNature.class),
    CHOOSE_CLOUD(ChooseCloud.class),
    CHOOSE_ISLAND(ChooseIsland.class),
    CHOOSE_STUDENT_COLOR(ChooseStudentColor.class),
    COMM_MESSAGE(CommMessage.class),
    CURRENT_TEAMS(CurrentTeams.class),
    CURRENT_GAME_STATE(GameView.class),

    // client messages
    PLAYED_ASSISTANT_CARD(PlayedAssistantCard.class),
    MOVED_STUDENT(MovedStudent.class),
    SWAPPED_STUDENTS(SwappedStudents.class),
    MOVED_MOTHER_NATURE(MovedMotherNature.class),
    CHOSEN_CLOUD(ChosenCloud.class),
    CHOSEN_ISLAND(ChosenIsland.class),
    CHOSEN_STUDENT_COLOR(ChosenStudentColor.class),
    ACTIVATED_CHARACTER_CARD(ActivatedCharacterCard.class),
    CONCLUDE_CHARACTER_CARD_EFFECT(ConcludeCharacterCardEffect.class),
    CHOSEN_GAME(ChosenGame.class),
    CHOSEN_TEAM(ChosenTeam.class),
    LOGIN(Login.class),
    START_GAME(StartGame.class);

    private static final Map<Class<? extends Message>, MessageType> LOOKUP_MAP;

    static {
        LOOKUP_MAP = new HashMap<>();
        for (MessageType m: MessageType.values())
            LOOKUP_MAP.put(m.getMessageClass(), m);
        LOOKUP_MAP.put(Message.class, INVALID);
        LOOKUP_MAP.put(Move.class, INVALID);
    }

    private final Class<? extends Message> messageClass;

    /**
     * Constructor
     * @param c the class of the message
     */
    MessageType(Class<? extends Message> c) {
        messageClass = c;
    }

    /**
     * Returns the class of the message
     * @return the class of the message
     */
    public Class<? extends Message> getMessageClass() {
        return messageClass;
    }

    /**
     * Returns the type of the message
     * @param message the message
     * @return the type of the message
     */
    public static MessageType retrieveByMessageClass(Message message) {
        return LOOKUP_MAP.get(message.getClass());
    }
}

