package common.messages.sapperMessage;

import common.SapperUser;
import common.messages.Message;

public abstract class SapperMessage extends Message {
    protected final int _row, _col;

    public SapperMessage(SapperUser sender, int row, int col) {
        super(sender, "");
        _row = row;
        _col = col;
    }

    public int getRow() {
        return _row;
    }

    public int getCol() {
        return _col;
    }

    @Override
    public String toString() {
        String selfClass = getClass() + "";
        String messageType = selfClass.substring(selfClass.lastIndexOf(".") + 1);

        return messageType + " from " + (_sender == null ? "server" : _sender.getUsername())
                + ": {row = " + _row
                + ", col = " + _col + "}";
    }
}
