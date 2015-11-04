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

    public void setSungkaProtocol(RemoteHuman remoteHuman){
        sungkaProtocol = new SungkaProtocol(remoteHuman,gameActivity);
    }

    public void setActivity(GameActivity gameActivity){
        this.gameActivity = gameActivity;
    }

    public void beginListening(){
        communicationThread = new Thread(new SungkaReceiver(bufferedReader,sungkaProtocol));
        communicationThread.start();
    }
}
