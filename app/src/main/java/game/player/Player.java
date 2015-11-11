package game.player;

import android.util.Log;

import java.util.ArrayList;

import game.board.Board;
import game.cup.Cup;

/**
 * PLAYER Class which represents a PLAYER. This class will be inherited to form a
 * Human player and a AI player.
 */
public abstract class Player {
    private String _name;
    protected Board _board;
    protected int score;
    protected Cup _store;
    protected Cup[] _cups;
    protected int maxConsecutiveMoves;
    protected int moves;

    private Side _side;

    protected ArrayList<MoveInfo> _moveInfos;//arraylist to store the users moves in a game
    protected MoveInfo _currentMove;
    protected PlayerActionListener _playerActionListener = new PlayerActionAdapter();

    protected boolean _cannotPerformAnAction = true;

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
        _currentMove= null;
        _moveInfos = new ArrayList<>();
        moves = 0;
        maxConsecutiveMoves = 0;
    }

    /**
     * Saves the player cup reference to this player.
     * @param cup of the player.
     */
    public void bindStore(Cup cup){
        _store = cup;
    }

    /**
     * Assigns the board to the player.
     * @param board Board to be assigned.
     */
    public void bindBoard(Board board){
        _board = board;
    }

    /**
     * Assigns a player action listener to the player.
     * @param listener Action listener to be assigned.
     */
    public void setPlayerActionListener(PlayerActionListener listener){
        _playerActionListener = listener;
    }

    public void setPlayerCannotPerformAction(boolean yes){
        _cannotPerformAnAction = yes;
    }

    public void moveStart(){
       // Log.v("Player", "Starting current move for "+getName());
        _currentMove = new MoveInfo(System.currentTimeMillis(),getName());//starting the move info object
    }

    public abstract void move(int index);

    public void moveEnd(){
       // Log.v("Player","Checking if its equal to null");
        if(_currentMove != null) {
           // Log.v("Player","Not equal to null");
          //  Log.v("Player", "Ending move started for " + getName());
            _currentMove.endMove(System.currentTimeMillis());
            _currentMove.calculateMoveDuration();//calculate the duration of the move and store it in the object
            Log.v("Player","Move time: "+_currentMove.getDurationOfMoveMillis());
            Log.v("Player", "Ending move completed for " + getName());
            if (_moveInfos.size() == 0) {//this is the first move
                _currentMove.setNumOfShellsCollected(_store.getCount());
            } else {//the amount of shells collected in this move, is the amount of shells in the store now minus the amount of shells in the store in the previous turn
                _currentMove.setNumOfShellsCollected(_store.getCount() - _moveInfos.get(_moveInfos.size() - 1).getNumOfShellsCollected());
            }
            _moveInfos.add(_currentMove);//want to maybe get the points the user collected in that move
        }else{
            Log.v("Player","Equal to null");
        }
    }



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
    public String getName(){
        return _name;
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
            if(otherPlayer.getName().equals(_name)){
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

    /**
     * Get the side the Player is on
     * @return the side the Player is on
     */
    public Side getSide() {
        return _side;
    }

    public void setSide(Side _side) {
        this._side = _side;
    }

    /**
     * Get the maximum number of Shells this Player has collected in this Game
     * @return the maximum number of Shells the Player has collected in this Game
     */
    public int getMaxNumberShellsCollected(){
        int maxNumberShells = 0;
        for(MoveInfo moveInfo : _moveInfos){
            if(maxNumberShells < moveInfo.getNumOfShellsCollected()){
                maxNumberShells = moveInfo.getNumOfShellsCollected();
            }
        }
        return maxNumberShells;
    }

    /**
     * Get the average turn (or move) time for this Player in this Game
     * @return the average turn time for this Player in the this Game
     */
    public double getAverageTurnTime(){
        double averageTurnTime = 0;
        for(MoveInfo moveInfo:_moveInfos){
            averageTurnTime+=moveInfo.getDurationOfMoveMillis();

        }
        averageTurnTime /= _moveInfos.size();
        return  averageTurnTime;
    }

    /**
     * Increase the amount of moves this Player has gotten in a row, by one
     */
    public void addMove(){
        moves++;
        if(moves > maxConsecutiveMoves){//new maximum value for the Player in this game
            maxConsecutiveMoves = moves;
        }
    }

    /**
     * When the user has not gotten another move in a row, reset there consecutive move count to 0
     */
    public void resetMove(){
        moves = 0;
    }

    /**
     * Get the maximum number of consecutive moves of the Player
     * @return the maximum number of consecutive moves of the Player
     */
    public int getMaxConsecutiveMoves(){
        return maxConsecutiveMoves;
    }
    
}




