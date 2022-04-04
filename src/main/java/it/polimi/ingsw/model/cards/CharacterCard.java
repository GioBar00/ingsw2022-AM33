package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.EnumMap;
import java.util.List;

public abstract class CharacterCard {
    final CharacterType type;
    final int cost;
    int additionalCost = 0;

    protected CharacterCard(CharacterType type, int cost) {
        this.type = type;
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public int getAdditionalCost() {
        return additionalCost;
    }

    public int getTotalCost(){
        return cost + additionalCost;
    }

    public void initialize(EffectHandler effectHandler) {}

    public abstract boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs);

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

    boolean areMovesValid(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs, int maxSize, EnumMap<StudentColor, Integer> copy) {
        if (pairs == null || pairs.size() == 0 || pairs.size() > maxSize)
            return false;
        List<StudentColor> entrance = effectHandler.getStudentsInEntrance();

        for (Pair<StudentColor, List<Integer>> pair: pairs) {
            StudentColor s = pair.getFirst();
            if (s == null || copy.get(s) <= 0)
                return false;
            List<Integer> second = pair.getSecond();
            if (second == null || second.size() != 1)
                return false;
            Integer index = second.get(0);
            if (index == null || index < 0 || index >= entrance.size())
                return false;
            StudentColor onEntrance = entrance.get(index);
            entrance.set(index, s);
            copy.replace(s, copy.get(s) - 1);
            copy.replace(onEntrance, copy.get(onEntrance) + 1);
        }
        return true;
    }

}
