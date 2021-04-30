package common.messages.clientServerMessage;

import common.SapperUser;

public class SuchUserAlreadyOnServerMessage extends ClientServerMessage {
    public SuchUserAlreadyOnServerMessage(SapperUser sender, String message) {
        super(sender, message);
    }
}
