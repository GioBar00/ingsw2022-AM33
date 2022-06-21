package it.polimi.ingsw.server.model.enums;

import it.polimi.ingsw.server.model.cards.*;

/**
 * enumeration of CharacterCard types; each type has related instance
 */
public enum CharacterType {
    FRIAR {
        @Override
        public CharacterCard instantiate() {
            return new Friar();
        }
    },
    FARMER {
        @Override
        public CharacterCard instantiate() {
            return new Farmer();
        }
    },
    HERALD {
        @Override
        public CharacterCard instantiate() {
            return new Herald();
        }
    },
    MAILMAN {
        @Override
        public CharacterCard instantiate() {
            return new Mailman();
        }
    },
    HERBALIST {
        @Override
        public CharacterCard instantiate() {
            return new Herbalist();
        }
    },
    CENTAUR {
        @Override
        public CharacterCard instantiate() {
            return new Centaur();
        }
    },
    JESTER {
        @Override
        public CharacterCard instantiate() {
            return new Jester();
        }
    },
    KNIGHT {
        @Override
        public CharacterCard instantiate() {
            return new Knight();
        }
    },
    HARVESTER {
        @Override
        public CharacterCard instantiate() {
            return new Harvester();
        }
    },
    MINSTREL {
        @Override
        public CharacterCard instantiate() {
            return new Minstrel();
        }
    },
    PRINCESS {
        @Override
        public CharacterCard instantiate() {
            return new Princess();
        }
    },
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
