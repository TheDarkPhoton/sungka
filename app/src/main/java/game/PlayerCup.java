package game;

/**
 * Created by Dovydas Rupsys on 19/10/15.
 */

/**
 * Describes the sungka Player Cup
 */
public class PlayerCup extends Cup {
//    private Player _player;

//    public Player getPlayer(){
//        return _player;
//    }

    /**
     * Checks if current cup is not the player cup.
     * @return false if this cup is a PlayerCup.
     */
    @Override
    public boolean isNotPlayerCup() {
        return true;
    }

}