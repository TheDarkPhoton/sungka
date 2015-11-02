package game.player;

import android.util.Pair;
import android.os.Handler;

import com.example.deathgull.sungka_project.GameActivity;

import java.util.ArrayList;
import java.util.Stack;

import game.board.Board;
import game.board.BoardSimulator;

/**
 * Class that represents the AI class, that will play against the PLAYER in the PLAYER vs AI mode.
 */
public class AI extends Player {
    private BoardSimulator sim;

    public AI(){
        super("AI");
    }

    /**
     * Calculates the move
     */
    @Override
    public void bindBoard(Board board) {
        super.bindBoard(board);
        sim = new BoardSimulator(_board);
    }

    @Override
    public void moveStart() {
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

        sim.explore();

        Handler h = new Handler();
        long delay = GameActivity.random.nextInt(2000) + 200;

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                move(sim.findBestMove());
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
        _playerActionListener.onMoveEnd(this);
    }
}
