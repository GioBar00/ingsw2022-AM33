package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import java.util.EnumMap;
import java.util.List;

public abstract class CharacterCard {
    CharacterType type;
    int cost;
    int additionalCost = 0;

    public int getCost() {
        return cost;
    }

    public int getAdditionalCost() {
        return additionalCost;
    }

    public int getTotalCost(){
        return cost + additionalCost;
    }

    public abstract void applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs);

    public void endEffect(EffectHandler effectHandler) {}

    public boolean canHandleBlocks(){
        return false;
    }

    public void addNumBlocks(int num) {}

    public boolean containsStudents() {
        return false;
    }

    public EnumMap<StudentColor, Integer> getStudents() {
        return new EnumMap<>(StudentColor.class);
    }

}
