package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

class Player{
    private final String nickname;
    private final Wizard wizard;
    private final ArrayList<AssistantCard> assistantCards;
    private AssistantCard playedCard;
    private final SchoolBoard schoolBoard;

    Player(String nickname,Wizard wizard,SchoolBoard schoolBoard){
        this.nickname = nickname;
        this.wizard = wizard;
        this.assistantCards = new ArrayList<>();
        Collections.addAll(assistantCards, AssistantCard.values());
        this.playedCard = null;
        this.schoolBoard = schoolBoard;
    }


    String getNickname(){return nickname;}

    Wizard getWizard(){return wizard;}
    SchoolBoard getSchoolBoard(){return schoolBoard;}
    AssistantCard getAssistantCard(){return playedCard;}
    void playAssistantCard (AssistantCard card) throws NoSuchElementException {
        if(assistantCards.contains(card)){
            playedCard = assistantCards.get(assistantCards.indexOf(card));
        }
        else throw new NoSuchElementException();
    }
    void clearPlayedCard(){playedCard = null;}
}
