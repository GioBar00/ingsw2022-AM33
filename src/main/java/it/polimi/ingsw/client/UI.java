package it.polimi.ingsw.client;

import it.polimi.ingsw.network.listeners.ViewListenerSubscriber;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.Winners;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;

/**
 * Interface for the user interfaces
 */
public interface UI extends ViewListenerSubscriber {

    /**
     * This method sets the WizardView.
     *
     * @param wizardsView the WizardView to set.
     */
    void setWizardView(WizardsView wizardsView);

    /**
     * This method sets the TeamView.
     *
     * @param teamsView the TeamView to set.
     */
    void setTeamsView(TeamsView teamsView);

    /**
     * This method sets the GameView.
     *
     * @param gameView the GameView to set.
     */
    void setGameView(GameView gameView);

    /**
     * This method requests to the user to choose the game mode and the number of players.
     */
    void chooseGame();

    /**
     * This method shows the start screen.
     */
    void showStartScreen();

    /**
     * This method shows the wizard menu where the player can choose the wizard.
     */
    void showWizardMenu();

    /**
     * This method shows the game menu where the player can choose the team.
     */
    void showLobbyScreen();

    /**
     * This method notifies the host that the game can start.
     */
    void hostCanStart();

    /**
     * This method notifies the host that the game cant start.
     */
    void hostCantStart();

    /**
     * This method shows the main game screen.
     */
    void showGameScreen();

    /**
     * This method sets the possible actions the player can do.
     *
     * @param message the message containing the possible actions.
     */
    void setPossibleActions(Message message);

    /**
     * This method notifies the player when the server is unavailable.
     */
    void serverUnavailable();

    /**
     * This method close the UI.
     */
    void close();

    /**
     * This method shows a {@link CommMessage} to the user.
     *
     * @param message the message to show.
     */
    void showCommMessage(CommMessage message);

    /**
     * This method shows the winners of the game.
     *
     * @param winners a {@link Winners} message containing the winners.
     */
    void showWinners(Winners winners);

    /**
     * This method notifies the player that they have to wait for the other players.
     */
    void showWaiting();
}
