package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.StudentColor;

import java.io.Serializable;
import java.util.EnumMap;

public class CharacterCardView implements Serializable {
    private final CharacterType type;
    private final boolean canBeUsed;
    private final int originalCost;
    private final int additionalCost;
    private final int numBlocks;
    private final EnumMap<StudentColor, Integer> student;

    private final boolean isActivating;

    public CharacterCardView(CharacterType type, boolean canBeUsed, int originalCost, int additionalCost, int numBlocks, EnumMap<StudentColor, Integer> students, boolean isActivating) {
        this.type = type;
        this.canBeUsed = canBeUsed;
        this.originalCost = originalCost;
        this.additionalCost = additionalCost;
        this.numBlocks = numBlocks;
        this.student = students;
        this.isActivating = isActivating;
    }

    public CharacterType getType() {
        return type;
    }

    public boolean canBeUsed() {
        return canBeUsed;
    }

    public int getOriginalCost() {
        return originalCost;
    }

    public int getAdditionalCost() {
        return additionalCost;
    }

    public int getNumBlocks() {
        return numBlocks;
    }

    public EnumMap<StudentColor, Integer> getStudent() {
        return student;
    }
}
