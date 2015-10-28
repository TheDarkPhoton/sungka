package game.player;

/**
 * Created by darkphoton on 28/10/15.
 */
public interface PlayerActionListener {
    void onMoveStart(Player player);
    boolean onMove(Player player, int index);
    void onMoveEnd(Player player);
}
