package game;

/**
 * Describes the sungka Cup
 */
public abstract class Cup {
    protected int _shells;                                  //the number of shells in this cup

    /**
     * Defines default values for the Cup object.
     */
    Cup(){
        _shells = 0;
    }

    /**
     * Checks if current cup is not the player cup.
     * @return false if this cup is a PlayerCup.
     */
    public abstract boolean isNotPlayerCup();

    /**
     * Increments the shell count.
     */
    public void addShell(){
        ++_shells;
    }

    public void addShells(int count){
        _shells += count;
    }

    /**
     * Removes all shells from the shell count and returns it.
     * @return shell count.
     */
    public int pickUpShells(){
        int shells = _shells;
        _shells = 0;
        return shells;
    }

    /**
     * Gets the count of shells.
     * @return shell count.
     */
    public int getCount(){
        return _shells;
    }

    /**
     * Checks if cup is empty.
     * @return true if this cup is empty.
     */
    public boolean isEmpty(){
        return _shells == 0;
    }
}
