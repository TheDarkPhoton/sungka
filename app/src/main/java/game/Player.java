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
    private Cup[] cups;
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
        this.cups = new Cup[7];
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
        if (store == cup)
            return true;

        if (just_store)
            return false;

        for (int i = 0; i < cups.length; i++) {
            if (cups[i] == cup)
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
        cups[index] = cup;
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
     * Checks if player has any valid moves.
     * @return true if there are moves that this player can make.
     */
    public boolean hasValidMove(){
        for (int i = 0; i < cups.length; i++) {
            if (cups[i].getCount() > 0)
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
