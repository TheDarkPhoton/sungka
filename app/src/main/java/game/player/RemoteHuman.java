package game.player;

/**
 * Created by darkphoton on 28/10/15.
 */
public class RemoteHuman extends Player {

    public RemoteHuman(String name){
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
        _board.addMove(this, index);

        _playerActionListener.onMove(this, index);
    }

    @Override
    public void moveEnd() {

        _playerActionListener.onMoveEnd(this);
    }
}
