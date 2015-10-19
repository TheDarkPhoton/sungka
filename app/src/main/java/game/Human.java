package game;

/**
 * Class that represents a Human Player, where the player will be the one making the moves, instead
 * of a move being generated for it.
 */
public class Human extends Player {
    /**
     * Initializes the Player Object, along with initializing the values of the Player's store and their respective
     * shell cups
     *
     * @param name the name of the player
     */
    public Human(String name) {
        super(name);
    }

}
