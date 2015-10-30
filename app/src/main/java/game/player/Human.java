package game.player;

import game.SungkaConnection;

/**
 * Class that represents a Human PLAYER, where the player will be the one making the moves, instead
 * of a move being generated for it.
 */
public class Human extends Player {
    private SungkaConnection sungkaConnection;//only this extension of player has a sungka connection
    private Boolean isOnline;
    /**
     * Initializes the PLAYER Object, along with initializing the values of the PLAYER's store and their respective
     * shell cups
     * @param name the name of the player
     */
    public Human(String name) {
        super(name);
        isOnline = false;
    }

    @Override
    public void moveStart() {
        _playerActionListener.onMoveStart(this);
    }

    @Override
    public void move(int index) {
        //send to remote player//need to decide if the player is a host or not
        if(isOnline) {
            sungkaConnection.sendMessage(Integer.toString(index));
        }
        _playerActionListener.onMove(this, index);
    }

    @Override
    public void moveEnd() {
        _playerActionListener.onMoveEnd(this);
    }

    public void setSungkaConnection(SungkaConnection sungkaConnection){
        this.sungkaConnection = sungkaConnection;
        isOnline = true;
    }
}
