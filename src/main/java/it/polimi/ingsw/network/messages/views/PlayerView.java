package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.server.model.enums.Wizard;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerView implements Serializable {
    /**
     * nickname of the player
     */
    private final String nickname;
    /**
     * wizard of the player
     */
    private final Wizard wizard;
    /**
     * assistant cards that the player still has in its hand
     */
    private final ArrayList<AssistantCard> assistantCards;
    /**
     * last assistant card played (null if there's none)
     */
    private final AssistantCard playedCard;
    /**
     * number of assistant cards that the player still has in its hand
     */
    private final int numAssistantCards;
    /**
     * schoolboard of the player
     */
    private final SchoolBoardView schoolBoardView;

    public PlayerView(String nickname, Wizard wizard,ArrayList<AssistantCard> assistantCards, AssistantCard playedCard, int numAssistantCards, SchoolBoardView schoolBoardView) {
        this.nickname = nickname;
        this.wizard = wizard;
        this.assistantCards = assistantCards;
        this.playedCard = playedCard;
        this.numAssistantCards = numAssistantCards;
        this.schoolBoardView = schoolBoardView;
    }

    /**
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the wizard
     */
    public Wizard getWizard() {
        return wizard;
    }

    /**
     * @return the assistant cards
     */
    public ArrayList<AssistantCard> getAssistantCards() {
        return assistantCards;
    }

    /**
     * @return last assistant card played
     */
    public AssistantCard getPlayedCard() {
        return playedCard;
    }

    /**
     * @return number of assistant cards
     */
    public int getNumAssistantCards() {
        return numAssistantCards;
    }

    /**
     * @return the schoolboard
     */
    public SchoolBoardView getSchoolBoardView() {
        return schoolBoardView;
    }
}
