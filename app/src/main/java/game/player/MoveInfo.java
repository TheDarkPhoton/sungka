package game.player;

/**
 * Stores the statistical data of a Players move in the game
 */
public class MoveInfo {
    private double timeStartMoveMilli;
    private double timeEndMoveMilli;
    private double durationOfMoveMilli;
    private int numOfShellsCollected;
    private String playerName;

    /**
     * Call this method at the start of a Players turn
     * @param timeStartMoveMilli the current time in milliseconds.
     * @param playerName the name of the Player for which we are storing the move information for
     */
    public MoveInfo(double timeStartMoveMilli,String playerName){
        this.timeStartMoveMilli = timeStartMoveMilli;
        timeEndMoveMilli = 0;
        this.playerName = playerName;
    }

    /**
     * This method is called after the player has ended their move, need to be called after
     * endMove(double timeEndMoveMilli) else an empty String is returned
     * @return the duration of the Players move in the format of mm:ss (minutes:seconds)
     */
    public String getDurationOfMove(){
        String finalTimerString = "";
        if(timeEndMoveMilli >0) {
            calculateMoveDuration();//calculate the duration of the move in milliseconds

            String minutesAsString;//string variable that will hold the minutes of the move
            String secondsAsString;//string variable that will hold the seconds of the move

            int minutes = (int) (durationOfMoveMilli % (1000 * 60 * 60)) / (1000 * 60);//calculate the milliseconds to minutes
            int seconds = (int) ((durationOfMoveMilli % (1000 * 60 * 60)) % (1000 * 60) / 1000);//calculate the milliseconds to seconds

            //to get 2 digit minute string values
            if (minutes < 10) {//if the value of minutes is just one digit, then add a 0 to it before
                minutesAsString = "0" + minutes;//adding it to the string variable
            } else {//else just add the minutes to the string
                minutesAsString = Integer.toString(minutes);
            }

            //to get 2 digit second string values
            if (seconds < 10) {//if the value is just one digit then add a 0 before it
                secondsAsString = "0" + seconds;
            } else {//else just add the value
                secondsAsString = Integer.toString(seconds);
            }
            //now we will get a result of the type mm:ss, for the duration of the move
            finalTimerString += minutesAsString + ":" + secondsAsString;
        }
        return finalTimerString;//and return it
    }

    /**
     * Calculates the duration of the move in milliseconds
     */
    private void calculateMoveDuration(){
        durationOfMoveMilli = timeEndMoveMilli-timeStartMoveMilli;
    }

    /**
     * Call this method when the user has ended there turn
     * @param timeEndMoveMilli the current time in milliseconds
     */
    public void endMove(double timeEndMoveMilli){
        this.timeEndMoveMilli = timeEndMoveMilli;
    }

    /**
     * Set the number of shells the Player has put in there Store in this move
     * @param numOfShellsCollected the number of Shells the Player has placed in there Store in this turn
     */
    public void setNumOfShellsCollected(int numOfShellsCollected){
        this.numOfShellsCollected = numOfShellsCollected;
    }

    /**
     * Get the number of Shells the Player has put in there store in this move
     * @return the number of Shells the Player has placed in there Store in this turn
     */
    public int getNumOfShellsCollected(){
        return numOfShellsCollected;
    }

    /**
     * The toString method of this class, which provides information of the Players Move
     * @return a String that shows the Shells placed in the Store in this move
     * and the duration of the move in the format mm:ss (minuts:seconds)
     */
    public String toString(){
        return String.format("Shells Collected: %d, Duration:(%s)",numOfShellsCollected,getDurationOfMove());
    }
}
