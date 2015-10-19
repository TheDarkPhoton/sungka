package game;

/**
 * Created by darkphoton on 19/10/15.
 */
public class Board {
    private Cup[] _cups;
    //define current player variables here

    Board(){
        _cups = new Cup[16];

        //define player a cups
        for (int i = 0; i < 7; i++)
            _cups[i] = new ShellCup(7);
        _cups[7] = new PlayerCup();

        //define player b cups
        for (int i = 8; i < 16; i++)
            _cups[i] = new ShellCup(7);
        _cups[16] = new PlayerCup();
    }

    public void distribute(int index){
        int shells = _cups[index++].pickUpShells();

        //distribute picked up shells
        while (shells > 0){
            if (index >= _cups.length)
                index = 0;

            Cup cup = _cups[index++];
            if (cup.isNotPlayerCup()){
                cup.addShell();
                --shells;
            }
        }
    }

    public boolean isGameOver(){
        return false;
    }

    public Cup getPlayerCupA(){
        return _cups[7];
    }
    public Cup getPlayerCupB(){
        return _cups[16];
    }
}
