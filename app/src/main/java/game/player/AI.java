package game.player;

import com.example.deathgull.sungka_project.GameActivity;

import game.board.Board;
import game.cup.Cup;

/**
 * Class that represents the AI class, that will play against the PLAYER in the PLAYER vs AI mode.
 */
public class AI extends Player {
    public AI(){
        super("AI");
    }

    /**
     * Bind the board to the AI, to allow the AI to make moves by knowing the state of the board
     * @param board the Board object which currently holds the state of the game
     */
    public void bindBoard(Board board){
    }

    @Override
    public void moveStart() {
        int index = GameActivity.random.nextInt(7);
        while (_cups[index].isEmpty())
            index = GameActivity.random.nextInt(7);

        move(index + 8);

        _playerActionListener.onMoveStart();
    }

    @Override
    public void move(int index) {
        _playerActionListener.onMove(index);
    }

    @Override
    public void moveEnd() {
        _playerActionListener.onMoveEnd();
    }
}
