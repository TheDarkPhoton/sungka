package game;

import com.example.darkphoton.sungka_project.CupButton;
import com.example.darkphoton.sungka_project.GameActivity;

/**
 * The protocol that will be used to process messages sent between the SungkaServer (the host of the game) and the SungkaClient (the player who
 * joined the game).
 */
public class SungkaProtocol {
    private GameActivity gameActivity;
    public static final String PLAYERMOVE = "MOVE:";
    public static final String PLAYEREND = "END";

    /**
     *Constructor to provide the Board object that we will use to reflect the changes the other user did on their device
     * @param gameActivity the gameActivity that contains the current state of the Board as well as allowing to reflect the actions performed by the other user
     */
    public SungkaProtocol(GameActivity gameActivity){
        this.gameActivity = gameActivity;
    }

    /**
     * Depending on the message sent, a different action will be performed. If the message contains
     * the instructions to perform a move on a index, then that move will be made if it is valid.
     * @param message the information that we will use to update the board
     */
    public void updateGame(String message){
        //TODO: update the board appropriately to the message received
        if(message.contains(PLAYERMOVE)){
            int indexMove = new Integer(message.split(PLAYERMOVE)[1]);
            CupButton cupButton = gameActivity.getCupButton(indexMove);
            if(cupButton != null){
                cupButton.performClick();
            }
        }else if(message.contains(PLAYEREND)){

        }
    }
}
