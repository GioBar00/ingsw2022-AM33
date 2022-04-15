package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.CharacterType;

public class CharacterCardView {
    private final CharacterType type;
    private boolean canBeUsed;
    private int originalCost;
    private int additionalCost;

    public CharacterCardView(CharacterType type) {
        this.type = type;
        this.canBeUsed = false;
        this.originalCost = 0;
        this.additionalCost = 0;
    }

    public void setCanBeUsed(boolean canBeUsed) {
        this.canBeUsed = canBeUsed;
    }

    public void setOriginalCost(int originalCost) {
        this.originalCost = originalCost;
    }

    public void setAdditionalCost(int additionalCost) {
        this.additionalCost = additionalCost;
    }
}
