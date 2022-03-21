package it.polimi.ingsw.model;

public enum AssistantCard {
    ONE(1,1), TWO(2,1), THREE(3,2), FOUR(4,2),
    FIVE(5,3), SIX(6,3), SEVEN(7,4) , EIGHT(8,4),
    NINE(9,5) ,TEN(10,5);

    private final int value;
    private final int moves;

     AssistantCard(int value,int moves){
        this.value = value;
        this.moves = moves;
    }
    public int getValue(){return value;}
    public int getMoves(){return moves;}

}
