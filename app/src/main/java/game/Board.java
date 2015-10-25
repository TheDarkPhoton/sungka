package game;

import android.util.Log;

/**
 * An object that describes the state of the current game board
 */
public class Board {
    private Cup[] _cups;                                    // an array of board cups
    private Player _currentPlayer;                          // the player whose turn it is
    private Player _playerOne;
    private Player _playerTwo;

    /**
     * Constructs board with default attributes.
     */
    Board(Player a, Player b){
        _cups = new Cup[16];
        _playerOne = a;
        _playerTwo = b;

        //define player a cups
        for (int i = 0; i < 7; i++) {
            _cups[i] = new ShellCup(7);
            a.bindShellCup(_cups[i], i);
        }
        _cups[7] = new PlayerCup(a);
        a.bindStore(_cups[7]);

        //define player b cups
        for (int i = 8; i < 15; i++) {
            _cups[i] = new ShellCup(7);
            b.bindShellCup(_cups[i], i - 8);
        }
        _cups[15] = new PlayerCup(a);
        b.bindStore(_cups[15]);

        // temporary player assignment
        _currentPlayer = a;
    }

    public HandOfShells pickUpShells(int index){
        if (!(_currentPlayer.isShellCup(_cups[index], index) && _cups[index].getCount() > 0))
            return null;

        return new HandOfShells(index, _cups[index].pickUpShells(), this);
    }

    public Cup getCup(int index){
        return _cups[index];
    }

    public void addShell(int index) {
        _cups[index].addShell();
    }

    /**
     * Picks up shells from the selected cup.
     * @param index of the cup.
     * @return Hand of shells object.
     */
    public HandOfShells pickUpShells(int index){
        Cup cup = _cups[index];
        HandOfShells hand = new HandOfShells(index, cup.pickUpShells());

        //send to the other user

        hand.bindBoard(this);
        return hand;
    }

    /**
     * Checks if a provided cup belongs to the opponent of the current player.
     * @param index the location of the cup in question
     * @return true if the indicated cup belongs to the current player's opponent
     */
    public boolean isOpponentStore(int index) {
        Log.i("Board", "is opponents store Index " + index);
        if (_currentPlayer.isStore(getPlayerCupA())) {
            // PlayerB's store is at 15
            return index == 15;
        } else if (_currentPlayer.isStore(getPlayerCupB())) {
            // PlayerA's store is at 7
            return index == 7;
        }

        return false;
    }

    public boolean isCurrentPlayersStore(int index) {
        if (_currentPlayer.isStore(getPlayerCupA())) {
            // PlayerB's store is at 15
            return index == 7;
        } else if (_currentPlayer.isStore(getPlayerCupB())) {
            // PlayerA's store is at 7
            return index == 15;
        }

        return false;
    }

    public void nextPlayersMove(){
        if (_currentPlayer == _playerOne)
            _currentPlayer = _playerTwo;
        else
            _currentPlayer = _playerOne;
    }

    /**
     * Checks if the game over condition is met.
     * @return return true of game should end, false otherwise
     */
    public boolean isGameOver(){
        if (getPlayerCupA().getCount() + getPlayerCupB().getCount() == 98) {
            return true;
        }
        return false;
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
}
