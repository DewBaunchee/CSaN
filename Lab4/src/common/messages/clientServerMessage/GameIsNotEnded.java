package common.messages.clientServerMessage;

import common.SapperUser;

public class GameIsNotEnded extends ClientServerMessage{
    public GameIsNotEnded(SapperUser sender, String message) {
        super(sender, message);
    }
}
