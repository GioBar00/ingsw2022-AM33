package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.server.ChooseIsland;

import java.util.Set;


/**
 * Herald character card.
 */
public class Herald extends CharacterCard {

    /**
     * Creates the herald.
     */
    public Herald() {
        super(CharacterType.HERALD, 3, 1);
    }

    /**
     * Applies the effect of the character card if the parameters are valid.
     * It forces the calc influence on an island group.
     * @param effectHandler handler for the effects.
     * @param parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect && parameters != null) {
            Integer islandGroupIndex = parameters.getIndex();
            if (islandGroupIndex != null && effectHandler.calcInfluenceOnIslandGroup(islandGroupIndex)) {
                currentChoicesNumber++;
                endEffect();
                return true;
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
}
