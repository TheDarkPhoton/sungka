package game;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 */
public class SungkaReceiver implements Runnable{
    private BufferedReader bufferedReader;
    private SungkaProtocol sungkaProtocol;
    public SungkaReceiver(BufferedReader bufferedReader,SungkaProtocol sungkaProtocol){
        this.bufferedReader = bufferedReader;
        this.sungkaProtocol = sungkaProtocol;
    }

    @Override
    public void run() {
        String fromOtherDevice;
        try {
            while((fromOtherDevice = bufferedReader.readLine())!=null) {
                if (sungkaProtocol != null) {
                    Log.v("SungkaReceiver",fromOtherDevice);
                    sungkaProtocol.updateGame(fromOtherDevice);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
