package it.polimi.ingsw.model.cards;

abstract class CharacterCard {
    Integer cost;
    Integer additionalCost = 0;


    Integer getAdditionalCost() {
        return additionalCost;
    }

    Integer getTotalCost(){
        return cost + additionalCost;
    }

    //FIXME
    //abstract void applyEffect(GameModel model, Object[] args);

    boolean canHandleBlocks(){
        return false;
    }

    void addNumBlocks(Integer num) throws Exception {
        throw new Exception();
    }
}
