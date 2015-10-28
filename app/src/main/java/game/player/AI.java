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
     * Activates the delay for realMoveStart to seem more natural
     */
    @Override
    public void moveStart() {
        Handler h = new Handler();
        long delay = new Random().nextInt(2000) + 200;

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                realMoveStart();
            }
        }, delay);

    }

    /**
     * Actually start the move computation
     */
    public void realMoveStart() {
        _playerActionListener.onMoveStart(this);

        int index = GameActivity.random.nextInt(7);
        while (_cups[index].isEmpty())
            index = GameActivity.random.nextInt(7);

        move(index + 8);
    }

    @Override
    public void move(int index) {
        Handler h = new Handler();

        _playerActionListener.onMove(this, index);
    }

    @Override
    public void moveEnd() {
        _playerActionListener.onMoveEnd(this);
    }
}
