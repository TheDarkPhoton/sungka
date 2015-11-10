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
    private GameActivity gameActivity;
    private Board board;
    private Boolean isOnlineGame;

    public Game(PlayerActionListener playerActionListener,GameActivity gameActivity) {
        this.playerActionListener = playerActionListener;
        this.gameActivity = gameActivity;
        isOnlineGame = false;
        // for now, assume human players
        playerOne = new Human("Shell Master");
        playerOne.setPlayerActionListener(playerActionListener);
        
       // playerTwo = new Human("Player B");
        playerTwo = new AI(100, 50);
        playerTwo.setPlayerActionListener(playerActionListener);



        if(isOnlineGame){
            setUpForOnlineGame();
        }

        /*//for the remote player
        RemoteHuman remoteHuman = new RemoteHuman("Remote Human");
        remoteHuman.setPlayerActionListener(playerActionListener);
        GameActivity.getUsersConnection().setActivity(gameActivity);//need this so that the moves can be carried on the ui thread
        GameActivity.getUsersConnection().setSungkaProtocol(remoteHuman);

        GameActivity.getUsersConnection().beginListening();*/

        board = new Board(playerOne, playerTwo);

        //need to decide which player starts first in a online game
       // board.swapCurrentPlayer();
    }

    /**
     * Called when the game is a online game. Sets up the different Players for that game
     */
    private void setUpForOnlineGame(){
        SungkaConnection sungkaConnection = GameActivity.getUsersConnection();


        Human human = new Human("Player On Device");
        human.setPlayerActionListener(playerActionListener);
        human.setSungkaConnection(sungkaConnection);

        RemoteHuman remoteHuman = new RemoteHuman("Remote Human");
        remoteHuman.setPlayerActionListener(playerActionListener);

        playerOne = human;
        playerTwo = remoteHuman;

        sungkaConnection.setActivity(gameActivity);
        sungkaConnection.setSungkaProtocol(remoteHuman);
        sungkaConnection.beginListening();

    }

    public Board getBoard(){
        return board;
    }
}
