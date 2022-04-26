package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.Message;


public class CurrentGameState implements Message {
    /**
     * The current game view.
     */
    private final GameView gameView;

    /**
     * Constructor.
     * @param gameView the current game view.
     */
    public CurrentGameState(GameView gameView) {
        this.gameView = gameView;
    }

    /**
     * Getter.
     * @return the current game view.
     */
    public GameView getGameView() {
        return gameView;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        if(gameView.getMode() == null || gameView.getPreset() == null || gameView.getPhase() == null || gameView.getState() == null)
            return false;
        return gameView.getIslandsView() != null && gameView.getPlayersView() != null;
    }
}
