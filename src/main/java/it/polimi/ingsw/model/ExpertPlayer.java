package it.polimi.ingsw.model;

import javax.naming.LimitExceededException;


class ExpertPlayer extends Player {
    private int coins;

    ExpertPlayer(String nickname,Wizard wizard,SchoolBoard schoolBoard){
        super(nickname,wizard,schoolBoard);
        this.coins = 1;
    }

    int getCoin(){
        return coins;
    }
    void addCoin(){coins = coins + 1;}
    void removeCoins(int num) throws LimitExceededException {
        if(num > coins)
            throw new LimitExceededException();
        coins = coins - num;
    }


}
