package game;

/**
 * Created by darkphoton on 19/10/15.
 */
public class ShellCup extends Cup {

    ShellCup(int shells){
        _shells = shells;
    }

    @Override
    public boolean isNotPlayerCup() {
        return false;
    }
}
