package game.player;

import com.example.deathgull.sungka_project.GameActivity;

/**
 * Class that represents the AI class, that will play against the PLAYER in the PLAYER vs AI mode.
 */
public class AI extends Player {

    public AI(){
        super("AI");
    }

    @Override
    public void moveStart() {
        _playerActionListener.onMoveStart(this);
        _cannotPerformAnAction = false;

        int index = GameActivity.random.nextInt(7);
        while (_cups[index].isEmpty())
            index = GameActivity.random.nextInt(7);

        move(index + 8);
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
