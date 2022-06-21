package it.polimi.ingsw.server.model.enums;

/**
 * types of wizards
 */
public enum Wizard {
    SENSEI, WITCH, MERLIN, KING;

    /**
     * This method returns the name of the wizard.
     *
     * @param input a string representing the name of the wizard.
     * @return a Wizard if the input is valid, null otherwise.
     */
    public static Wizard getWizardFromString(String input) {
        input = input.toLowerCase();
        return switch (input) {
            case "sensei" -> SENSEI;
            case "witch" -> WITCH;
            case "merlin" -> MERLIN;
            case "king" -> KING;
            default -> null;
        };
    }
}
