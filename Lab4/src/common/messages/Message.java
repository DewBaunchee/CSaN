package common.messages;

import common.SapperUser;

import java.io.Serializable;

public abstract class Message implements Serializable {
    protected SapperUser _sender;
    protected String _message;

    public Message(SapperUser sender, String message) {
        _sender = sender;
        _message = message;
    }

    public SapperUser getSender() {
        return _sender;
    }

    public String getMessage() {
        return _message;
    }

    @Override
    public String toString() {
        String selfClass = getClass() + "";
        String messageType = selfClass.substring(selfClass.lastIndexOf(".") + 1);

        return messageType + " from " + (_sender == null ? "server" : _sender.getUsername()) + ":\n\t" + _message;
    }
}
