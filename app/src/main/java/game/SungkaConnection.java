package game;

import android.os.AsyncTask;

import java.io.PrintWriter;

/**
 * General connection class, SungkaClient and SungkaServer classes extend this and specify the appropriate
 * operations that need to be done depending if the User is a host or if they join the game.
 */
public abstract class SungkaConnection extends AsyncTask<String,Integer,Boolean> {
    protected PrintWriter printWriter;

    /**
     * Send a message to the other device
     * @param message the information of the board which you want to send to the other device
     */
    public void sendMessage(String message){
        printWriter.println(message);
    }
}
