package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.server.model.enums.Wizard;

import java.util.ArrayList;

public class PlayerView {
    private final String nickname;
    private final Wizard wizard;
    private final ArrayList<AssistantCard> assistantCards;
    private final AssistantCard playedCard;
    private final int numAssistantCards;
    private final SchoolBoardView schoolBoardView;

    public PlayerView(String nickname, Wizard wizard,ArrayList<AssistantCard> assistantCards, AssistantCard playedCard, int numAssistantCards, SchoolBoardView schoolBoardView) {
        this.nickname = nickname;
        this.wizard = wizard;
        this.assistantCards = assistantCards;
        this.playedCard = playedCard;
        this.numAssistantCards = numAssistantCards;
        this.schoolBoardView = schoolBoardView;
    }

    public String getNickname() {
        return nickname;
    }

    public Wizard getWizard() {
        return wizard;
    }

    public ArrayList<AssistantCard> getAssistantCards() {
        return assistantCards;
    }

    public AssistantCard getPlayedCard() {
        return playedCard;
    }

    public int getNumAssistantCards() {
        return numAssistantCards;
    }

    public SchoolBoardView getSchoolBoardView() {
        return schoolBoardView;
    }
}
