package common.messages.sapperMessage;

import sapper.view.GameZone;

public class GameZoneMessage extends SapperMessage {
    private final GameZone _gameZone;

    public GameZoneMessage(GameZone gameZone) {
        super(null, 0, 0);
        _gameZone = gameZone;
    }

    public GameZone getGameZone() {
        return _gameZone;
    }

    @Override
    public String toString() {
        String selfClass = getClass() + "";
        String messageType = selfClass.substring(selfClass.lastIndexOf(".") + 1);

        return messageType + " from " + (_sender == null ? "server" : _sender.getUsername()) + ": \n\t" + _gameZone.toString();
    }
}
