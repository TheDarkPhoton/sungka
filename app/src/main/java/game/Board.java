package game;

/**
 * An object that describes the state of the current game board
 */
public class Board {
    private Cup[] _cups;                                    //an array of board cups
    //define current player variables here

    /**
     * Constructs board with default attributes.
     */
    Board(Player a, Player b){
        _cups = new Cup[16];

        //define player a cups
        for (int i = 0; i < 7; i++) {
            _cups[i] = new ShellCup(7);
            a.bindShellCup(_cups[i], i);
        }
        _cups[7] = new PlayerCup(a);
        a.bindStore(_cups[7]);

        //define player b cups
        for (int i = 8; i < 16; i++) {
            _cups[i] = new ShellCup(7);
            b.bindShellCup(_cups[i], i - 8);
        }
        _cups[16] = new PlayerCup(a);
        b.bindStore(_cups[16]);
    }

    /**
     * Distributes shells of the cup indicated by the index.
     * @param index of the cup in the array.
     */
    public void distribute(int index){
        //get the number of shells in the cup and remove them, then increment index
        int shells = _cups[index++].pickUpShells();

        //while we have shells distribute them
        while (shells > 0){
            //if index is more then the size of cups then loop around and continue
            if (index >= _cups.length)
                index = 0;

            //get the cup object and then increment index
            Cup cup = _cups[index++];

            //if cup is not a player cup then add a shell to that cup
            if (cup.isNotPlayerCup()){
                cup.addShell();
                --shells;
            }
        }
    }

    /**
     * Checks if the game over condition is met.
     * @return return true of game should end, false otherwise
     */
    public boolean isGameOver(){
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
        return _cups[16];
    }
}
