package game;

/**
 * The class that controls the flow of the game and gives control to players.
 */
public class Game {

    private Board board;

    public Game() {

        // for now, assume human players
        Player playerOne = new Human("Shell Master");
        Player playerTwo = new Human("Angry Gull");

        board = new Board(playerOne, playerTwo);
    }

    public Board getBoard(){
        return board;
    }

    public void start() {
//        board.giveMove();
    }
}
