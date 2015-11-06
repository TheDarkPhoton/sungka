package game;

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
    private Runnable timer = new Runnable() {
        @Override
        public void run() {
            //connection is lost
            Log.v("SungkaReceiver","No data received from the other device in 90s");
        }
    };
    private Handler handler;

    /**
     * Constructor that provides the necessary variables to be able to obtain the data sent between the devices and perform operations
     * on it.
     * @param bufferedReader the reader that receives the data sent by the other device
     * @param sungkaProtocol the protocol used to process the data
     */
    public SungkaReceiver(BufferedReader bufferedReader,SungkaProtocol sungkaProtocol){
        this.bufferedReader = bufferedReader;
        this.sungkaProtocol = sungkaProtocol;
        handler = new Handler();
    }

//TODO: could cause a problem when you are not deciding anything and the timer runs out

    /**
     * Called so that it begins to read in data sent from the other device
     */
    @Override
    public void run() {
        String fromOtherDevice;
        handler.postDelayed(timer,90000);//wait 90 seconds, if its not stopped then the connection is lost
        try {
            while((fromOtherDevice = bufferedReader.readLine())!=null) {//text received from the other device
                handler.removeCallbacks(timer);//no need to end the connection
                if (sungkaProtocol != null) {
                    Log.v("SungkaReceiver",fromOtherDevice);
                    sungkaProtocol.updateGame(fromOtherDevice);
                }
                handler.postDelayed(timer,90000);//start the timer again
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
