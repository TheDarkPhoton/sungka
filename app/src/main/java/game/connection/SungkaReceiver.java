package game.connection;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * This class is used to be run on a separate thread to receive the data between the devices and use the SungkaProtocol, to perform
 * the appropriate operations on those messages.
 */
public class SungkaReceiver implements Runnable{
    private BufferedReader bufferedReader;
    private SungkaProtocol sungkaProtocol;


    /**
     * Constructor that provides the necessary variables to be able to obtain the data sent between the devices and perform operations
     * on it.
     * @param bufferedReader the reader that receives the data sent by the other device
     * @param sungkaProtocol the protocol used to process the data
     */
    public SungkaReceiver(BufferedReader bufferedReader,SungkaProtocol sungkaProtocol){
        this.bufferedReader = bufferedReader;
        this.sungkaProtocol = sungkaProtocol;
    }

    /**
     * Called so that it begins to read in data sent from the other device
     */
    @Override
    public void run() {
        String fromOtherDevice;
        //check that buffered reader isnt null
        try {
            while( (fromOtherDevice = bufferedReader.readLine())!=null) {//text received from the other device
                if (sungkaProtocol != null) {
                   // Log.v("SungkaReceiver",fromOtherDevice);
                    sungkaProtocol.updateGame(fromOtherDevice);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
