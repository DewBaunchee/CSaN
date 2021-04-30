package common.messages.clientServerMessage;

import common.SapperUser;

public class ClientConnectedMessage extends ClientServerMessage {
    public ClientConnectedMessage(SapperUser user) {
        super(user, "");
    }
}
