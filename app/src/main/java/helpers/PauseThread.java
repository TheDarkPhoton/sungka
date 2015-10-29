package helpers;

/**
 * Created by darkphoton on 29/10/15.
 */
public class PauseThread {
    public PauseThread(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
