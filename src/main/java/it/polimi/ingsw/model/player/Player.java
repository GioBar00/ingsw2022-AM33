package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enums.AssistantCard;
import it.polimi.ingsw.enums.Wizard;

import java.util.ArrayList;
import java.util.Collections;

public class Player{
    /**
     * nickname of the player
     */
    private final String nickname;
    /**
     * wizard of the player
     */
    private final Wizard wizard;
    /**
     * assistantCards of the player
     */
    private final ArrayList<AssistantCard> assistantCards;
    /**
     * assistantCard currently player by the player
     */
    private AssistantCard playedCard;
    /**
     * schoolBoard of the player
     */
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
    public String getNickname() {
        return nickname;
    }

    /**
     * Used to get the wizard
     * @return the wizard of the Player
     */
    Wizard getWizard() {
        return wizard;
    }

    /**
     * Used to get the schoolBoard
     * @return the schoolBoard of the Player
     */
    SchoolBoard getSchoolBoard() {
        return schoolBoard;
    }

    /**
     * Calculates the last played card
     * @return the last played card
     */
    AssistantCard getAssistantCard() {
        return playedCard;
    }

    /**
     * Tries to play an AssistantCard
     * @param card the card the player wants to play
     * @return if the card was played successfully.
     */
    boolean playAssistantCard (AssistantCard card) {
        if(!assistantCards.contains(card))
            return false;
        playedCard = card;
        assistantCards.remove(card);
        return true;
    }

    /**
     * Removes the last played card
     */
    void clearPlayedCard() {
        playedCard = null;
    }

    /**
     * Calculates the remaining cards of the player
     * @return a list of remaining cards
     */
    public ArrayList<AssistantCard> getHand(){
        return new ArrayList<>(assistantCards);
    }
}
