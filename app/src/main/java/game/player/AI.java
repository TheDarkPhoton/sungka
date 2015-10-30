package game.player;

import android.os.Handler;
import android.util.Log;

import com.example.deathgull.sungka_project.GameActivity;

import java.util.Random;

import game.board.Board;

/**
 * Class that represents the AI class, that will play against the PLAYER in the PLAYER vs AI mode.
 */
public class AI extends Player {

    public AI(){
        super("AI");
    }

    /**
     * Calculates the move
     */
    @Override
    public void moveStart() {
        _playerActionListener.onMoveStart(this);

        int index = GameActivity.random.nextInt(7);
        while (_cups[index].isEmpty())
            index = GameActivity.random.nextInt(7);

        final int moveIndex = index;

        Handler h = new Handler();
        long delay = GameActivity.random.nextInt(2000) + 200;

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                move(moveIndex + 8);
            }
        }, delay);

    }

    @Override
    public void move(int index) {
        _playerActionListener.onMove(this, index);
    }

    @Override
    public void moveEnd() {
        _playerActionListener.onMoveEnd(this);
    }
}
