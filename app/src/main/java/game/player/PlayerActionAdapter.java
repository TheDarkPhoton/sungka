package game.player;

/**
 * Created by darkphoton on 28/10/15.
 */
public class PlayerActionAdapter implements PlayerActionListener {
    private boolean _actionInProgress = false;

    public void setActionInProgress(boolean inProgress){
        _actionInProgress = inProgress;
    }
    public boolean isActionInProgress(){
        return _actionInProgress;
    }

    @Override
    public void onMoveStart() {

    }

    @Override
    public void onMove(int cupIndex) {

    }

    @Override
    public void onMoveEnd() {

    }


}
