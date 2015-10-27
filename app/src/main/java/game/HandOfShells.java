package game;

import java.util.ArrayList;

/**
 * Creates a class representing a hand that has picked up some shells and will deposit them
 * in successive cups.
 */
public class HandOfShells {
    private static final String TAG = "HandOfShells";

    private int _cup_index;
    private Board _board;
    private int _shells;
    private ArrayList<String> _messages = new ArrayList<>();

    /**
     * Cup.pickUpShells() should be used to provide the value for 'cup'.
     * @param cupIndex the index location of a cup that a player has selected
     * @param shells the number of shells in that cup
     */
    public HandOfShells(int cupIndex, int shells) {
        _cup_index = cupIndex;
        _shells = shells;
    }

    /**
     * Saves the reference to the board of the hand.
     * @param board Board in question.
     */
    public void bindBoard(Board board){
        _board = board;
    }

    /**
     * Provides the index of the next Cup to drop a shell into.
     * @return the index of the next Cup to drop a shell into
     */
    public int getNextCup() {
        int next = (_cup_index + 1) % 16;

        // if next cup belongs to opponent, skip it
        if (_board.isOpponentStore(next)) {
            next = (next + 1) % 16;
        }

        return _cup_index = next;
    }

    /**
     * Sets the value of the current cup index.
     * @param index The cup over which hand was moved.
     */
    public void setNextCup(int index){
        _cup_index = index;
    }

    /**
     * Drops all shells in the current cup.
     */
    public void dropAllShells(){
        _board.getCup(_cup_index).addShells(_shells);
        _shells = 0;
    }

    /**
     * Removes a shell from the hand and places it into a Cup.
     */
    public int dropShell() {
        Cup cup = _board.getCup(_cup_index);
        int robbedIndex = -1;
        if (_board.getCurrentPlayer().isPlayersCup(cup) && cup.getCount() == 0){
            if (_cup_index < 7)
                robbedIndex = 14 - _cup_index;
            else if (_cup_index > 7 && _cup_index < 15)
                robbedIndex =  6 - (_cup_index - 8);
        }

        --_shells;
        _board.addShell(_cup_index);

        if (_shells == 0){
            Player oldPlayer = _board.getCurrentPlayer();
            Player newPlayer = _board.nextPlayersMove(_cup_index);

            if (_board.hasValidMoves()){
                if (oldPlayer == newPlayer){
                    Player opponent = _board.getOpponent();
                    if (!opponent.hasValidMove())
                        _messages.add(_board.getOpponent().getName() + " has no valid move.");
                    _messages.add(_board.getCurrentPlayer().getName() + " gets another turn.");
                }
                else {
                    _messages.add(_board.getCurrentPlayer().getName() + "'s turn.");
                }
            }
        }

        return robbedIndex;
    }

    /**
     * Adds a message to the list of messages.
     * @param msg message to be added.
     */
    public void addMessage(String msg){
        _messages.add(msg);
    }

    /**
     * Gets all of the saved messages.
     * @return saved messages.
     */
    public ArrayList<String> getMessages(){
        return _messages;
    }
}
