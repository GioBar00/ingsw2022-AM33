package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.views.WizardsView;

/**
 * Message sent by the server to the client to notify the available wizards.
 */
public class AvailableWizards implements Message {

    /**
     * enum set that contains only the wizards that haven't been chosen by any player yet
     */
    private final WizardsView wizardsView;


    /**
     * Constructor.
     *
     * @param wizardsView wizardsView of the current wizards
     */
    public AvailableWizards(WizardsView wizardsView) {
        this.wizardsView = wizardsView;
    }

    /**
     * @return the WizardsView
     */
    public WizardsView getWizardsView() {
        return wizardsView;
    }

    /**
     * @return true if the content of the message is not null
     */
    @Override
    public boolean isValid() {
        return wizardsView != null;
    }
}
