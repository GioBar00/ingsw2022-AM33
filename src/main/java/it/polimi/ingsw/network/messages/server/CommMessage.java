package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.CommMsgType;

public class CommMessage extends Message {
    String message;

    public CommMessage(CommMsgType type) {
        this.message = type.getMessage();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean isValid() {
        return message != null;
    }
}
