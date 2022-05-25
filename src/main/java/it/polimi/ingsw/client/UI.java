package it.polimi.ingsw.client;

import it.polimi.ingsw.network.listeners.ViewListenerSubscriber;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;

/**
 * Interface for the user interfaces
 */
public interface UI extends ViewListenerSubscriber {

    void setWizardView(WizardsView wizardsView);

    void setTeamsView(TeamsView teamsView);

    void setGameView(GameView gameView);

    void chooseGame();

    void showStartScreen();

    void showWizardMenu();

    void showLobbyScreen();

    void hostCanStart();

    void hostCantStart();

    void showGameScreen();

    void setPossibleMoves(Message message);

    void serverUnavailable();

    void close();

    void showCommMessage(CommMessage message);
}
