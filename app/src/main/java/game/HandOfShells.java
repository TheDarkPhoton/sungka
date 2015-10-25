package game;

import android.util.Log;

/**
 * Creates a class representing a hand that has picked up some shells and will deposit them
 * in successive cups.
 */
public class HandOfShells {

    private static final String TAG = "HandOfShells";

    private int _cup_index;
    private Board _board;
    private int _shells;

    /**
     * Cup.pickUpShells() should be used to provide the value for 'cup'.
     *
     * @param cupIndex the index location of a cup that a player has selected
     * @param shells the number of shells in that cup
     */
    public HandOfShells(int cupIndex, int shells) {
        _cup_index = cupIndex;
        _shells = shells;
    }

    public void bindBoard(Board board){
        _board = board;
    }

    /**
     * Provides the index of the next Cup to drop a shell into.
     * @return the index of the next Cup to drop a shell into
     */
    public int nextCup() {
        int next = (_cup_index + 1) % 16;

        // if next cup belongs to opponent, skip it
        if (_board.isOpponentStore(next)) {
            next = (next + 1) % 16;
        }

        return _cup_index = next;
    }

    public boolean dropShellValid() {
        return _board.isOpponentStore(_cup_index);
    }

    /**
     * Removes a shell from the hand and places it into a Cup.
     */
    public void dropShell() {
        Log.i(TAG, "Dropping a shell from hand");
        --_shells;
        _board.addShell(_cup_index);

        if (_shells == 0 && !_board.isCurrentPlayersStore(_cup_index))
            _board.nextPlayersMove();
    }

    public int shellCount(){
        return _shells;
    }

    /**
     * @return true if there are no more shells left
     */
    public boolean isNotEmpty() {
        return _shells > 0;
    }
}
