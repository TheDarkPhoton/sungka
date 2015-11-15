package game.board;

import android.util.Pair;

import java.util.ArrayList;


import game.cup.Cup;
import game.cup.PlayerCup;
import game.cup.ShellCup;
import game.player.Player;
import helpers.backend.Node;
import helpers.backend.State;

/**
 * An object that describes the state of the current game board
 */
public class Board {
    protected ArrayList<BoardState> _state_messages = new ArrayList<>();
    protected ArrayList<Pair<Player, Integer>> _moves = new ArrayList<>();

    protected Cup[] _cups;                                    // an array of board cups
    protected Player _currentPlayer;                          // the player whose turn it is
    protected Player _playerOne;
    protected Player _playerTwo;

    protected boolean _validMoveExists = true;

    /**
     * Constructs board with default attributes.
     * @param a Player one
     * @param b Player two
     */
    public Board(Player a, Player b){
        _cups = new Cup[16];
        _playerOne = a;
        _playerTwo = b;
        // temporary player assignment
        _currentPlayer = null;

        //define player a cups
        for (int i = 0; i < 7; i++) {
            _cups[i] = new ShellCup(1);
            a.bindShellCup(_cups[i], i);
        }
        _cups[7] = new PlayerCup(a);
        a.bindStore(_cups[7]);
        a.bindBoard(this);

        //define player b cups
        for (int i = 8; i < 15; i++) {
            _cups[i] = new ShellCup(1);
            b.bindShellCup(_cups[i], i - 8);
        }
        _cups[15] = new PlayerCup(a);
        b.bindStore(_cups[15]);
        b.bindBoard(this);
    }

    /**
     * Picks up shells from the selected cup.
     * @param index of the cup
     * @return Hand of shells object.
     */
    public HandOfShells pickUpShells(int index){
        return pickUpShells(index, false);
    }

    /**
     * Picks up shells from the selected cup.
     * @param index of the cup
     * @param robber if set to true the opponents cups are valid instead of current players.
     * @return hand of shells object.
     */
    public HandOfShells pickUpShells(int index, boolean robber){
        if (!isValid(index, robber))
            return null;

        addMove(getCurrentPlayer(), index);
        
        Player player = (index < 7) ? getPlayerA() : getPlayerB();
        if (robber){
            if (isPlayerA(player))
                player = getPlayerB();
            else
                player = getPlayerA();
        }

        HandOfShells hand = new HandOfShells(player, index, _cups[index].pickUpShells());

        hand.bindBoard(this);
        return hand;
    }

    /**
     * Checks whether a cup can be selected.
     * @param index of the cup
     * @param robber if set to true the opponents cups are valid instead of current players.
     * @return if the move is valid
     */
    public boolean isValid(int index, boolean robber) {
        Player player = robber ? getOpponent() : getCurrentPlayer();
        Cup cup = _cups[index];

        if (cup.getCount() == 0)
            return false;

        return player == null || (_validMoveExists && player.isPlayersCup(cup));
    }

    /**
     * Determines whether to change who the current player is. Will return the player
     * that should move next.
     * @param cup is used to check if the last shell landed in player's store.
     * @return the player that is the current player.
     */
    public Player nextPlayersMove(Player player, int cup){
        if (_currentPlayer == null) {
            if (player.isPlayersCup(_cups[cup], true))
                return player;

            return null;
        }

        _currentPlayer.moveEnd();

        if (!(isCurrentPlayersStore(cup) && _currentPlayer.hasValidMove()) &&
                getOpponent().hasValidMove()) {
            _currentPlayer.resetMove();//the amount of moves the player has just done is set to 0 again
            _currentPlayer = getOpponent();
        }
        else if (!getCurrentPlayer().hasValidMove() && !getOpponent().hasValidMove()){
            _validMoveExists = false;
            addStateMessage(BoardState.GAME_OVER);
        }

        if (hasValidMoves())
            _currentPlayer.moveStart();

        return _currentPlayer;
    }

    /**
     * Gives turn to the next player.
     */
    public void nextPlayersMove(){
        _currentPlayer.moveEnd();

        if (getOpponent().hasValidMove()) {
            _currentPlayer = getOpponent();
            _currentPlayer.moveStart();
        }
        else if (hasValidMoves())
            _currentPlayer.moveStart();
    }

    /**
     * Adds a shell to the specified cup.
     * @param index cup in question.
     */
    public void addShell(int index) {
        _cups[index].addShell();
    }

