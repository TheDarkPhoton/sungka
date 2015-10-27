package game;

/**
 * Class that represents the AI class, that will play against the PLAYER in the PLAYER vs AI mode.
 */
public class AI extends Player {
    private Board board;
    public AI(){
        super("AI");
    }

    /**
     * Bind the board to the AI, to allow the AI to make moves by knowing the state of the board
     * @param board the Board object which currently holds the state of the game
     */
    public void bindBoard(Board board){
        this.board = board;
    }
    /**
     * Called to carry out the AI move
     */
    public void makeMove(){

    }
}
