package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.network.messages.messagesView.GameView;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.network.messages.Message;


public class CurrentGameState extends Message{
    private final GameView gameView;

    public CurrentGameState(GameView gameView) {
        this.gameView = gameView;
    }

    public GameView getGameView() {
        return gameView;
    }

    @Override
    public boolean isValid() {
        if(gameView.getMode() == null || gameView.getPreset() == null || gameView.getPhase() == null || gameView.getState() == null)
            return false;
        if(gameView.getIslandsView() == null || gameView.getPlayersView() == null)
            return false;
        return true;
    }
}
