package it.polimi.ingsw.server.model.enums;

import it.polimi.ingsw.server.model.cards.*;

/**
 * enumeration of CharacterCard types; each type has related instance
 */
public enum CharacterType {
    /**
     * Friar characterType
     */
    FRIAR {
        @Override
        public CharacterCard instantiate() {
            return new Friar();
        }
    },
    /**
     * Farmer characterType
     */
    FARMER {
        @Override
        public CharacterCard instantiate() {
            return new Farmer();
        }
    },
    /**
     * Herald characterType
     */
    HERALD {
        @Override
        public CharacterCard instantiate() {
            return new Herald();
        }
    },
    /**
     * Mailman characterType
     */
    MAILMAN {
        @Override
        public CharacterCard instantiate() {
            return new Mailman();
        }
    },
    /**
     * Herbalist characterType
     */
    HERBALIST {
        @Override
        public CharacterCard instantiate() {
            return new Herbalist();
        }
    },
    /**
     * Centaur characterType
     */
    CENTAUR {
        @Override
        public CharacterCard instantiate() {
            return new Centaur();
        }
    },
    /**
     * Jester characterType
     */
    JESTER {
        @Override
        public CharacterCard instantiate() {
            return new Jester();
        }
    },
    /**
     * Knight characterType
     */
    KNIGHT {
        @Override
        public CharacterCard instantiate() {
            return new Knight();
        }
    },
    /**
     * Harvester characterType
     */
    HARVESTER {
        @Override
        public CharacterCard instantiate() {
            return new Harvester();
        }
    },
    /**
     * Minstrel characterType
     */
    MINSTREL {
        @Override
        public CharacterCard instantiate() {
            return new Minstrel();
        }
    },
    /**
     * Princess characterType
     */
    PRINCESS {
        @Override
        public CharacterCard instantiate() {
            return new Princess();
        }
    },
    /**
     * Thief characterType
     */
    THIEF {
        @Override
        public CharacterCard instantiate() {
            return new Thief();
        }
    };

    /**
     * instantiates a CharacterCard of the current type
     *
     * @return the instantiated CharacterCard
     */
    public abstract CharacterCard instantiate();
}
