package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;

/**
 * Message sent by the client to login.
 */
public class Login implements Message {
    /**
     * The nickname.
     */
    private final String nickname;

    /**
     * Constructor.
     * @param nickname the nickname
     */
    public Login(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Getter.
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return nickname != null;
    }
}
