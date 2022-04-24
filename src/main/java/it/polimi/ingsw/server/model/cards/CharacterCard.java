package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.Message;

import java.util.*;

/**
 * Abstract class of a character card.
 */
public abstract class CharacterCard {
    /**
     * type of the card.
     */
    final CharacterType type;
    /**
     * cost of the card.
     */
    final int cost;
    /**
     * additional cost of the card.
     */
    int additionalCost = 0;
    /**
     * if the effect of the card was applied.
     */
    boolean appliedEffect = false;
    /**
     * current choices number.
     */
    int currentChoicesNumber = 0;
    /**
     * required choices number to complete the effect.
     */
    final int requiredChoicesNumber;
    /**
     * maximum choices number to complete the effect.
     */
    final int maximumChoicesNumber;

    /**
     * Constructs the card with no required choices.
     * @param type of the card.
     * @param cost of the card.
     */
    CharacterCard(CharacterType type, int cost) {
        this(type, cost, 0, 0);
    }
    /**
     * Constructs the card with required choices.
     * @param type of the card.
     * @param cost of the card.
     * @param requiredChoicesNumber of the card.
     */
    CharacterCard(CharacterType type, int cost, int requiredChoicesNumber) {
        this(type, cost, requiredChoicesNumber, requiredChoicesNumber);
    }
    /**
     * Constructs the card.
     * @param type of the card.
     * @param cost of the card.
     * @param requiredChoicesNumber of the card.
     * @param maximumChoicesNumber of the card.
     */
    CharacterCard(CharacterType type, int cost, int requiredChoicesNumber, int maximumChoicesNumber) {
        this.type = type;
        this.cost = cost;
        this.requiredChoicesNumber = requiredChoicesNumber;
        this.maximumChoicesNumber = maximumChoicesNumber;
    }

    /**
     * @return cost.
     */
    public int getCost() {
        return cost;
    }

    /**
     * @return additional cost.
     */
    public int getAdditionalCost() {
        return additionalCost;
    }

    /**
     * @return total cost.
     */
    public int getTotalCost(){
        return cost + additionalCost;
    }

    /**
     * @return requires choices number.
     */
    public int getRequiredChoicesNumber() {
        return requiredChoicesNumber;
    }

    /**
     * Initializes the character card.
     * @param effectHandler handler for the effects.
     */
    public void initialize(EffectHandler effectHandler) {}

    /**
     * Applies the effect of the character card.
     * @param effectHandler handler for the effects.
     * @param parameters for the effect.
     * @return if the effect was applied.
     */
    public abstract boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters);

    /**
     * Ends the effect if at least the required choices were make.
     * @return if the effect was ended correctly.
     */
    public boolean endEffect() {
        if (!appliedEffect && currentChoicesNumber >= requiredChoicesNumber) {
            additionalCost++;
            appliedEffect = true;
            return true;
        }
        return false;
    }

    /**
     * Reverts the effect of the character card if the effect is not permanent.
     * @param effectHandler handler for the effects.
     */
    public void revertEffect(EffectHandler effectHandler) {
        if (appliedEffect) {
            appliedEffect = false;
            currentChoicesNumber = 0;
        }

    }

    /**
     * @return if card has applied effect.
     */
    public boolean hasAppliedEffect() {
        return appliedEffect;
    }

    /**
     * @param effectHandler effect handler.
     * @return message to send to the client.
     */
    public Message getRequiredAction(EffectHandler effectHandler) {
        return null;
    }

    /**
     * @return if it can handle blocks.
     */
    public boolean canHandleBlocks(){
        return false;
    }

    /**
     * Adds number of blocks back to the card.
     * @param num of blocks to add.
     */
    public void addNumBlocks(int num) {}

    /**
     * @return if it contains students.
     */
    public boolean containsStudents() {
        return false;
    }

    /**
     * @return the students on the card
     */
    public EnumMap<StudentColor, Integer> getStudents() {
        return new EnumMap<>(StudentColor.class);
    }

    /**
     * @return an ordinal student set of the available students.
     */
    static Set<Integer> getAvailableStudentsOrdinal(EnumMap<StudentColor, Integer> students) {
        Set<Integer> availableStudents = new HashSet<>();
        for (Map.Entry<StudentColor, Integer> entry: students.entrySet())
            if (students.get(entry.getKey()) > 0)
                availableStudents.add(entry.getKey().ordinal());
        return availableStudents;
    }

    /**
     * @return the available entrance indexes.
     */
    static Set<Integer> getAvailableEntranceIndexes(EffectHandler effectHandler) {
        List<StudentColor> entrance = effectHandler.getStudentsInEntrance();
        Set<Integer> availableEntranceIndexes = new HashSet<>();
        for (int i = 0; i < entrance.size(); i++)
            if (entrance.get(i) != null)
                availableEntranceIndexes.add(i);
        return availableEntranceIndexes;
    }

    /**
     * @return type of the card
     */
    public CharacterType getType() {
        return type;
    }

    /**
     * @return num of blocks
     */
    public int getNumBlocks(){
        return 0;
    }
}
