package it.polimi.ingsw.server.controllers;

import it.polimi.ingsw.server.model.cards.CharacterParameters;
import it.polimi.ingsw.server.model.enums.StudentColor;

public class CharacterChoiceAdapter {
    boolean activatedCard;

    CharacterChoiceAdapter(){
        activatedCard = false;
    }
    void setActivatedCard() {
        activatedCard = true;
    }

    void resetActivatedCard() {
        activatedCard = false;
    }

    CharacterParameters chooseIsland(int index) {
        if(activatedCard)
            return new CharacterParameters(index);
        else return null;
    }

    CharacterParameters chooseColor(StudentColor color) {
        if(activatedCard)
            return new CharacterParameters(color);
        else return null;
    }

    CharacterParameters fromCard(int cardIndex, int index) {
        if(activatedCard) {
            return new CharacterParameters(StudentColor.retrieveStudentColorByOrdinal(cardIndex),index);
        }
        return null;
    }

    CharacterParameters fromHall(int hallIndex, int index) {
        if(activatedCard) {
            return new CharacterParameters(StudentColor.retrieveStudentColorByOrdinal(hallIndex),index);
        }
        return null;
    }

}
