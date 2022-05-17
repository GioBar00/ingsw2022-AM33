package it.polimi.ingsw.server.model.enums;

/**
 * enumeration for assistant cards: contains data for number of card, its value and number of moves
 */
public enum AssistantCard {
    CHEETAH(1, 1), OSTRICH(2, 1), CAT(3, 2), EAGLE(4, 2),
    FOX(5, 3), SNAKE(6, 3), OCTOPUS(7, 4), DOG(8, 4),
    ELEPHANT(9, 5), TURTLE(10, 5);

    private final Integer value;
    private final Integer moves;

    AssistantCard(Integer value, Integer moves) {
        this.value = value;
        this.moves = moves;
    }

    public Integer getValue() {
        return value;
    }

    public Integer getMoves() {
        return moves;
    }

    public static AssistantCard getFromInt(int value) {
        return switch (value) {
            case 1 -> CHEETAH;
            case 2 -> OSTRICH;
            case 3 -> CAT;
            case 4 -> EAGLE;
            case 5 -> FOX;
            case 6 -> SNAKE;
            case 7 -> OCTOPUS;
            case 8 -> DOG;
            case 9 -> ELEPHANT;
            case 10 -> TURTLE;
            default -> null;
        };
    }
}
