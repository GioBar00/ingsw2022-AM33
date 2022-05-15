package it.polimi.ingsw.server.model.enums;

/**
 * enumeration for assistant cards: contains data for number of card, its value and number of moves
 */
public enum AssistantCard {
    ONE(1,1), TWO(2,1), THREE(3,2), FOUR(4,2),
    FIVE(5,3), SIX(6,3), SEVEN(7,4) , EIGHT(8,4),
    NINE(9,5) ,TEN(10,5);

    private final Integer value;
    private final Integer moves;

     AssistantCard(Integer value, Integer moves){
        this.value = value;
        this.moves = moves;
    }
    public Integer getValue(){return value;}
    public Integer getMoves(){return moves;}

    public static AssistantCard getFromInt(int value){
        return switch (value) {
            case 1 -> ONE;
            case 2 -> TWO;
            case 3 -> THREE;
            case 4 -> FOUR;
            case 5 -> FIVE;
            case 6 -> SIX;
            case 7 -> SEVEN;
            case 8 -> EIGHT;
            case 9 -> NINE;
            case 10 -> TEN;
            default -> null;
        };
    }
}
