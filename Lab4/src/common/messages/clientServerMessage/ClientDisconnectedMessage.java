package common.messages.clientServerMessage;

import common.SapperUser;

public class ClientDisconnectedMessage extends ClientServerMessage {
    public ClientDisconnectedMessage(SapperUser sender) {
        super(sender, "");
    }
}
