package game;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import game.player.RemoteHuman;
//might have to interface client and server, and then depending on the one we have do the appropriate action
/**
 * This class represents the Client in the Server-Client relationship. This will allow one Player to connect to anothers
 * server and thereby be able to play against each other. This player joins the game.
 */
public class SungkaClient extends SungkaConnection{
    //TODO:allow the user to connect to a server using a Socket, and pass the information when a move is made to the SungkaServer
    //TODO:so that the other player can also see it in their screen
    //TODO: will probably need a Board object to have access to the board, to perform the changes of the other user
    //TODO:should have static string values that indicate what the other user did eg: perform a move,has left or wants to end the game
    //TODO: make a class that deals with the messages being sent in a separate thread of the main one
    private String hostName;
    private int portNumber;
    private Socket clientSocket;


    /**
     * The constructor to provide the necessary information about the Server
     * @param hostName the IP address of the server, it is the IPv4 address if both users are connected to the same network
     * @param portNumber the port of the server which we want to connect to
     * @param remoteHuman the other Player in the game
     */
    public SungkaClient(String hostName, int portNumber,RemoteHuman remoteHuman){
        this.hostName = hostName;
        this.portNumber = portNumber;
        sungkaProtocol = new SungkaProtocol(remoteHuman);
    }
    public SungkaClient(String hostName, int portNumber){
        this.hostName = hostName;
        this.portNumber = portNumber;
        sungkaProtocol = null;
    }

    public void setSungkaProtocol(RemoteHuman remoteHuman){
        sungkaProtocol = new SungkaProtocol(remoteHuman);
    }

    /**
     * Sets up the Socket to connect to the server, as well as setting up a PrintWriter to send information to
     * the server and a BufferedReader to receive message from the Server
     */
    private void connectClient() throws IOException {
        clientSocket = new Socket(hostName,portNumber);
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
        //TODO:could show a dialog on the gameActivity while the connection is established between the devices
        super.onPreExecute();
    }


    protected void onPostExecute(Boolean result){
        //TODO:could remove that dialog since the connection has been established
        super.onPostExecute(result);
        //listenForServerHandler.postDelayed(listenForServer, 50);//start the listenForServer runnable thread in 50 ms
    }


}