package common.messages.clientServerMessage;

import common.SapperUser;

public class WaitForPlayersMessage extends ClientServerMessage {
    public WaitForPlayersMessage(SapperUser sender, String message) {
        super(sender, message);
    }
}
