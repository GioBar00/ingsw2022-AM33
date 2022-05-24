package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.network.messages.views.PlayerView;
import it.polimi.ingsw.network.messages.views.SchoolBoardView;
import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.server.model.enums.Wizard;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A Class that holds all the information of a player
 */
public class Player {

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
     *
     * @param nickname    nickname chosen by the player
     * @param wizard      random wizard given to the player
     * @param schoolBoard the schoolBoard related to the student
     */
    Player(String nickname, Wizard wizard, SchoolBoard schoolBoard) {
        this.nickname = nickname;
        this.wizard = wizard;
        this.assistantCards = new ArrayList<>();
        Collections.addAll(assistantCards, AssistantCard.values());
        this.playedCard = null;
        this.schoolBoard = schoolBoard;
    }

    /**
     * Used to get the nickname
     *
     * @return the nickname of the Player
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Used to get the wizard
     *
     * @return the wizard of the Player
     */
    Wizard getWizard() {
        return wizard;
    }

    /**
     * Used to get the schoolBoard
     *
     * @return the schoolBoard of the Player
     */
    SchoolBoard getSchoolBoard() {
        return schoolBoard;
    }

    /**
     * Calculates the last played card
     *
     * @return the last played card
     */
    AssistantCard getAssistantCard() {
        return playedCard;
    }

    /**
     * Tries to play an AssistantCard
     *
     * @param card the card the player wants to play
     * @return if the card was played successfully.
     */
    boolean playAssistantCard(AssistantCard card) {
        if (!assistantCards.contains(card))
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
     *
     * @return a list of remaining cards
     */
    public ArrayList<AssistantCard> getHand() {
        return new ArrayList<>(assistantCards);
    }

    /**
     * @param isDestPlayer whether the player considered to build the view is the one to whom the gameView will be sent
     * @return the playerView of the current player
     */
    public PlayerView getPlayerView(boolean isDestPlayer) {
        SchoolBoardView sbView = getSchoolBoard().getSchoolBoardView();
        ArrayList<AssistantCard> assistantCardsView = null;
        int numAssistantCards = 10;

        if (isDestPlayer) {
            assistantCardsView = assistantCards;
        }
        for (AssistantCard as : AssistantCard.values()) {
            if (!assistantCards.contains(as)) {
                numAssistantCards--;
            }
        }

        return new PlayerView(nickname, wizard, assistantCardsView, playedCard, numAssistantCards, sbView);
    }
}
