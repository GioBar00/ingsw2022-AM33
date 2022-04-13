package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.CommMsgType;

public class CommMessage extends Message {
    CommMsgType type;

    public CommMessage(CommMsgType type) {
        this.type = type;
    }

    public CommMsgType getType() {
        return type;
    }

    @Override
    public boolean isValid() {
        return type != null;
    }
}
