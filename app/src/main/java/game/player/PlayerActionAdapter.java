package game.player;

/**
 * Created by darkphoton on 28/10/15.
 */
public class PlayerActionAdapter implements PlayerActionListener {
    private static boolean _actionInProgress = false;

    public static void setAnimationInProgress(boolean inProgress){
        _actionInProgress = inProgress;
    }
    public static boolean isAnimationInProgress(){
        return _actionInProgress;
    }

    @Override
    public void onMoveStart(final Player player) {

    }

    @Override
    public void onMove(final Player player, final int index) {

    }

    @Override
    public void onMoveEnd(Player player) {

    }


}
