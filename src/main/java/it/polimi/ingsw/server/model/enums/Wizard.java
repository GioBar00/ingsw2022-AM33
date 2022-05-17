package it.polimi.ingsw.server.model.enums;

/**
 * types of wizards
 */
public enum Wizard {
    SENSEI, WITCH, MERLIN, KING;

    public static Wizard getWizardFromString(String input){
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
