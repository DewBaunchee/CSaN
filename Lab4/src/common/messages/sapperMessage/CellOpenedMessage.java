package common.messages.sapperMessage;

import common.SapperUser;

public class CellOpenedMessage extends SapperMessage {
    public CellOpenedMessage(SapperUser sender, int row, int col) {
        super(sender, row, col);
    }
}
