package game.player;

import android.os.Handler;

import com.example.deathgull.sungka_project.GameActivity;

import game.board.Board;
import helpers.backend.Node;
import helpers.backend.Simulator;
import helpers.backend.State;

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
        sim = new Simulator(_board);
    }

    @Override
    public void moveStart() {
        super.moveStart();
        _playerActionListener.onMoveStart(this);
        _cannotPerformAnAction = false;

        if (_board.getCurrentPlayer() == null){
            firstMoveMode();
        } else {
            gameMode();
        }
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

    private void firstMoveMode(){
        int index = GameActivity.random.nextInt(7);
        while (_cups[index].isEmpty())
            index = GameActivity.random.nextInt(7);

        long delay = GameActivity.random.nextInt(1000) + 200;
        final int finalIndex = index;

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                sim.doMove(finalIndex + 8);
                move(finalIndex + 8);
            }
        }, delay);
    }

    private void gameMode(){
        Node<State> node = _board.getStateNode();
        if (_board.isPlayerA(_board.getCurrentPlayer()))
            node.getElement().setPlayer(sim.getPlayerA());
        else
            node.getElement().setPlayer(sim.getPlayerB());

        sim.setCurrentNode(node);
        long delay = GameActivity.random.nextInt(1000) + 200;

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                sim.explore(_difficulty);
                move(sim.findBestMove(_accuracy));
            }
        }, delay);
    }
}
