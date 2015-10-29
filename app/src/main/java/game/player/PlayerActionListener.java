package game.player;

/**
 * Created by darkphoton on 28/10/15.
 */
public interface PlayerActionListener {
    void onMoveStart(final Player player);
    void onMove(final Player player, final int index);
    void onMoveEnd(final Player player);
}
