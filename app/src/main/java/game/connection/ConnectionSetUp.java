package game.connection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class used after a connection has been established between the devices, but before they start the game. It is used
 * to send and receive the names of the Players to display them.
 */
public class ConnectionSetUp extends AsyncTask<String,Integer,String> {
    private String TAG = "ConnectionSetUp";
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String name;

    /**
     * Constructor that sets up the AsyncTask to send and receive the names of each of the Players
     * @param bufferedReader the BufferedReader that receives the text from the other device
     * @param printWriter the PrintWriter that allows you to send a message to the other device
     * @param name the name of the Player on this device
     */
    public ConnectionSetUp(BufferedReader bufferedReader,PrintWriter printWriter,String name){
        this.bufferedReader = bufferedReader;
        this.printWriter = printWriter;
        this.name = name;
        sendName();
    }

    /**
     * Send the name of the Player on this device to the other device
     */
    public void sendName(){
        Log.v(TAG,"Sengin name: "+name);
        printWriter.println(name);
    }

    /**
     * Start listening to get the name of the other Player on the other device
     * @param params empty parameters
     * @return the name of the other Player
     */
    @Override
    protected String doInBackground(String... params) {
        String fromOtherDevice;
        try {
            while((fromOtherDevice = bufferedReader.readLine())!=null) {//text received from the other device
                Log.v(TAG,"Received the other users name: "+fromOtherDevice);
                return fromOtherDevice;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
