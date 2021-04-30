package common.messages.clientServerMessage;

import common.SapperUser;

public class ServerAcceptMessage extends ClientServerMessage {
    public ServerAcceptMessage(SapperUser sender, String message) {
        super(sender, message);
    }
}
