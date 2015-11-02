package game;

import com.example.deathgull.sungka_project.GameActivity;

import game.board.Board;
import game.player.AI;
import game.player.Human;
import game.player.Player;
import game.player.PlayerActionAdapter;
import game.player.PlayerActionListener;

/**
 * The class that controls the flow of the game and gives control to players.
 */
public class Game {

    private Board board;

    public Game(PlayerActionListener playerActionListener) {

        // for now, assume human players
        Player playerOne = new Human("Shell Master");
        playerOne.setPlayerActionListener(playerActionListener);
        //((Human) playerOne).setSungkaConnection(GameActivity.getUsersConnection());

        Player playerTwo = new AI();
        playerTwo.setPlayerActionListener(playerActionListener);

        board = new Board(playerOne, playerTwo);
    }

    public Board getBoard(){
        return board;
    }

    public void start() {
//        board.giveMove();
    }
}
