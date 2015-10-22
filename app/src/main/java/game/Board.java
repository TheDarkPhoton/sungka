package game;

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

    /**
     * Distributes shells of the cup indicated by the index.
     * @param index of the cup in the array.
     */
    public void distribute(int index){
        // don't allow a Player to select an opponent's Cup
        if (((PlayerCup) _cups[index]).getPlayer() != _currentPlayer)
            return;

        //get the number of shells in the cup and remove them, then increment index
        int shells = _cups[index++].pickUpShells();

        //while we have shells distribute them
        while (shells > 0){
            //if index is more then the size of cups then loop around and continue
            if (index >= _cups.length)
                index = 0;

            //get the cup object and then increment index
            Cup cup = _cups[index++];

            // if cup is not a player cup OR the cup is the player's store,
            // then add a shell to that cup
            if (cup.isNotPlayerCup() || _currentPlayer.isStore(cup)) {
                cup.addShell();
                --shells;
            }
        }

        // need to check a few things with the last used cup
        int lastIndex = (index - 1) % 16;
        Cup lastCup = _cups[lastIndex];

        if (lastCup.getCount() == 1 && _currentPlayer.isShellCup(lastCup, lastIndex)) {

            // the last shell fell into an empty cup belonging to the player, so capture shells
            int numShells = lastCup.pickUpShells();
            numShells += _cups[index % 8].pickUpShells();
            _currentPlayer.captureShells(numShells);
        }

        // will possibly change in future
        if (!_currentPlayer.isStore(lastCup)) {

            // the last cup isn't the current player's store, so switch players
            _currentPlayer = (_currentPlayer == _playerOne) ? _playerTwo : _playerOne;
        }
    }

    /**
     * Checks if a provided cup belongs to the opponent of the current player.
     * @param index the location of the cup in question
     * @return true if the indicated cup belongs to the current player's opponent
     */
    public boolean isOpponentStore(int index) {
        if (_currentPlayer.isStore(getPlayerCupA())) {
            // PlayerB's store is at 15
            return index == 15;
        } else if (_currentPlayer.isStore(getPlayerCupB())) {
            // PlayerA's store is at 7
            return index == 7;
        }

        return false;
    }

    /**
     * Checks if the game over condition is met.
     * @return return true of game should end, false otherwise
     */
    public boolean isGameOver(){
        return false;
    } // TODO

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
