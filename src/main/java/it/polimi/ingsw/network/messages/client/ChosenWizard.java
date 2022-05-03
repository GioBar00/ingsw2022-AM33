package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.server.model.enums.Wizard;

/**
 * Message sent by the client to choose the Wizard
 */
public class ChosenWizard implements Message {

    /**
     * The chosen Wizard
     */
    private final Wizard wizard;

    /**
     * Constructor
     * @param wizard the chosen wizard
     */
    public ChosenWizard(Wizard wizard) {
        this.wizard = wizard;
    }

    /**
     * Gets the chosen wizard
     * @return the chosen wizard
     */
    public Wizard getWizard() {
        return wizard;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return wizard!= null;
    }
}
