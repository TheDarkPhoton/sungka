package com.example.darkphoton.sungka_project.players;

/**
 * Player Class which represents a Player. This class will be inherited to form a
 * Human player and a AI player.
 */
public class Player {
    private int moves;
    private String name;
    private int score;
    private PlayerCup store;
    private ShellCup[] shellCups;

    /**
     * Initializes the Player Object, along with initializing the values of the Player's store and their respective
     * shell cups
     * @param name the name of the player
     */
    public Player(String name){
        this.name = name;
        shellCups = new ShellCup[7];
        store = null;
    }
    /**
     *
     * @return the current amount of shells a Player has in their store
     */
    public int getScore(){
        return store.getShells();
    }

    /**
     *
     * @return the name of the Player
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
     * Stores the Player's Store to them
     * @param store the Player's store
     */
    public void setStore(PlayerCup store){
        this.store = store;
    }

    /**
     * Stores the Player's ShellCups
     * @param shellCups the Player's ShellCups
     */
    public void setShellCups(ShellCup[] shellCups){
        this.shellCups = shellCups;
    }

    /**
     * Method used to be able to identify between Player objects
     * @param object the Object which we are checking if it is equal to this Player Object
     * @return true if the object is equal to this Player Object, false otherwise
     */
    public boolean equals(Object object){
        try{
            Player otherPlayer = (Player) o;
            if(otherPlayer.getName().equals(name)){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }
}