    /**
     * Checks if a provided cup belongs to the opponent of the current player.
     * @param index the location of the cup in question
     * @return true if the indicated cup belongs to the current player's opponent
     */
    public boolean isOpponentStore(int index) {
        return getOpponent().isPlayersCup(_cups[index], true);
    }

    /**
     * Checks if a provided cup belongs to the current player.
     * @param index the location of the cup in question
     * @return true if the indicated cup belongs to the current player
     */
    public boolean isCurrentPlayersStore(int index) {
        return getCurrentPlayer().isPlayersCup(_cups[index], true);
    }

    /**
     * Gets the current player.
     * @return current player.
     */
    public Player getCurrentPlayer(){
        return _currentPlayer;
    }

    /**
     * Gets the opponent player.
     * @return opponent player.
     */
    public Player getOpponent(){
        if (_currentPlayer == _playerOne)
            return _playerTwo;
        else
            return _playerOne;
    }

    /**
     * Gets the specified cup.
     * @param index of the cup in question.
     * @return cup in question.
     */
    public Cup getCup(int index){
        return _cups[index];
    }

    /**
     * Checks if the game over condition is met.
     * @return return true of game should end, false otherwise
     */
    public boolean hasValidMoves(){
        return _validMoveExists;
    }

    /**
     * Determines if provided player the the first player
     * @param p Provided player.
     * @return True if provided player is the first player.
     */
    public boolean isPlayerA(Player p){
        return _playerOne == p;
    }

    /**
     * Determines if provided player the the second player
     * @param p Provided player.
     * @return true if provided player is the second player.
     */
    public boolean isPlayerB(Player p){
        return _playerTwo == p;
    }

    /**
     * Gets the first player.
     * @return first player.
     */
    public Player getPlayerA(){
        return _playerOne;
    }

    /**
     * Gets the second player.
     * @return second player.
     */
    public Player getPlayerB(){
        return _playerTwo;
    }

    /**
     * Gets the PlayerA cup
     * @return returns player cup object.
     */
    public Cup getPlayerCupA(){
        return _cups[7];
    }

    /**
     * Gets the PlayerB cup
     * @return returns player cup object.
     */
    public Cup getPlayerCupB(){
        return _cups[15];
    }

    /**
     * Adds a message to the list of messages.
     * @param msg message to be added.
     */
    public void addStateMessage(BoardState msg){
        _state_messages.add(msg);
    }

    /**
     * Gets all of the saved messages.
     * @return saved messages.
     */
    public ArrayList<BoardState> getMessages(){
        return _state_messages;
    }

    /**
     * Adds a move to the list of moves made this game.
     * @param player Player that made the move.
     * @param move Cup upon which the move was executed.
     */
    public void addMove(Player player, int move){
        _moves.add(new Pair<Player, Integer>(player, move));
    }

    /**
     * Gets the list of moves made in this game.
     * @return an array list of player/move pairs.
     */
    public ArrayList<Pair<Player, Integer>> getMoves(){
        return _moves;
    }

    /**
     * Used in unit testing to ensure that _playerOne is the current player.
     */
    public void setCurrentPlayerA() {
        _currentPlayer = _playerOne;
    }

    public void setCurrentPlayerB() {
        _currentPlayer = _playerTwo;
    }


    protected Integer[] getState(){
        Integer[] state = new Integer[16];
        for (int i = 0; i < _cups.length; i++)
            state[i] = _cups[i].getCount();

        return state;
    }

    public Node<State> getStateNode(){
        Node<State> node = new Node<>(new State(1));

        node.getElement().setPlayer(getOpponent());
        node.getElement().setValue(0);
        node.getElement().setState(getState());

        return node;
    }

    /**
     * Checks if the game is currently a draw or if the game ended in a draw
     * @return if the game is a draw
     */
    public boolean isDraw(){
        return _playerOne.getScore() == _playerTwo.getScore();
    }

    /**
     * Get the Player that is currently winning or the one that has just won the Game
     * @return the Player that is winning or has just won
     */
    public Player getPlayerWon(){
        if(_playerOne.getScore() > _playerTwo.getScore()){
            return _playerOne;
        }
        return _playerTwo;
    }

    /**
     * Used to return the winning player after a match
     */
    public Player getWinningPlayer() {
        if (_playerOne.getScore() > _playerTwo.getScore())
            return _playerOne;

        if  (_playerOne.getScore() < _playerTwo.getScore())
            return _playerTwo;

        return null;
    }
    
}
