package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;

public class Login extends Message {
    private final String nickname;

    public Login(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean isValid() {
        return nickname != null;
    }
}
