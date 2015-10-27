package game;

import java.util.ArrayList;

/**
 * PLAYER Class which represents a PLAYER. This class will be inherited to form a
 * Human player and a AI player.
 */
public class Player {
    private int moves;
    private String name;
    private int score;
    private Cup store;
    private Cup[] shellCups;
    private ArrayList<MoveInfo> moveInfos;//arraylist to store the users moves in a game

    /**
     * Initializes the PLAYER Object, along with initializing the values of the PLAYER's store and their respective
     * shell cups
     * Initializes the Player Object, along with initializing the values of the Player's store and their respective
     * shell cups. Also initializes the list that will hold the Player's Moves the game.
     * @param name the name of the player
     */
    public Player(String name){
        this.name = name;
        this.shellCups = new Cup[7];
        this.store = null;
        moveInfos = new ArrayList<MoveInfo>();
    }

    /**
     * Saves the player cup reference to this player.
     * @param cup of the player.
     */
    public void bindStore(Cup cup){
        store = cup;
    }

    public Cup getStore(){
        return store;
    }

    /**
     * Saves a players shell cup in a local array.
     * @param cup Shell cup of the player.
     * @param index array index that corresponds with the cup.
     */
    public void bindShellCup(Cup cup, int index){
        shellCups[index] = cup;
    }

    /**
     *
     * @return the current amount of shells a PLAYER has in their store
     */
    public int getScore(){
        return store.getCount();
    }

    /**
     *
     * @return the name of the PLAYER
     */
    public String getName(){
        return name;
    }

    /**
     * Increases the amount of moves a user has
     */
    public void addMove(){
        moves++;
    }

    /**
     * Checks if the provided cup is the PLAYER's store.
     * @param cup the Cup to check
     * @return true if the cup is the PLAYER's store
     */
    public boolean isStore(Cup cup) {
        return cup == store;
    }

    /**
     * Determines if a Cup is one of the PLAYER's Shell Cups
     * @param cup the Cup to check
     * @param i the position of the Cup in the array
     * @return true if the provided Shell Cup belongs to the PLAYER
     */
    public boolean isShellCup(Cup cup, int i) {
        if (cup.isNotPlayerCup()) {
            return shellCups[i % 8] == cup;
        }
        return false;
    }

    /**
     * Capture shells from opponent's cup
     * @param numShells the number of shells to put in the store
     */
    public void captureShells(int numShells) {
        // need to cast to PlayerCup in order to be able to use this method
        ((PlayerCup) store).addCapturedShells(numShells);
    }


    /**
     * Method used to be able to identify between PLAYER objects
     * @param object the Object which we are checking if it is equal to this PLAYER Object
     * @return true if the object is equal to this PLAYER Object, false otherwise
     */
    public boolean equals(Object object){
        try{
            Player otherPlayer = (Player) object;
            if(otherPlayer.getName().equals(name)){
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
        moveInfos.add(moveInfo);
    }

    /**
     * Get the list of all the Moves the Player has made up to this point
     * @return an ArrayList that contains the Player's moves
     */
    public ArrayList<MoveInfo> getMoveInfos(){
        return moveInfos;
    }
}
