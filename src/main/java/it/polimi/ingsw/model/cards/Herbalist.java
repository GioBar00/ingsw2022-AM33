package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
    public boolean applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        for (Map.Entry<StudentColor, List<Integer>> entry: pairs.entrySet()) {
            if (numBlocks > 0) {
                if (effectHandler.blockIslandGroup(entry.getValue().get(0))) {
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
