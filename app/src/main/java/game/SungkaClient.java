package game;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class represents the Client in the Server-Client relationship. This will allow one Player to connect to anothers
 * server and thereby be able to play against each other.
 */
public class SungkaClient extends AsyncTask<String,Integer,Boolean> {
    //TODO:allow the user to connect to a server using a Socket, and pass the information when a move is made to the SungkaServer
    //TODO:so that the other player can also see it in their screen
    //TODO: will probably need a Board object to have access to the board, to perform the changes of the other user
    //TODO:should have static string values that indicate what the other user did eg: perform a move,has left or wants to end the game
    private String hostName;
    private int portNumber;
    private Board board;
    private Socket clientSocket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private Handler listenForServerHandler;
    private Runnable listenForServer = new Runnable() {
        @Override
        public void run() {
            String fromServer;
            try {
                while((fromServer = bufferedReader.readLine()) !=null) {
                    //received a message from the server, do the appropriate change in the board
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * The constructor to provide the necessary information about the Server
     * @param hostName the IP address of the server, it is the IPv4 address if both users are connected to the same network
     * @param portNumber the port of the server which we want to connect to
     * @param board the Board object of the player that is connecting to the server
     */
    public SungkaClient(String hostName, int portNumber,Board board){
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.board = board;
    }

    /**
     * Sets up the Socket to connect to the server, as well as setting up a PrintWriter to send information to
     * the server and a BufferedReader to receive message from the Server
     */
    private void connectClient() throws IOException {
            clientSocket = new Socket(hostName,portNumber);
            printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            listenForServerHandler = new Handler();
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
     * Send a message to the server
     * @param message the information of the board which you want to send to the server
     */
    public void sendMessageToServer(String message){
        printWriter.println(message);
    }

    /**
     * Close the connection the server
     * @throws IOException if an issue arrises when attempting to close the Socket
     */
    public void closeConnection() throws IOException {
        clientSocket.close();
    }

    protected void onPostExecute(Boolean result){
        super.onPostExecute(result);
        listenForServerHandler.postDelayed(listenForServer, 50);//start the listenForServer runnable thread in 50 ms
    }
}
