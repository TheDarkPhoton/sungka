package game.connection;

import android.util.Log;

import com.example.deathgull.sungka_project.MenuActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import game.player.RemoteHuman;


/**
 * This class represents the Server that one Player will set up, so that another Player
 * can connect to it. Then the Player's will be able to play against each other. This player hosts the game.
 */
public class SungkaServer extends SungkaConnection {
    private String TAG ="SungkaServer";
    private int portNumber;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    /**
     * The constructor used to provide the necessary information to set up the ServerSocket
     * @param portNumber the port that the ServerSocket will bind to.
     * @param menuActivity the MenuActivity from where we need to show that a connection has been established
     * @param playerName the name of the Player on the current device
     */
    public SungkaServer(int portNumber,MenuActivity menuActivity,String playerName){
        this.portNumber = portNumber;
        sungkaProtocol = null;
        this.menuActivity = menuActivity;
        this.playerName = playerName;
    }
    /**
     * Sets up the ServerSocket and waits for a Socket to connect to it, as well as setting up a PrintWriter to send information to
     * the Client and a BufferedReader to receive message from the Client
     * @throws IOException if an error occurs when setting up the ServerSocket or the read and write objects we will use
     */
    private void setUpServer() throws IOException {
        serverSocket = new ServerSocket(portNumber);
        Log.v("SungkaServer","Establishing connection");
        clientSocket = serverSocket.accept();//waits for a client socket to connect to it
        Log.v("SungkaServer", "Connection established");
        printWriter = new PrintWriter(clientSocket.getOutputStream(),true);//to send messages to the other user
        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//to read what the other user sends
    }

    /**
     *Called to set up the server in the background
     * @param params empty parameters
     * @return true if it successfully sets up the ServerSocket with the ability to read and write to the Socket connected to it, else it returns false
     */
    @Override
    protected Boolean doInBackground(String... params) {
        try {
            setUpServer();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    public void closeConnection() throws IOException {
        serverSocket.close();
    }
}
