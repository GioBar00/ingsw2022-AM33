package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.messages.messagesView.GameView;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.messages.Message;

public class CurrentGameState extends Message {
    private final GameView gameView;

    public CurrentGameState(Game game, Player dest) {
        gameView = game.getGameView(dest);
    }

    public GameView getGameView() {
        return gameView;
    }

    @Override
    public boolean isValid() {
        return gameView != null;
    }
}
