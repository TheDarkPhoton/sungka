package game.player;

import game.board.BoardSimulator;

/**
 * Class that represents a Human PLAYER, where the player will be the one making the moves, instead
 * of a move being generated for it.
 */
public class Human extends Player {

    /**
     * Initializes the PLAYER Object, along with initializing the values of the PLAYER's store and their respective
     * shell cups
     * @param name the name of the player
     */
    public Human(String name) {
        super(name);
    }

    @Override
    public void moveStart() {
        _playerActionListener.onMoveStart(this);
        _cannotPerformAnAction = false;
    }

    @Override
    public void move(int index) {
        if (_cannotPerformAnAction)
            return;

        _cannotPerformAnAction = true;

        //send to remote player

        _playerActionListener.onMove(this, index);
    }

    @Override
    public void moveEnd() {
        _playerActionListener.onMoveEnd(this);
    }
}
