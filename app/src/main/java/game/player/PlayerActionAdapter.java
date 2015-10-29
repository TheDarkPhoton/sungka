package game.player;

/**
 * Created by darkphoton on 28/10/15.
 */
public class PlayerActionAdapter implements PlayerActionListener {
    private boolean _actionInProgress = false;

    public void setAnimationInProgress(boolean inProgress){
        _actionInProgress = inProgress;
    }
    public boolean isAnimationInProgress(){
        return _actionInProgress;
    }

    @Override
    public void onMoveStart(Player player) {

    }

    @Override
    public boolean onMove(Player player, int index) {
        return false;
    }

    @Override
    public void onMoveEnd(Player player) {

    }


}
