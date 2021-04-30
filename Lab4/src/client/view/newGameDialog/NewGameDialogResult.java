package client.view.newGameDialog;

public class NewGameDialogResult {
    private int _bombCount;
    private int _fieldWidth;

    public NewGameDialogResult() {
        _bombCount = 0;
        _fieldWidth = 0;
    }

    public void setFieldWidth(int fieldWidth) {
        _fieldWidth = fieldWidth;
    }

    public void setBombCount(int bombCount) {
        _bombCount = bombCount;
    }

    public int getBombCount() {
        return _bombCount;
    }

    public int getFieldWidth() {
        return _fieldWidth;
    }
}
