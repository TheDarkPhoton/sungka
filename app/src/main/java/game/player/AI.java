package game.player;

import android.os.Handler;

import com.example.deathgull.sungka_project.GameActivity;

import game.board.Board;

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

        int index = GameActivity.random.nextInt(7);
        while (_cups[index].isEmpty())
            index = GameActivity.random.nextInt(7);

        move(index + 8);
    }

    @Override
    public void move(int index) {
        if (_actionChosen)
            return;

        _actionChosen = true;

        _playerActionListener.onMove(this, index);
    }

    @Override
    public void moveEnd() {
        _actionChosen = false;

        _playerActionListener.onMoveEnd(this);
    }
}
