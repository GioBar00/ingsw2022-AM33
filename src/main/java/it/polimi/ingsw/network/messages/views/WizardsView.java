package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.server.model.enums.Wizard;

import java.io.Serializable;
import java.util.EnumSet;

/**
 * This class contains the information about the wizards.
 */
public class WizardsView implements Serializable {

    /**
     * set of available players
     */
    EnumSet<Wizard> availableWizards;

    /**
     * Constructor
     * @param availableWizards set of the available wizards
     */
    public WizardsView(EnumSet<Wizard> availableWizards) {
        this.availableWizards = availableWizards;
    }

    /**
     * @return the wizard currently not used by any other player
     */
    public EnumSet<Wizard> getAvailableWizards() {
        return availableWizards;
    }
}
