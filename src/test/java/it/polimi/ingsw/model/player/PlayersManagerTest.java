package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enums.AssistantCard;
import org.junit.jupiter.api.Test;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//TODO JavaDOC
class PlayersManagerTest {
    PlayersManager pm = new PlayersManager(3);

    @Test
    void CreatePm(){
        //TestingAdd
        try{pm.addPlayer("Pl1",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }
        try{pm.addPlayer("Pl2",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }
        assertThrows(NameAlreadyBoundException.class, () -> pm.addPlayer("Pl1",1,1));
        assertEquals(1,pm.getAvailablePlayerSlots());
        try{pm.addPlayer("Pl3",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }
        assertThrows(NoPermissionException.class, () -> pm.addPlayer("Pl4",0,0));

    }

    @Test
    void playersManagerTest() {
        try{pm.addPlayer("Pl1",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }
        try{pm.addPlayer("Pl2",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }
        try{pm.addPlayer("Pl3",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }

        //Check slots
        assertEquals(0,pm.getAvailablePlayerSlots());

        //Check arrayList
        assertEquals("Pl1",pm.getCurrentPlayer().getNickname());
        assertEquals("Pl1",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl2",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl3",pm.getPlayers().get(2).getNickname());
        assertEquals("Pl1",pm.getCurrentPlayer().getNickname());

        //Check next
        pm.nextPlayer();
        assertEquals("Pl2",pm.getCurrentPlayer().getNickname());
        pm.nextPlayer();
        pm.nextPlayer();
        assertEquals("Pl1",pm.getCurrentPlayer().getNickname());
    }

    @Test
    void SortingTest(){
        try{pm.addPlayer("Pl1",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }
        try{pm.addPlayer("Pl2",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }
        try{pm.addPlayer("Pl3",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }

        pm.currentPlayerPlayed(AssistantCard.FOUR);
        pm.nextPlayer();
        pm.currentPlayerPlayed(AssistantCard.TWO);
        pm.nextPlayer();
        pm.currentPlayerPlayed(AssistantCard.FOUR);
        pm.nextPlayer();

        pm.calculatePlayerOrder();
        assertEquals("Pl2",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl1",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl3",pm.getPlayers().get(2).getNickname());
        pm.currentPlayerPlayed(AssistantCard.SEVEN);
        pm.nextPlayer();
        pm.currentPlayerPlayed(AssistantCard.TWO);
        pm.nextPlayer();
        pm.currentPlayerPlayed(AssistantCard.ONE);

        pm.calculatePlayerOrder();
        assertEquals("Pl3",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl1",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl2",pm.getPlayers().get(2).getNickname());
    }
    @Test
    void clockwiseTest (){
        try{pm.addPlayer("Pl1",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }
        try{pm.addPlayer("Pl2",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }
        try{pm.addPlayer("Pl3",6,9);}
        catch (NoPermissionException | NameAlreadyBoundException ignored){

        }

        pm.currentPlayerPlayed(AssistantCard.FOUR);
        pm.nextPlayer();
        pm.currentPlayerPlayed(AssistantCard.TWO);
        pm.nextPlayer();
        pm.currentPlayerPlayed(AssistantCard.FOUR);
        pm.nextPlayer();

        pm.calculatePlayerOrder();
        assertEquals("Pl2",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl1",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl3",pm.getPlayers().get(2).getNickname());
        pm.calculateClockwiseOrder();

        assertEquals("Pl2",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl3",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl1",pm.getPlayers().get(2).getNickname());


    }

}