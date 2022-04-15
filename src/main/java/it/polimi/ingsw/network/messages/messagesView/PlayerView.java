package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.server.model.enums.Wizard;

import java.util.ArrayList;
import java.util.Collections;

public class PlayerView {
    private final String nickname;
    private final ArrayList<AssistantCard> assistantCards;
    private AssistantCard playedCard;
    private int coins;
    private int numAssistantCards;

    public PlayerView(String nickname, Wizard wizard, SchoolBoardView schoolBoardView){
        this.nickname = nickname;
        this.assistantCards = new ArrayList<>();
        Collections.addAll(assistantCards, AssistantCard.values());
        this.playedCard = null;
        this.coins = 0;
        this.numAssistantCards = 0;
    }

    public boolean playAssistantCard (AssistantCard card) {
        if(!assistantCards.contains(card))
            return false;
        playedCard = card;
        assistantCards.remove(card);
        return true;
    }

    public ArrayList<AssistantCard> getHand(){
        return new ArrayList<>(assistantCards);
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setNumAssistantCards(int numAssistantCards) {
        this.numAssistantCards = numAssistantCards;
    }

    public String getNickname() {
        return nickname;
    }
}
