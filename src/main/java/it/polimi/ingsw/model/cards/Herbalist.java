package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.List;

/**
 * Herbalist character card.
 */
public class Herbalist extends CharacterCard {

    /**
     * number of block on the card.
     */
    int numBlocks;

    /**
     * Creates the herbalist
     */
    public Herbalist() {
        super(CharacterType.HERBALIST, 2);
    }

    /**
     * Initializes the character card. It puts 4 blocks on the card.
     * @param effectHandler handler for the effects.
     */
    @Override
    public void initialize(EffectHandler effectHandler) {
        numBlocks = 4;
    }

    /**
     * Applies the effect of the character card if the parameters are correct.
     * It blocks an island group.
     * @param effectHandler handler for the effects.
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        for (Pair<StudentColor, List<Integer>> pair: pairs) {
            if (numBlocks > 0) {
                List<Integer> second = pair.getSecond();
                if (second != null && second.size() > 0 && second.get(0) != null && effectHandler.blockIslandGroup(second.get(0))) {
                    additionalCost++;
                    numBlocks--;
                    appliedEffect = true;
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * @return if it can handle blocks.
     */
    @Override
    public boolean canHandleBlocks() {
        return true;
    }

    /**
     * Adds number of blocks back to the card.
     * @param num of blocks to add.
     */
    @Override
    public void addNumBlocks(int num) {
        numBlocks += num;
    }
}
