package game.board;

import game.cup.Cup;
import game.player.Player;

/**
 * Creates a class representing a hand that has picked up some shells and will deposit them
 * in successive cups.
 */
public class HandOfShells {
    private static final String TAG = "HandOfShells";

    private Board _board;
    private Player _player;
    private int _cup_index;
    private int _shells;

    /**
     * Cup.pickUpShells() should be used to provide the value for 'cup'.
     * @param player The player to which this hand belongs.
     * @param cupIndex The index location of a cup that a player has selected
     * @param shells The number of shells in that cup
     */
    public HandOfShells(Player player, int cupIndex, int shells) {
        _player = player;
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

        if (_board.getOpponent().isPlayersCup(_board.getCup(next), true)){
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

        if (_board.getOpponent().hasValidMove())
            _board.nextPlayersMove();
        else if (_board.hasValidMoves())
            _board.getCurrentPlayer().moveStart();
    }

    /**
     * Removes a shell from the hand and places it into a Cup.
     */
    public HandOfShells dropShell() {
        --_shells;
        _board.addShell(_cup_index);

        Cup cup = _board.getCup(_cup_index);
        HandOfShells hand = null;

        // if the last shell went in a cup belonging to Player, but it isn't a store, and it
        // was empty before that, then steal shells from opponent.
        if (!_player.isPlayersCup(cup, true) && _player.isPlayersCup(cup) && cup.getCount() == 1 && _shells == 0){
            if (_board.isPlayerA(_player))
                hand = _board.pickUpShells(14 - _cup_index, true);
            else
                hand = _board.pickUpShells(6 - (_cup_index - 8), true);
        }

        if (hand != null){
            if (_board.isPlayerA(_player)) {
                if (_board.getPlayerB().hasValidMove()) {
                    _board.addStateMessage(BoardState.PLAYER_B_WAS_ROBBED);
                    _board.addStateMessage(BoardState.PLAYER_B_TURN);
                }
                else {
                    _board.addStateMessage(BoardState.PLAYER_B_WAS_ROBBED_OF_HIS_FINAL_MOVE);
                }
            }
            else if (_board.isPlayerB(_player)) {
                if (_board.getPlayerA().hasValidMove()) {
                    _board.addStateMessage(BoardState.PLAYER_A_WAS_ROBBED);
                    _board.addStateMessage(BoardState.PLAYER_A_TURN);
                }
                else {
                    _board.addStateMessage(BoardState.PLAYER_A_WAS_ROBBED_OF_HIS_FINAL_MOVE);
                }
            }
        }
        else if (_shells == 0){
            Player newPlayer = _board.nextPlayersMove(_cup_index);
            if (_board.hasValidMoves()){
                if (_player.isPlayersCup(_board.getCup(_cup_index), true)){
                    if (_board.isPlayerA(_player))
                        _board.addStateMessage(BoardState.PLAYER_A_GETS_ANOTHER_TURN);
                    else
                        _board.addStateMessage(BoardState.PLAYER_B_GETS_ANOTHER_TURN);
                }
                else if (_player == newPlayer){
                    Player opponent = _board.getOpponent();
                    if (!opponent.hasValidMove()){
                        if (_board.isPlayerA(opponent)){
                            _board.addStateMessage(BoardState.PLAYER_A_HAS_NO_VALID_MOVE);
                            _board.addStateMessage(BoardState.PLAYER_B_GETS_ANOTHER_TURN);
                        }
                        else{
                            _board.addStateMessage(BoardState.PLAYER_B_HAS_NO_VALID_MOVE);
                            _board.addStateMessage(BoardState.PLAYER_A_GETS_ANOTHER_TURN);
                        }
                    }
                    else{
                        if (_board.isPlayerA(_board.getCurrentPlayer()))
                            _board.addStateMessage(BoardState.PLAYER_A_TURN);
                        else
                            _board.addStateMessage(BoardState.PLAYER_B_TURN);
                    }
                }
                else {
                    if (_board.isPlayerA(_board.getCurrentPlayer()))
                        _board.addStateMessage(BoardState.PLAYER_A_TURN);
                    else
                        _board.addStateMessage(BoardState.PLAYER_B_TURN);
                }
            }
        }

        return hand;
    }

    /**
     * Gets the player to which this hand belongs.
     * @return player of the hand.
     */
    public Player belongsToPlayer(){
        return _player;
    }

    /**
     * Gets the index of the cup that the hand is over.
     * @return cup index.
     */
    public int currentCupIndex(){
        return _cup_index;
    }

    public int getShellCount(){
        return _shells;
    }

    public boolean isEmpty(){
        return _shells == 0;
    }
}
