package game.player;

import android.util.Pair;
import android.os.Handler;

import com.example.deathgull.sungka_project.GameActivity;

import java.util.ArrayList;
import java.util.Stack;

import game.board.Board;
import helpers.backend.Simulator;

/**
 * Class that represents the AI class, that will play against the PLAYER in the PLAYER vs AI mode.
 */
public class AI extends Player {
    private Simulator sim;

    private float _accuracy;
    private float _difficulty;

    public AI(int accuracyInPercents, int difficultyInPercents, String name) {
        super(name);

        _accuracy = (accuracyInPercents > 100 || accuracyInPercents < 0) ? 1 : (float)accuracyInPercents / 100f;
        _difficulty = (difficultyInPercents > 100 || difficultyInPercents < 50) ? 0.5f : 1f - ((float)difficultyInPercents / 100f);
    }

    /**
     * Calculates the move
     */
    @Override
    public void bindBoard(Board board) {
        super.bindBoard(board);
        sim = new Simulator(_board, _accuracy, _difficulty);
    }

    @Override
    public void moveStart() {
        super.moveStart();
        _playerActionListener.onMoveStart(this);
        _cannotPerformAnAction = false;

        ArrayList<Pair<Player, Integer>> allMoves = _board.getMoves();
        Stack<Pair<Player, Integer>> opponentMoves = new Stack<>();
        for (int i = allMoves.size() - 1; i >= 0; --i) {
            if (allMoves.get(i).first == this)
                break;
            else
                opponentMoves.push(allMoves.get(i));
        }

        while (!opponentMoves.isEmpty())
            sim.doMove(opponentMoves.pop().second);

        sim.explore(_difficulty);

        Handler h = new Handler();
        long delay = GameActivity.random.nextInt(1000) + 200;

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                move(sim.findBestMove(_accuracy));
            }
        }, delay);
    }

    @Override
    public void move(int index) {
        if (_cannotPerformAnAction)
            return;

        _cannotPerformAnAction = true;

        _playerActionListener.onMove(this, index);
    }

    @Override
    public void moveEnd() {
        super.moveEnd();
        _playerActionListener.onMoveEnd(this);
    }
}
