package it.polimi.ingsw.network.messages.enums;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.Move;
import it.polimi.ingsw.network.messages.client.*;
import it.polimi.ingsw.network.messages.server.*;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    INVALID(null),

    PLAY_ASSISTANT_CARD(PlayAssistantCard.class),
    MOVE_STUDENT(MoveStudent.class),
    SWAP_STUDENTS(SwapStudents.class),
    MULTIPLE_POSSIBLE_MOVES(MultiplePossibleMoves.class),
    MOVE_MOTHER_NATURE(MoveMotherNature.class),
    CHOOSE_CLOUD(ChooseCloud.class),
    CHOOSE_ISLAND(ChooseIsland.class),
    CHOOSE_STUDENT_COLOR(ChooseStudentColor.class),

    PLAYED_ASSISTANT_CARD(PlayedAssistantCard.class),
    MOVED_STUDENT(MovedStudent.class),
    SWAPPED_STUDENT(SwappedStudent.class),
    MOVED_MOTHER_NATURE(MovedMotherNature.class),
    CHOSEN_CLOUD(ChosenCloud.class),
    CHOSEN_ISLAND(ChosenIsland.class),
    CHOSEN_STUDENT_COLOR(ChosenStudentColor.class),
    ACTIVATED_CHARACTER_CARD(ActivatedCharacterCard.class),
    CONCLUDE_CHARACTER_CARD_EFFECT(ConcludeCharacterCardEffect.class);

    private static final Map<Class<? extends Message>, MessageType> LOOKUP_MAP;

    static {
        LOOKUP_MAP = new HashMap<>();
        for (MessageType m: MessageType.values())
            LOOKUP_MAP.put(m.getMessageClass(), m);
        LOOKUP_MAP.put(Message.class, INVALID);
        LOOKUP_MAP.put(Move.class, INVALID);
    }

    private final Class<? extends Message> messageClass;

    MessageType(Class<? extends Message> c) {
        messageClass = c;
    }

    public Class<? extends Message> getMessageClass() {
        return messageClass;
    }

    public static MessageType retrieveByMessageClass(Message c) {
        return LOOKUP_MAP.get(c.getClass());
    }
}


