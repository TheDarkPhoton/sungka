package game;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class represents the Server that one Player will set up, so that another Player
 * can connect to it. Then the Player's will be able to play against each other. This player hosts the game.
 */
public class SungkaServer extends AsyncTask<String,Integer,Boolean> {
    private int portNumber;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private Handler listenForClientHandler;
    private SungkaProtocol sungkaProtocol;
    private Runnable listenForClient = new Runnable() {
        @Override
        public void run() {
            String fromClient;
            try {
                while((fromClient = bufferedReader.readLine()) !=null) {
                    //received a message from the client, do the appropriate change in the board
                    sungkaProtocol.updateGame(fromClient);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    //TODO: be able to let a user set up a server, and allow the server to be able handle all the operations
    //TODO:including passing information of the current state of the board between the Players, as well as their moves

    /**
     * The constructor used to provide the necessary information to set up the ServerSocket
     * @param portNumber the port that the ServerSocket will bind to
     * @param board the board object of the current Player
     */
    public SungkaServer(int portNumber,Board board){
        this.portNumber = portNumber;
        listenForClientHandler = new Handler();
        sungkaProtocol = new SungkaProtocol(board);
    }

    /**
     * Sets up the ServerSocket and waits for a Socket to connect to it, as well as setting up a PrintWriter to send information to
     * the Client and a BufferedReader to receive message from the Client
     * @throws IOException if an error occurs when setting up the ServerSocket or the read and write objects we will use
     */
    private void setUpServer() throws IOException {
        serverSocket = new ServerSocket(portNumber);
        clientSocket = serverSocket.accept();//waits for a client socket to connect to it
        printWriter = new PrintWriter(clientSocket.getOutputStream(),true);//to send messages to the other user
        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//to read what the other user sends
    }

    /**
     * Send a message to the client, the user than joined the game
     * @param message the message you wish to send to the client
     */
    public void sendMessage(String message){
        printWriter.println(message);
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

    protected void onPostExecute(Boolean result){
        super.onPostExecute(result);
        listenForClientHandler.postDelayed(listenForClient, 50);//start the listenForClient runnable thread in 50 ms
    }


}
