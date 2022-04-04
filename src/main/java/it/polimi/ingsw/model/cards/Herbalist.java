package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.List;

public class Herbalist extends CharacterCard {

    int numBlocks;

    public Herbalist() {
        super(CharacterType.HERBALIST, 2);
    }

    @Override
    public void initialize(EffectHandler effectHandler) {
        numBlocks = 4;
    }

    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        for (Pair<StudentColor, List<Integer>> pair: pairs) {
            if (numBlocks > 0) {
                List<Integer> second = pair.getSecond();
                if (second != null && second.size() > 0 && effectHandler.blockIslandGroup(second.get(0))) {
                    additionalCost++;
                    numBlocks--;
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean canHandleBlocks() {
        return true;
    }

    @Override
    public void addNumBlocks(int num) {
        numBlocks += num;
    }
}
