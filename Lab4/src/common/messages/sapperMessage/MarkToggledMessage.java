package common.messages.sapperMessage;

import common.SapperUser;

public class MarkToggledMessage extends SapperMessage {
    public MarkToggledMessage(SapperUser sender, int row, int col) {
        super(sender, row, col);
    }
}
