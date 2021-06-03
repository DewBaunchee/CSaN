package common.messages.clientServerMessage;

import common.SapperUser;

public class ServerShutdownMessage extends ClientServerMessage {
    public ServerShutdownMessage(SapperUser sender, String message) {
        super(sender, message);
    }
}
