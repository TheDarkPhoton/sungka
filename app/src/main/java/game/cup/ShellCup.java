package game.cup;

/**
 * Describes the sungka Shell Cup
 */
public class ShellCup extends Cup {

    /**
     * Sets the default cup attributes.
     * @param shells The initial number of shells in the cup.
     */
    public ShellCup(int shells){
        _shells = shells;
    }

    /**
     * Checks if current cup is not the player cup.
     * @return true if this cup is not a PlayerCup.
     */
    @Override
    public boolean isNotPlayerCup() {
        return true;
    }
}
