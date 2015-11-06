package game;

import android.os.AsyncTask;
import android.util.Log;

import com.example.deathgull.sungka_project.GameActivity;

import java.io.BufferedReader;
import java.io.PrintWriter;

import game.player.RemoteHuman;

/**
 * General connection class, SungkaClient and SungkaServer classes extend this and specify the appropriate
 * operations that need to be done depending if the User is a host or if they join the game.
 */
public abstract class SungkaConnection extends AsyncTask<String,Integer,Boolean> {
    public static final String HOST_CONNECTION = "HOSTCONNECTION";
    public static final String JOIN_CONNECTION = "JOINCONNECTION";
    protected PrintWriter printWriter;
    protected SungkaProtocol sungkaProtocol;
    protected Thread communicationThread;
    protected BufferedReader bufferedReader;
    protected GameActivity gameActivity;

    /**
     * Send a message to the other device
     * @param message the information of the board which you want to send to the other device
     */
    public void sendMessage(String message){
        Log.v("SungkaConnection",message);
        printWriter.println(message);
    }

    /**
     * Set the SungkaProtocol used to process the data, by providing the RemoteHuman on which the actions need to be performed on
     * @param remoteHuman the RemoteHuman object that represents the other user in a multiplayer game
     */
    public void setSungkaProtocol(RemoteHuman remoteHuman){
        sungkaProtocol = new SungkaProtocol(remoteHuman,gameActivity);
    }

    /**
     * Set the activity that is running on the main thread where the views we need to perform actions on
     * are running on.
     * @param gameActivity the GameActivity that currently is running
     */
    public void setActivity(GameActivity gameActivity){
        this.gameActivity = gameActivity;
    }

    /**
     * Initialize the Thread to listen for the data being sent by the other device
     */
    public void beginListening(){
        communicationThread = new Thread(new SungkaReceiver(bufferedReader,sungkaProtocol));
        communicationThread.start();
    }
}
