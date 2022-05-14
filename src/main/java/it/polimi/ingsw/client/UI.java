package it.polimi.ingsw.client;

import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.network.listeners.ViewListenerSubscriber;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.server.model.enums.AssistantCard;

import java.util.EnumSet;

/**
 * Interface for the user interfaces
 */
public interface UI extends ViewListenerSubscriber {

    void setWizardView(WizardsView wizardsView);

    void setTeamsView(TeamsView teamsView);

    void setGameView(GameView gameView);

    void setHost();

    void showStartScreen();

    void showWizardMenu();

    void showLobbyScreen();

    void hostCanStart();

    void hostCantStart();

    void showGameScreen();

    void setPossibleMoves(Message message);

    void close();
    void updateGameView();

    void updateLobbyView();

    void showCommMessage(CommMessage message);
}
