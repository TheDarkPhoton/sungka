package game;

/**
 * Player Class which represents a Player. This class will be inherited to form a
 * Human player and a AI player.
 */
public class Player {
    private int moves;
    private String name;
    private int score;
    private Cup store;
    private Cup[] shellCups;

    /**
     * Initializes the Player Object, along with initializing the values of the Player's store and their respective
     * shell cups
     * @param name the name of the player
     */
    public Player(String name){
        this.name = name;
        this.shellCups = new Cup[7];
        this.store = null;
    }

    /**
     * Saves the player cup reference to this player.
     * @param cup of the player.
     */
    public void bindStore(Cup cup){
        store = cup;
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
     * @return the current amount of shells a Player has in their store
     */
    public int getScore(){
        return store.getCount();
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
            Player otherPlayer = (Player) object;
            if(otherPlayer.getName().equals(name)){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }
}