package helpers.backend;

/**
 * Created by darkphoton on 13/11/15.
 */
public class PauseThreadFor {
    public PauseThreadFor(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
