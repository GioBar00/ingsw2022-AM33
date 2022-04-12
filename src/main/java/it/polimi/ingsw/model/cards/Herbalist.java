package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.server.ChooseIsland;
import it.polimi.ingsw.util.Pair;

import java.util.Set;


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
        super(CharacterType.HERBALIST, 2, 1);
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
     * @param parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect && parameters != null) {
            if (numBlocks > 0) {
                Integer islandGroupIndex = parameters.getIndex();
                if (islandGroupIndex != null && effectHandler.blockIslandGroup(islandGroupIndex)) {
                    numBlocks--;
                    currentChoicesNumber++;
                    endEffect();
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * @param effectHandler effect handler.
     * @return choose island message.
     */
    @Override
    public Message getCommandMessage(EffectHandler effectHandler) {
        Set<Integer> islandIndexes = effectHandler.getAvailableIslandIndexes();
        return new ChooseIsland(islandIndexes);
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

    /**
     * For Tests only
     * @return num of blocks on the card.
     */
    public int getNumBlocks() {
        return numBlocks;
    }

}
