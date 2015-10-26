package game;

/**
 * The protocol that will be used to process messages sent between the SungkaServer (the host of the game) and the SungkaClient (the player who
 * joined the game).
 */
public class SungkaProtocol {
    private Board board;

    /**
     *Constructor to provide the Board object that we will use to reflect the changes the other user did on their device
     * @param board the Board object that contains the current state of the Board as well as allowing to reflect the actions performed by the other user
     */
    public SungkaProtocol(Board board){
        this.board = board;
    }

    /**
     * Used to update the state of the board for the Player on the current device. Includes the other user making a move and
     * the other user leaving the game.
     * @param message the information that we will use to update the board
     */
    public void updateGame(String message){
        //TODO: update the board appropriately to the message received
    }
}
