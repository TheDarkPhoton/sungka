package game;

import com.example.deathgull.sungka_project.GameActivity;

import game.board.Board;
import game.connection.SungkaConnection;
import game.player.AI;
import game.player.Human;
import game.player.Player;
import game.player.PlayerActionAdapter;
import game.player.PlayerActionListener;
import game.player.RemoteHuman;


/**
 * The class that controls the flow of the game and gives control to players.
 */
public class Game {
    private Player playerOne;
    private Player playerTwo;
    private PlayerActionListener playerActionListener;
    private Board board;
    private String firstPlayer;
    private String secondPlayer;

    public Game(PlayerActionListener playerActionListener,boolean isOnlineGame, String firstName, String secondName) {
        this.playerActionListener = playerActionListener;
        this.firstPlayer = firstName;
        this.secondPlayer = secondName;

        //need to decide which one to use
        setUpAIGame();

        setUpPlayerPlayerGame();

        if(isOnlineGame){
            setUpForOnlineGame();
        }

        board = new Board(playerOne, playerTwo);

        //need to decide which player starts first in a online game
        //board.swapCurrentPlayer();
    }

    /**
     * Called when the game is a Player vs Player game on the current device. Sets up the different types of Players
     * for that game
     */
    private void setUpPlayerPlayerGame(){
        playerOne = new Human(firstPlayer);
        playerOne.setPlayerActionListener(playerActionListener);

        playerTwo = new Human(secondPlayer);
        playerTwo.setPlayerActionListener(playerActionListener);
    }

    /**
     * Called when the game is an Player vs AI game. Sets up the different types of Players for that game
     */
    private void setUpAIGame(){
        playerOne = new Human(firstPlayer);
        playerOne.setPlayerActionListener(playerActionListener);


        playerTwo = new AI(100, 100);
        playerTwo.setPlayerActionListener(playerActionListener);
    }

    /**
     * Called when the game is a online game. Sets up the different Players for that game
     */
    private void setUpForOnlineGame(){
        SungkaConnection sungkaConnection = GameActivity.getUsersConnection();


        Human human = new Human(firstPlayer);
        human.setPlayerActionListener(playerActionListener);
        human.setSungkaConnection(sungkaConnection);


        RemoteHuman remoteHuman = new RemoteHuman(secondPlayer);
        remoteHuman.setPlayerActionListener(playerActionListener);


        playerOne = human;
        playerTwo = remoteHuman;

        sungkaConnection.setSungkaProtocol(remoteHuman);
        sungkaConnection.beginListening();

    }

    public Board getBoard(){
        return board;
    }
}
