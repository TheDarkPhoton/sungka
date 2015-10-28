package game.player;

/**
 * Class that represents a Human PLAYER, where the player will be the one making the moves, instead
 * of a move being generated for it.
 */
public class Human extends Player {
    /**
     * Initializes the PLAYER Object, along with initializing the values of the PLAYER's store and their respective
     * shell cups
     *
     * @param name the name of the player
     */
    public Human(String name) {
        super(name);
    }

    @Override
    public void moveStart() {
        _playerActionListener.onMoveStart();
    }

    @Override
    public void move(int index) {
        _playerActionListener.onMove(index);
    }

    @Override
    public void moveEnd() {
        _playerActionListener.onMoveEnd();
    }
}
