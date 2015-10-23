package game;

/**
 * Creates a class representing a hand that has picked up some shells and will deposit them
 * in successive cups.
 */
public class HandOfShells {

    private int _cup_index;
    private Board _board;
    private int _shells;

    /**
     * Cup.pickUpShells() should be used to provide the value for 'cup'.
     *
     * @param cup the index location of a cup that a player has selected
     * @param shells the number of shells in that cup
     */
    public HandOfShells(int cup, int shells) {
        _cup_index = cup;
        _shells = shells;
    }

    /**
     * Saves the reference to the board of the player.
     * @param board of the players.
     */
    public void bindBoard(Board board){
        _board = board;
    }

    /**
     * Provides the index of the next Cup to drop a shell into.
     * @return the index of the next Cup to drop a shell into
     */
    private int next() {
        int next = (_cup_index + 1) % 16;

        // if next cup belongs to opponent, skip it
        if (_board.isOpponentStore(next)) {
            next = (next + 1) % 16;
        }

        return next;
    }

    /**
     * Reduces the number of shells in the hand by one.
     */
    private void dropShell() {
        _shells--;
    }

    /**
     * @return true if there are no more shells left
     */
    private boolean isNotEmpty() {
        return _shells == 0;
    }
}
