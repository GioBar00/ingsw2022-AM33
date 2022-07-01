package it.polimi.ingsw.server.model.enums;

/**
 * enumeration for assistant cards: contains data for number of card, its value and number of moves
 */
public enum AssistantCard {
    /**
     * cheetah card
     */
    CHEETAH(1, 1),
    /**
     * ostrich card
     */
    OSTRICH(2, 1),
    /**
     * cat card
     */
    CAT(3, 2),
    /**
     * eagle card
     */
    EAGLE(4, 2),
    /**
     * fox card
     */
    FOX(5, 3),
    /**
     * snake card
     */
    SNAKE(6, 3),
    /**
     * octopus card
     */
    OCTOPUS(7, 4),
    /**
     * dog card
     */
    DOG(8, 4),
    /**
     * elephant card
     */
    ELEPHANT(9, 5),
    /**
     * turtle card
     */
    TURTLE(10, 5);

    /**
     * number of card
     */
    private final Integer value;

    /**
     * number of moves
     */
    private final Integer moves;

    /**
     * Public constructor of the class.
     *
     * @param value number of card
     * @param moves number of moves
     */
    AssistantCard(Integer value, Integer moves) {
        this.value = value;
        this.moves = moves;
    }

    /**
     * Getter for the value of the card
     *
     * @return the value of the card
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Getter for the number of moves
     *
     * @return the number of moves
     */
    public Integer getMoves() {
        return moves;
    }

    /**
     * Getter for the card from a number
     *
     * @param value the number of the card
     * @return the card
     */
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
