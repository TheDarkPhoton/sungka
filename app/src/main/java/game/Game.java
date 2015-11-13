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

    public Game(String p1Name, String p2Name, PlayerActionListener playerActionListener, GameActivity gameActivity) {
        this.playerActionListener = playerActionListener;
        this.gameActivity = gameActivity;
        // for now, assume human players
        playerOne = new Human("Shell Master");
        isOnlineGame = false;

        playerOne = new Human(p1Name);
        playerOne.setPlayerActionListener(playerActionListener);
        
        playerTwo = new Human(p2Name);
        playerTwo.setPlayerActionListener(playerActionListener);

        if(playerTwo instanceof RemoteHuman){
            setUpForOnlineGame();
        }

        board = new Board(playerOne, playerTwo);
    }

    public Game(String p1Name, String p2Name, int aiDifficulty,
                PlayerActionListener playerActionListener, GameActivity gameActivity) {

        this.playerActionListener = playerActionListener;
        this.gameActivity = gameActivity;

        playerOne = new Human(p1Name);
        playerOne.setPlayerActionListener(playerActionListener);

        playerTwo = new AI(100, aiDifficulty, p2Name);
        playerTwo.setPlayerActionListener(playerActionListener);

        board = new Board(playerOne, playerTwo);
    }

    /**
     * Called when the game is a online game. Sets up the different Players for that game
     */
    private void setUpForOnlineGame(){
        SungkaConnection sungkaConnection = GameActivity.getUsersConnection();


        Human human = new Human("Player On Device");
        human.setPlayerActionListener(playerActionListener);
        human.setSungkaConnection(sungkaConnection);


        RemoteHuman remoteHuman = new RemoteHuman(sungkaConnection.getOtherName());
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
