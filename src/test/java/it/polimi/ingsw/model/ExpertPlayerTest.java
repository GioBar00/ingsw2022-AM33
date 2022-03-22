package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import javax.naming.LimitExceededException;

import static org.junit.jupiter.api.Assertions.*;

class ExpertPlayerTest {

    @Test
    void coinTest() {
        ExpertPlayer player = new ExpertPlayer("test", Wizard.ONE,new SchoolBoard(23,Tower.GREY,5));
        assertEquals(1,player.getCoin());
        player.addCoin();
        assertEquals(2,player.getCoin());
        try{player.removeCoins(2);
            assertEquals(0,player.getCoin());
        }
        catch(LimitExceededException ignored) {}
        assertThrows(LimitExceededException.class,()->player.removeCoins(1));
        assertThrows(LimitExceededException.class,()->player.removeCoins(10));
    }

}