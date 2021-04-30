package common.messages.sapperMessage;

import common.SapperUser;
import sapper.model.SapperModel;

public class SapperModelMessage extends SapperMessage {

    private SapperModel _model;

    public SapperModelMessage(SapperUser sender, SapperModel model) {
        super(sender, 0, 0);
        _model = model;
    }

    public SapperModel getModel() {
        return _model;
    }

    @Override
    public String toString() {
        String selfClass = getClass() + "";
        String messageType = selfClass.substring(selfClass.lastIndexOf(".") + 1);

        return messageType + " from " + (_sender == null ? "server" : _sender.getUsername()) + ": \n\t" + _model.toString();
    }
}
