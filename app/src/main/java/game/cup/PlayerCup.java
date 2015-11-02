package game.cup;

import game.player.Player;

/**
 * Describes the sungka PLAYER Cup (store)
 */
public class PlayerCup extends Cup {
    private Player _player;

    /**
     * Constructs a players cup with a reference to the player.
     * @param p owner of the cup.
     */
    public PlayerCup(Player p){
        _player = p;
    }

    /**
     * Returns reference to the player.
     * @return cup owner.
     */
    public Player getPlayer(){
        return _player;
    }

    /**
     * Add captured shells to the total
     * @param numShells the number of captured shells
     */
    public void addCapturedShells(int numShells) {
        _shells += numShells;
    }

    /**
     * Checks if current cup is not the player cup.
     * @return false if this cup is a PlayerCup.
     */
    @Override
    public boolean isNotPlayerCup() {
        return false;
    }
}