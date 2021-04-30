package common.messages.clientServerMessage;

import common.SapperUser;

public class WaitingForNewGameMessage extends ClientServerMessage {
    public WaitingForNewGameMessage(SapperUser sender, String message) {
        super(sender, message);
    }
}
