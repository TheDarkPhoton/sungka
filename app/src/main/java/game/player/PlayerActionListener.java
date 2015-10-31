package game.player;


public interface PlayerActionListener {
    void onMoveStart(final Player player);
    void onMove(final Player player, final int index);
    void onMoveEnd(final Player player);
}
