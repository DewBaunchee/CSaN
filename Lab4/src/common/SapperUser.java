package common;

import java.io.Serializable;

public class SapperUser implements Serializable {
    private final String _username;
    private int _winCount;
    private int _lostCount;

    public SapperUser(String username) {
        _username = username;
        _winCount = _lostCount = 0;
    }

    public String getUsername() {
        return _username;
    }

    public void addWin() {
        _winCount++;
        System.out.println("Adding win count to " + this);
    }

    public void addLose() {
        _lostCount++;
    }

    public int getWinCount() {
        return _winCount;
    }

    public int getLostCount() {
        return _lostCount;
    }

    public double getWinRate() {
        return _lostCount == 0 ? _winCount : (double) _winCount / _lostCount;
    }

    @Override
    public boolean equals(Object comparable) {
        if(comparable == null) {
            return false;
        }
        if(comparable.getClass() != this.getClass()) {
            return false;
        }

        return _username.equals(((SapperUser)comparable)._username);
    }

    @Override
    public String toString() {
        return "User: " + _username +
                "\nWin: " + _winCount +
                "\nLost: " + _lostCount +
                "\nW/L: " + getWinRate();
    }
}
