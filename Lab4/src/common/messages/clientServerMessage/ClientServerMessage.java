package common.messages.clientServerMessage;

import common.SapperUser;
import common.messages.Message;

public abstract class ClientServerMessage extends Message {
    public ClientServerMessage(SapperUser sender, String message) {
        super(sender, message);
    }
}
