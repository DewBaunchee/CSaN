package common.messages.sapperMessage;

import common.SapperUser;

public class LoseMessage extends SapperMessage {
    public LoseMessage(SapperUser sender, int row, int col) {
        super(sender, row, col);
    }
}
