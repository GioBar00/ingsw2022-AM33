package it.polimi.ingsw.client;

import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.network.listeners.ViewListenerSubscriber;
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

    void setHost();

    void showError(CommMessage message);
    void showStartScreen();

    void showWizardMenu();

    void showLobbyScreen();

    void showGameScreen();

    void updateGameView();

    void updateLobbyView();

    void showCommMessage();
}
