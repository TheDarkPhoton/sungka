package game.player;

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
    }

    @Override
    public void move(int index) {
        if (_actionChosen)
            return;

        _actionChosen = true;

        //send to remote player

        _playerActionListener.onMove(this, index);
    }

    @Override
    public void moveEnd() {
        _actionChosen = false;

        _playerActionListener.onMoveEnd(this);
    }
}
