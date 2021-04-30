package common.messages.clientServerMessage;

import common.SapperUser;

public class TooMuchPlayersMessage extends ClientServerMessage {
    public TooMuchPlayersMessage(SapperUser sender, String message) {
        super(sender, message);
    }
}
