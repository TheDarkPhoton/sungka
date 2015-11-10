package game.player;

import android.util.Log;

import game.connection.SungkaConnection;
import game.connection.SungkaProtocol;

/**
 * Class that represents a Human PLAYER, where the player will be the one making the moves, instead
 * of a move being generated for it.
 */
public class Human extends Player {

    private SungkaConnection sungkaConnection;//only this extension of player has a sungka connection
    private Boolean isOnline;
    //private MoveInfo currentMove;
    /**
     * Initializes the PLAYER Object, along with initializing the values of the PLAYER's store and their respective
     * shell cups
     * @param name the name of the player
     */
    public Human(String name) {
        super(name);
        isOnline = false;
        //currentMove = null;
    }

    @Override
    public void moveStart() {
        super.moveStart();
        Log.v("Human", "Starting current move for " + getName());
        Log.v("Human","Start at time "+System.currentTimeMillis());
        _playerActionListener.onMoveStart(this);
        _cannotPerformAnAction = false;
    }

    @Override
    public void move(int index) {
        if (_cannotPerformAnAction)
            return;

        _cannotPerformAnAction = true;

        //send to remote player//need to decide if the player is a host or not
        if(isOnline) {
            Log.v("Human",SungkaProtocol.PLAYERMOVE+Integer.toString(index));
            sungkaConnection.sendMessage(SungkaProtocol.PLAYERMOVE+Integer.toString(index));
        }
        _playerActionListener.onMove(this, index);
    }

    @Override
    public void moveEnd() {
        super.moveEnd();
        Log.v("Human", "Ending move started for " + getName());
        Log.v("Human","Ends time: "+System.currentTimeMillis());
//        Log.v("Human","Move time: "+_currentMove.getDurationOfMoveMillis());
        _playerActionListener.onMoveEnd(this);
    }

    /**
     * Set the connection this Player will use in an online game to communicate to another Player when they make a move
     * @param sungkaConnection the connection (either SungkaClient or SungkaServer) to communicate to another Player
     */
    public void setSungkaConnection(SungkaConnection sungkaConnection){
        this.sungkaConnection = sungkaConnection;
        isOnline = true;
    }


}
