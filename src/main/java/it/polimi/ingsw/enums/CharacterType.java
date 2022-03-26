package it.polimi.ingsw.enums;

import it.polimi.ingsw.model.cards.*;

public enum CharacterType {
    FRIAR {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Friar();
        }
    },
    FARMER {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Farmer();
        }
    },
    HERALD {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Herald();
        }
    },
    MAILMAN {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Mailman();
        }
    },
    HERBALIST {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Herbalist();
        }
    },
    CENTAUR {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Centaur();
        }
    },
    JESTER {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Jester();
        }
    },
    KNIGHT {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Knight();
        }
    },
    HARVESTER {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Harvester();
        }
    },
    MINSTREL {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Minstrel();
        }
    },
    PRINCESS {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Princess();
        }
    },
    THIEF {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return new Thief();
        }
    };

    public abstract it.polimi.ingsw.model.cards.CharacterCard instantiate();
}
