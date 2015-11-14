package game.connection;

import android.util.Log;

import com.example.deathgull.sungka_project.MenuActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import game.player.RemoteHuman;

/**
 * This class represents the Client in the Server-Client relationship. This will allow one Player to connect to anothers
 * server and thereby be able to play against each other. This player joins the game.
 */
public class SungkaClient extends SungkaConnection{
    private String TAG = "SungkaClient";
    private String hostIp;
    private int portNumber;
    private Socket clientSocket;

    /**
     * The constructor to provide the necessary information about the Server
     * @param hostIp the IP address of the server, it is the IPv4 address if both users are connected to the same network
     * @param portNumber the port of the server which we want to connect to
     * @param playerName the name of the Player on the current device
     * @param menuActivity the MenuActivity from where we need to show that a connection has been established
     */
    public SungkaClient(String hostIp, int portNumber,String playerName,MenuActivity menuActivity){
        this.hostIp = hostIp;
        this.portNumber = portNumber;
        sungkaProtocol = null;
        this.playerName = playerName;
        this.menuActivity = menuActivity;
    }


    /**
     * Sets up the Socket to connect to the server, as well as setting up a PrintWriter to send information to
     * the server and a BufferedReader to receive message from the Server
     */
    private void connectClient() throws IOException {
        Log.v("SungkaClient","Establishing connection");
        clientSocket = new Socket(hostIp,portNumber);
        Log.v("SungkaClient","Connected");
        printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    }

    /**
     * This method is called to set up the client Socket, along with the PrintWriter to send information
     * to the Server and the BufferedReader to receive information from the Server
     * @param params empty parameters
     * @return true if it successfully has connected to the Server and set up the writers and readers to the Server
     * else it returns false
     */
    @Override
    protected Boolean doInBackground(String... params) {
        try {
            connectClient();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Close the connection the server
     * @throws IOException if an issue arises when attempting to close the Socket
     */
    public void closeConnection() throws IOException {
        clientSocket.close();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

}