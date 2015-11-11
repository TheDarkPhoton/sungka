package game.connection;

import android.os.AsyncTask;
import android.os.Handler;
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
    private String TAG = "SungkaConnection";
    protected PrintWriter printWriter;
    protected SungkaProtocol sungkaProtocol;
    protected Thread communicationThread;
    protected BufferedReader bufferedReader;
    protected GameActivity gameActivity;
    private Handler timerHandler = new Handler();
    private Handler pingHandler = new Handler();
    protected Runnable pingOther = new Runnable() {
        @Override
        public void run() {//every 5 seconds ping and wait 10 seconds for a
            Log.v(TAG,"sending Ping through a message");
            sendMessage(SungkaProtocol.PING);
            Log.v(TAG,"started timer");
            timerHandler.postDelayed(connectionLost,10000);
        }
    };
    protected Runnable connectionLost = new Runnable() {
        @Override
        public void run() {
            //connection is lost
            Log.v(TAG,"didnt receive a ping back");
            gameActivity.otherPlayerDidDisconnect();
        }
    };

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
        sungkaProtocol = new SungkaProtocol(remoteHuman,gameActivity,this);
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
        ping();
        communicationThread = new Thread(new SungkaReceiver(bufferedReader,sungkaProtocol));
        communicationThread.start();
    }

    public void stopTimer(){
        Log.v(TAG,"Stoped the timer with the time Handler");
        timerHandler.removeCallbacks(connectionLost);
    }

    public void ping(){
        Log.v(TAG,"About to send a ping in 50ms");
        pingHandler.postDelayed(pingOther, 50);
    }

    public void stopPings(){
        Log.v(TAG,"Stopped pings");
        pingHandler.removeCallbacks(pingOther);
    }
}
