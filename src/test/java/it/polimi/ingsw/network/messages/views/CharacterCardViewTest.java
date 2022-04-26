package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.GameModelExpert;
import it.polimi.ingsw.server.model.cards.*;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.GamePreset;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCardViewTest {
    /**
     * the game model for the expert game
     */
    GameModelExpert gme = new GameModelExpert(new GameModel(GamePreset.THREE));

    /**
     * adds the player and prepares the game
     */
    void prepareModel() {
        gme.addPlayer("1");
        gme.addPlayer("2");
        gme.addPlayer("3");
        gme.startGame();
    }

    /**
     * Replaces the character cards in the game and initializes the card.
     * @param card to initialize.
     */
    void initializeCharacterCardOnGameModel(CharacterCard card) {
        gme.getCharacterCards().set(0, card);
        gme.getCharacterCards().set(1, card);
        gme.getCharacterCards().set(2, card);
        card.initialize(gme);
    }

    /**
     * resets the model to the condition where each player has enough coins to play any card
     */
    void resetModel(){
        gme.getModel().getRoundManager().startActionPhase();
        gme.getPlayerCoins().put("1", 10);
        gme.getPlayerCoins().put("2", 10);
        gme.getPlayerCoins().put("3", 10);
    }

    /**
     * resets the model to the condition where each player had no coins
     */
    void removeCoinsFormPlayer(){
        gme.getPlayerCoins().put("1", 0);
        gme.getPlayerCoins().put("2", 0);
        gme.getPlayerCoins().put("3", 0);
    }

    /**
     * for each type of character card, the test instantiates one and checks the attributes of the corresponding
     * characterCardView in two cases: when the destination player has enough money to play it and when it doesn't
     */
    @Test
    void characterCardViewTest(){
        prepareModel();
        for (CharacterType ct : CharacterType.values()) {
            resetModel();
            CharacterCard characterCard = ct.instantiate();

            initializeCharacterCardOnGameModel(characterCard);

            List<CharacterCardView> ccv = gme.getCharacterCardsView(gme.getModel().getPlayersManager().getPlayers().get(0).getNickname());
            CharacterCardView ccv1 = ccv.get(0);

            assertEquals(ct, ccv1.getType());
            assertTrue(ccv1.canBeUsed());
            assertEquals(characterCard.getCost(), ccv1.getOriginalCost());
            assertEquals(0, ccv1.getAdditionalCost());
            if (characterCard.canHandleBlocks())
                assertEquals(4, ccv1.getNumBlocks());
            else
                assertEquals(0, ccv1.getNumBlocks());
            if (characterCard.containsStudents())
                assertNotNull(ccv1.getStudent());
            else
                assertNull(ccv1.getStudent());

            removeCoinsFormPlayer();
            ccv = gme.getCharacterCardsView(gme.getModel().getPlayersManager().getPlayers().get(0).getNickname());
            ccv1 = ccv.get(0);

            assertEquals(ct, ccv1.getType());
            assertFalse(ccv1.canBeUsed());
            assertFalse(ccv1.isActivating());
            assertEquals(characterCard.getCost(), ccv1.getOriginalCost());
            assertEquals(0, ccv1.getAdditionalCost());
            if (characterCard.canHandleBlocks())
                assertEquals(4, ccv1.getNumBlocks());
            else
                assertEquals(0, ccv1.getNumBlocks());
            if (characterCard.containsStudents())
                assertNotNull(ccv1.getStudent());
            else
                assertNull(ccv1.getStudent());
        }
    }
}