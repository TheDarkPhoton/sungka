package game.player;

import java.util.ArrayList;

import game.cup.Cup;

/**
 * PLAYER Class which represents a PLAYER. This class will be inherited to form a
 * Human player and a AI player.
 */
public abstract class Player {
    private String _name;
    protected int moves;
    protected int score;
    protected Cup _store;
    protected Cup[] _cups;

    protected ArrayList<MoveInfo> _moveInfos;                                                //arraylist to store the users moves in a game
    protected PlayerActionListener _playerActionListener = new PlayerActionAdapter();

    /**
     * Initializes the PLAYER Object, along with initializing the values of the PLAYER's store and their respective
     * shell cups
     * Initializes the Player Object, along with initializing the values of the Player's store and their respective
     * shell cups. Also initializes the list that will hold the Player's Moves the game.
     * @param name the name of the player
     */
    public Player(String name){
        _name = name;
        _cups = new Cup[7];
        _store = null;
        _moveInfos = new ArrayList<>();
    }

    /**
     * Saves the player cup reference to this player.
     * @param cup of the player.
     */
    public void bindStore(Cup cup){
        _store = cup;
    }

    public void setPlayerActionListener(PlayerActionListener listener){
        _playerActionListener = listener;
    }

    public abstract void moveStart();
    public abstract void move(int index);
    public abstract void moveEnd();

    /**
     * Determines if the cup provided belongs to this player.
     * @param cup Cup in question.
     * @return true if cup belongs to the player.
     */
    public boolean isPlayersCup(Cup cup){
        return isPlayersCup(cup, false);
    }

    /**
     * Determines if the cup provided belongs to this player.
     * @param cup Cup in question.
     * @param just_store Checks if the cup is the players store.
     * @return true if cup belongs to the player.
     */
    public boolean isPlayersCup(Cup cup, boolean just_store){
        if (_store == cup)
            return true;

        if (just_store)
            return false;

        for (int i = 0; i < _cups.length; i++) {
            if (_cups[i] == cup)
                return true;
        }

        return false;
    }

    /**
     * Saves a players shell cup in a local array.
     * @param cup Shell cup of the player.
     * @param index array index that corresponds with the cup.
     */
    public void bindShellCup(Cup cup, int index){
        _cups[index] = cup;
    }

    /**
     *
     * @return the current amount of shells a PLAYER has in their store
     */
    public int getScore(){
        return _store.getCount();
    }

    /**
     *
     * @return the name of the PLAYER
     */
    public String get_name(){
        return _name;
    }

    /**
     * Increases the amount of moves a user has
     */
    public void addMove(){
        moves++;
    }

    /**
     * Checks if player has any valid moves.
     * @return true if there are moves that this player can make.
     */
    public boolean hasValidMove(){
        for (int i = 0; i < _cups.length; i++) {
            if (_cups[i].getCount() > 0)
                return true;
        }

        return false;
    }

    /**
     * Method used to be able to identify between PLAYER objects
     * @param object the Object which we are checking if it is equal to this PLAYER Object
     * @return true if the object is equal to this PLAYER Object, false otherwise
     */
    public boolean equals(Object object){
        try{
            Player otherPlayer = (Player) object;
            if(otherPlayer.get_name().equals(_name)){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    /**
     * Add the information of a move to the Player
     * @param moveInfo the information of the move that the player just finished
     */
    public void addMoveInfo(MoveInfo moveInfo){
        _moveInfos.add(moveInfo);
    }

    /**
     * Get the list of all the Moves the Player has made up to this point
     * @return an ArrayList that contains the Player's moves
     */
    public ArrayList<MoveInfo> get_moveInfos(){
        return _moveInfos;
    }
}
