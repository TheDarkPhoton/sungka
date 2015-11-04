package game.player;

import android.util.Log;

import game.SungkaConnection;
import game.SungkaProtocol;

/**
 * Class that represents a Human PLAYER, where the player will be the one making the moves, instead
 * of a move being generated for it.
 */
public class Human extends Player {
    private SungkaConnection sungkaConnection;//only this extension of player has a sungka connection
    private Boolean isOnline;
    private MoveInfo currentMove;
    /**
     * Initializes the PLAYER Object, along with initializing the values of the PLAYER's store and their respective
     * shell cups
     * @param name the name of the player
     */
    public Human(String name) {
        super(name);
        isOnline = false;
        currentMove = null;
    }

    @Override
    public void moveStart() {
        _playerActionListener.onMoveStart(this);
        currentMove = new MoveInfo(System.currentTimeMillis(),get_name());//starting the moveinfo object
    }

    @Override
    public void move(int index) {
        //send to remote player//need to decide if the player is a host or not
        if(isOnline) {
            Log.v("Human",SungkaProtocol.PLAYERMOVE+Integer.toString(index));
            sungkaConnection.sendMessage(SungkaProtocol.PLAYERMOVE+Integer.toString(index));
        }
        _playerActionListener.onMove(this, index);
    }

    @Override
    public void moveEnd() {
        _playerActionListener.onMoveEnd(this);
        currentMove.endMove(System.currentTimeMillis());
        if(_moveInfos.size() == 0){//this is the first move
            currentMove.setNumOfShellsCollected(_store.getCount());
        }else{//the amount of shells collected in this move, is the amount of shells in the store now minus the amount of shells in the store in the previous turn
            currentMove.setNumOfShellsCollected(_store.getCount()-_moveInfos.get(_moveInfos.size()-1).getNumOfShellsCollected());
        }
        _moveInfos.add(currentMove);//want to maybe get the points the user collected in that move
    }

    public void setSungkaConnection(SungkaConnection sungkaConnection){
        this.sungkaConnection = sungkaConnection;
        isOnline = true;
    }
}
