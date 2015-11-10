package com.example.deathgull.sungka_project;

/**
 * This class represents the statistics collected for a unique Player.
 */
public class PlayerStatistic {
    private String playerName;
    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private int gamesDrawn;
    private double averageMoveTimeInMillis;
    private double maxNumShellsCollected;
    private double maxConsecutiveMoves;

    /**
     * Creates a PlayerStatistic object, that is used to hold the statistics of a Player
     * @param playerName the name of the Player we are storing statistics for, this should be a unique name.
     */
    public PlayerStatistic(String playerName){
        this.playerName = playerName;
        gamesPlayed = 0;
        gamesLost = 0;
        gamesWon = 0;
        gamesDrawn = 0;
        averageMoveTimeInMillis = 0;
        maxNumShellsCollected = 0;
        maxConsecutiveMoves = 0;
    }

    /**
     * Get the Players name for which we are storing the statistics for
     * @return the name of the Player
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Get the games played by the Player
     * @return the games played by the Player
     */
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * Get the games won by the Player
     * @return the games won by the Player
     */
    public int getGamesWon() {
        return gamesWon;
    }

    /**
     * Get the games lost by the Player
     * @return the games lost by the player
     */
    public int getGamesLost() {
        return gamesLost;
    }

    /**
     * Get the average move or turn time of the Player
     * @return the average move or turn time of the Player
     */
    public double getAverageMoveTimeInMillis() {
        return averageMoveTimeInMillis;
    }

    /**
     * Get the maximum number of consecutive moves this Player has gotten
     * @return the maximum number of consecutive moves the Player has gotten
     */
    public double getMaxConsecutiveMoves() {
        return maxConsecutiveMoves;
    }

    /**
     * Get the number of Draws the Player has
     * @return the number of Draws the Player has
     */
    public int getGamesDrawn() {
        return gamesDrawn;
    }

    /**
     * Set the number of Draws the Player has
     * @param gamesDrawn the number of Draws the Player has
     */
    public void setGamesDrawn(int gamesDrawn) {
        this.gamesDrawn = gamesDrawn;
    }

    /**
     * Set the maximum number of consecutive moves this Player has gotten
     * @param maxConsecutiveMoves the maximum number of consecutive moves the Player has gotten
     */
    public void setMaxConsecutiveMoves(double maxConsecutiveMoves) {
        this.maxConsecutiveMoves = maxConsecutiveMoves;
    }

    /**
     * Get the maximum number of shells the Player has collected in all their games
     * @return the maximum number of shells the Player has ever gotten
     */
    public double getMaxNumShellsCollected() {
        return maxNumShellsCollected;
    }

    /**
     * Set the number of Games played by a Player;
     * @param gamesPlayed the number of Games the user has played
     */
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    /**
     * Set the number of Games won by the Player
     * @param gamesWon the number of Games the user has won
     */
    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    /**
     * Set the the number of Games lost by the Player
     * @param gamesLost the number of Games the user has lost
     */
    public void setGamesLost(int gamesLost) {
        this.gamesLost = gamesLost;
    }

    /**
     * Set the average turn or move time in milliseconds of the Player
     * @param averageMoveTimeInMillis the average turn or move time in milliseconds
     */
    public void setAverageMoveTimeInMillis(double averageMoveTimeInMillis) {
        this.averageMoveTimeInMillis = averageMoveTimeInMillis;
    }

    /**
     * Set the maximum number of shells collected by the Player
     * @param maxNumShellsCollected the maximum number of shells the Player has collected
     */
    public void setMaxNumShellsCollected(double maxNumShellsCollected) {
        this.maxNumShellsCollected = maxNumShellsCollected;
    }

    /**
     * Get the Players Win to Loss ratio (How many wins per loss)
     * @return the Players Win to Loss ratio
     */
    public double getPlayerWinLossRatio(){
        return gamesWon/gamesLost;
    }

    /**
     * Get the average time of the Players move in a format of mm:ss (minutes:seconds)
     * @return the average time for a Players move in a mm:ss(minutes:seconds) format
     */
    public String getAverageMoveTime(){
        String finalTimerString = "";
            String minutesAsString;//string variable that will hold the minutes of the move
            String secondsAsString;//string variable that will hold the seconds of the move

            int minutes = (int) (averageMoveTimeInMillis % (1000 * 60 * 60)) / (1000 * 60);//calculate the milliseconds to minutes
            int seconds = (int) ((averageMoveTimeInMillis % (1000 * 60 * 60)) % (1000 * 60) / 1000);//calculate the milliseconds to seconds

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
        return finalTimerString;//and return it
    }

    /**
     * Update the average time a Player takes to make a move, by including the average time they took to make a move
     * in a Game
     * @param gameAverageMoveTime the average time the Player took to make a move in a Game they just played
     */
    public void updateAverageMoveTimeInMillis(double gameAverageMoveTime){
        averageMoveTimeInMillis = (averageMoveTimeInMillis*(gamesPlayed-1)+gameAverageMoveTime)/gamesPlayed;
    }

    /**
     * Increase the amount of games played by the Player by 1
     */
    public void increaseGamesPlayed(){
        gamesPlayed++;
    }

    /**
     * Increase the amount of games won by the Player by 1
     */
    public void increaseGamesWon(){
        gamesWon++;
    }

    /**
     * Increase the amount of games lost by the Player by 1
     */
    public void increaseGamesLost(){
        gamesLost++;
    }

    /**
     * Increase the amount of games drawed by the Player by 1
     */
    public void increaseGamesDraw(){gamesDrawn++;}

    public String toString(){
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", playerName,gamesPlayed,gamesWon,gamesLost,gamesDrawn,averageMoveTimeInMillis,maxNumShellsCollected,maxConsecutiveMoves);
    }


}
