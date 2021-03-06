package it.polimi.ingsw.server.lobby;

import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.server.AvailableWizards;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.CurrentTeams;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.network.listeners.ConcreteMessageListenerSubscriber;
import it.polimi.ingsw.server.PlayerDetails;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;

import java.util.*;

/**
 * Class that handle the game lobby
 */
public class Lobby extends ConcreteMessageListenerSubscriber {

    /**
     * List of players in the lobby with their details
     */
    protected final List<PlayerDetails> players;

    /**
     * Max number of players
     */
    protected final int maxPlayers;

    /**
     * Constructor of the class
     *
     * @param maxPlayers the max number of player that can be hosted
     */
    public Lobby(int maxPlayers) {
        players = new ArrayList<>();
        this.maxPlayers = maxPlayers;
    }

    /**
     * Adds a player to the Lobby
     *
     * @param nickname the nickname of the host
     * @return false if it cannot be added ture otherwise
     */
    public boolean addPlayer(String nickname) {
        if (players.size() >= maxPlayers)
            return false;
        for (PlayerDetails p : players) {
            if (p.getNickname().equals(nickname))
                return false;
        }
        players.add(new PlayerDetails(nickname));
        return true;
    }

    /**
     * Try to give to the player a selected wizard
     *
     * @param wizard   the wizard requested
     * @param nickname the player who made the request
     * @return true if the wizard has been added false otherwise
     */
    public boolean setWizard(Wizard wizard, String nickname) {
        PlayerDetails update = null;
        for (PlayerDetails p : players) {
            if (p.getNickname().equals(nickname)) {
                update = p;
            } else if (p.getWizard() != null)
                if (p.getWizard().equals(wizard))
                    return false;
        }
        if (update != null) {
            if (update.getWizard() == null) {
                update.setWizard(wizard);
                return true;
            }
        }
        return false;
    }

    /**
     * Getter of the game Host/Master
     *
     * @return the nickname of the host/master
     */
    public String getMaster() {
        return players.get(0).getNickname();
    }

    /**
     * Boolean method for knowing if the match can start
     *
     * @return true if the match can start. False otherwise
     */
    public boolean canStart() {
        if (maxPlayers == players.size()) {
            for (PlayerDetails p : players) {
                if (p.getWizard() == null)
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Getter of the players in the lobby
     *
     * @return an ArrayList of PlayerDetails that contains the players in the lobby
     */
    public ArrayList<PlayerDetails> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Remove a player from the lobby
     *
     * @param nickname of player who has to be removed
     * @return false if the player is the host, true otherwise
     */
    public boolean removePlayer(String nickname) {
        if (nickname.equals(players.get(0).getNickname())) {
            return false;
        }

        for (PlayerDetails p : players) {
            if (p.getNickname().equals(nickname)) {
                players.remove(p);
                return true;
            }
        }
        return false;
    }

    /**
     * Changes the team of a specified player
     *
     * @param nickname of the player who wants to change the team
     * @param tower    that represent the team
     * @return false if the method is not implemented
     */
    public boolean changeTeam(String nickname, Tower tower) {
        return false;
    }

    /**
     * Getter for the team view
     *
     * @return the current teams
     */
    public TeamsView getTeamView() {
        return null;
    }

    /**
     * @return current available wizards
     */
    public WizardsView getWizardsView() {
        LinkedList<Wizard> wizards = new LinkedList<>(Arrays.asList(Wizard.values()));
        int counter = 0;

        for (PlayerDetails pd : players) {
            if (pd.getWizard() != null) {
                counter++;
                wizards.remove(pd.getWizard());
            }
        }

        // enum set doesn't allow empty set
        if (counter == 4)
            return new WizardsView(EnumSet.noneOf(Wizard.class));
        return new WizardsView(EnumSet.copyOf(wizards));
    }

    /**
     * Sends the available Wizards to a specified player
     *
     * @param identifier the player who has to receive available Wizards
     */
    public void notifyAvailableWizards(String identifier) {
        System.out.println("S: notifying available wizards to " + identifier);
        notifyMessageListener(identifier, new MessageEvent(this, new AvailableWizards(getWizardsView())));
    }

    /**
     * Sends the available Wizards to all players
     */
    public void notifyAvailableWizards() {
        for (PlayerDetails pd : players) {
            if (pd.getWizard() == null)
                notifyAvailableWizards(pd.getNickname());
        }
    }

    /**
     * Sends the team view to a specified player
     *
     * @param identifier the payer who has to receive the Team View
     */
    public void notifyTeams(String identifier) {
        if (getTeamView() != null)
            notifyMessageListener(identifier, new MessageEvent(this, new CurrentTeams(getTeamView())));
    }

    /**
     * Sends the team view to all players
     */
    public void notifyTeams() {
        if (getTeamView() != null)
            notifyMessageListeners(new MessageEvent(this, new CurrentTeams(getTeamView())));
    }

    /**
     * Notifies the start to the host
     */
    public void sendStart() {
        if (canStart()) {
            notifyMessageListener(getMaster(), new MessageEvent(this, new CommMessage(CommMsgType.CAN_START)));
        } else
            notifyMessageListener(getMaster(), new MessageEvent(this, new CommMessage(CommMsgType.ERROR_CANT_START)));
    }

    /**
     * Method for know if a player is inside the lobby
     *
     * @param identifier the nickname of the player
     * @return true if the player is already in the lobby, false otherwise
     */
    public boolean containsPlayer(String identifier) {
        for (PlayerDetails pd : players) {
            if (pd.getNickname().equals(identifier))
                return true;
        }
        return false;
    }
}
