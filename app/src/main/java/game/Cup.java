package game;

/**
 * Created by darkphoton on 19/10/15.
 */
public abstract class Cup {
    protected int _shells;

    Cup(){
        _shells = 0;
    }

    public abstract boolean isNotPlayerCup();

    public void addShell(){
        ++_shells;
    }
    public int pickUpShells(){
        int shells = _shells;
        _shells = 0;
        return shells;
    }

    public boolean isEmpty(){
        return _shells == 0;
    }
}
