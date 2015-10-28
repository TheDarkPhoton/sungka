package game.player;

/**
 * Created by darkphoton on 28/10/15.
 */
public interface PlayerActionListener {
    void onMoveStart();
    void onMove(int cupIndex);
    void onMoveEnd();
}
