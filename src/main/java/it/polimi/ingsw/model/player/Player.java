package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enums.AssistantCard;
import it.polimi.ingsw.enums.Wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

public class Player{
    private final String nickname;
    private final Wizard wizard;
    private final ArrayList<AssistantCard> assistantCards;
    private AssistantCard playedCard;
    private final SchoolBoard schoolBoard;

    /**
     * Constructor of Player
     * @param nickname nickname chosen by the player
     * @param wizard random wizard given to the player
     * @param schoolBoard the schoolBoard related to the student
     */
    Player(String nickname, Wizard wizard, SchoolBoard schoolBoard){
        this.nickname = nickname;
        this.wizard = wizard;
        this.assistantCards = new ArrayList<>();
        Collections.addAll(assistantCards, AssistantCard.values());
        this.playedCard = null;
        this.schoolBoard = schoolBoard;
    }

    /**
     * Used to get the nickname
     * @return the nickname of the Player
     */
    public String getNickname(){return nickname;}

    /**
     * Used to get the wizard
     * @return the wizard of the Player
     */
    Wizard getWizard(){return wizard;}

    /**
     * Used to get the schoolBoard
     * @return the schoolBoard of the Player
     */
    SchoolBoard getSchoolBoard(){return schoolBoard;}

    /**
     * Calculates the last played card
     * @return the last played card
     */
    AssistantCard getAssistantCard(){return playedCard;}

    /**
     * Tries to play an AssistantCard
     * @param card the card the player wants to play
     * @throws NoSuchElementException if the card is not in the player hand
     */
    void playAssistantCard (AssistantCard card) throws NoSuchElementException {
        if(assistantCards.contains(card)){
            playedCard = card;
            assistantCards.remove(card);
        }
        else throw new NoSuchElementException();
    }

    /**
     * Removes the last played card
     */
    void clearPlayedCard(){playedCard = null;}

    /**
     * Calculates the remaining cards of the player
     * @return a list of remaining cards
     */
    public ArrayList<AssistantCard> getHand(){
        return new ArrayList<>(assistantCards);

    }
}
