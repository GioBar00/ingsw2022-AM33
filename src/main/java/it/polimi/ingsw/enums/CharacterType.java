package it.polimi.ingsw.enums;

public enum CharacterType {
    FRIAR {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    },
    FARMER {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    },
    HERALD {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    },
    MAILMAN {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    },
    HERBALIST {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    },
    CENTAUR {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    },
    JESTER {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    },
    KNIGHT {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    },
    HARVESTER {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    },
    MINSTREL {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    },
    PRINCESS {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    },
    THIEF {
        @Override
        public it.polimi.ingsw.model.cards.CharacterCard instantiate() {
            return null;
        }
    };

    public abstract it.polimi.ingsw.model.cards.CharacterCard instantiate();
}
