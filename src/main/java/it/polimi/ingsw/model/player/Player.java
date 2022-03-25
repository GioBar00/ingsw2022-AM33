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

    Player(String nickname, Wizard wizard, SchoolBoard schoolBoard){
        this.nickname = nickname;
        this.wizard = wizard;
        this.assistantCards = new ArrayList<>();
        Collections.addAll(assistantCards, AssistantCard.values());
        this.playedCard = null;
        this.schoolBoard = schoolBoard;
    }


    public String getNickname(){return nickname;}

    Wizard getWizard(){return wizard;}

    SchoolBoard getSchoolBoard(){return schoolBoard;}

    AssistantCard getAssistantCard(){return playedCard;}

    void playAssistantCard (AssistantCard card) throws NoSuchElementException {
        if(assistantCards.contains(card)){
            playedCard = card;
            assistantCards.remove(card);
        }
        else throw new NoSuchElementException();
    }
    void clearPlayedCard(){playedCard = null;}

    public ArrayList<AssistantCard> getHand(){
        return new ArrayList<>(assistantCards);

    }
}
