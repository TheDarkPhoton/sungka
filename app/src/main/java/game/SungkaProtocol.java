package game;

import android.util.Log;

import game.player.RemoteHuman;

/**
 * The protocol that will be used to process messages sent between the SungkaServer (the host of the game) and the SungkaClient (the player who
 * joined the game).
 */
public class SungkaProtocol {
    private RemoteHuman remoteHuman;
    public static final String PLAYERMOVE = "MOVE:";
    public static final String PLAYEREND = "END";

    /**
     *Constructor to provide the Board object that we will use to reflect the changes the other user did on their device
     * @param remoteHuman the other Player in the game
     */
    public SungkaProtocol(RemoteHuman remoteHuman){
        this.remoteHuman = remoteHuman;
    }

    /**
     * Depending on the message sent, a different action will be performed. If the message contains
     * the instructions to perform a move on a index, then that move will be made if it is valid.
     * @param message the information that we will use to update the board
     */
    public void updateGame(String message){
        //TODO: update the board appropriately to the message received
        if(message.contains(PLAYERMOVE)){
            Log.v("SungkaProtocol",message);
            int indexMove = new Integer(message.split(PLAYERMOVE)[1]);
            remoteHuman.move(indexMove);//perform the other players move on this device
        }else if(message.equals(PLAYEREND)){//end the game

        }
    }
}
