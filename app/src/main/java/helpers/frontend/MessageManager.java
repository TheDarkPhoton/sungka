package helpers.frontend;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.example.deathgull.sungka_project.GameActivity;
import com.example.deathgull.sungka_project.R;

import game.player.Player;

/**
 * Created by martinkubat on 07/11/15.
 */
public class MessageManager {
    private YourMoveTextView _top;
    private YourMoveTextView _bottom;

    private FrameLayout _parent;
    private Player _currentPlayer;

    /**
     * Constructor
     * @param context of the activity
     * @param parent that the labels will be inset in
     */
    public MessageManager(Context context, FrameLayout parent) {
        _parent = parent;

        _top = new YourMoveTextView(context, true);
        _bottom = new YourMoveTextView(context, false);

        _parent = parent;
        _parent.addView(_top);
        _parent.addView(_bottom);

        // set ids on the text views
        GameActivity activity = (GameActivity) context;
        int idTop = activity.getResources().getIdentifier("moveTextPlayer2", "id", activity.getPackageName());
        int idBot = activity.getResources().getIdentifier("moveTextPlayer1", "id", activity.getPackageName());
        _top.setId(idTop);
        _bottom.setId(idBot);
    }

    /**
     * Called when a player starts a move
     * @param player that is currently playing
     */
    public void onMoveStart(Player player) {
        if (_currentPlayer == player)
            return;

        _currentPlayer = player;

        if (player.getBoard().isPlayerA(player)) {
            _bottom.setIsCurrentTurn(true);
        } else {
            _top.setIsCurrentTurn(true);
        }
    }

    public void onMoveEnd(Player player) {
        if (player.getBoard().isPlayerA(player)) {
            _bottom.setIsCurrentTurn(false);
        } else {
            _top.setIsCurrentTurn(false);
        }

    }

    /**
     * Called when the game is over
     * @param isDraw whether the game resulted in a draw
     * @param winningPlayer the winning player
     */
    public void gameOver(boolean isDraw, Player winningPlayer) {
        if (isDraw) {
            _bottom.displayPermanentMessage(R.string.str_ItsADraw);
            _top.displayPermanentMessage(R.string.str_ItsADraw);
        } else {

            if (winningPlayer.getBoard().isPlayerA(winningPlayer)){
                _bottom.displayPermanentMessage(R.string.str_YouWon);
                _top.displayPermanentMessage(R.string.str_YouLost);
            } else {
                _top.displayPermanentMessage(R.string.str_YouWon);
                _bottom.displayPermanentMessage(R.string.str_YouLost);
            }
        }

        _top.setIsGameover(true);
        _bottom.setIsGameover(true);
    }

    /**
     * Called when player gets another turn
     * @param player that gets another turn
     */
    public void playerGetsAnotherTurn(Player player) {
        if (player.getBoard().isPlayerA(player))
            _bottom.displayTemporaryMessage(R.string.str_AnotherTurn);
        else
            _top.displayTemporaryMessage(R.string.str_AnotherTurn);
    }

    /**
     * Called when player got robbed
     * @param player that got robbed
     */
    public void playerGotRobbed(Player player) {
        if (player.getBoard().isPlayerA(player)){
            _bottom.displayTemporaryMessage(R.string.str_YouWereRobbed);
            _top.displayTemporaryMessage(R.string.str_YouRobbedOtherPlayer);
        }
        else {
            _top.displayTemporaryMessage(R.string.str_YouWereRobbed);
            _bottom.displayTemporaryMessage(R.string.str_YouRobbedOtherPlayer);
        }
    }

    /**
     * Called when player got robbed of his final move
     * @param player that got robbed of his final move
     */
    public void playerGotRobbedOfHisFinalMove(Player player) {
        if (player.getBoard().isPlayerA(player))
            _top.displayTemporaryMessage(R.string.str_AnotherTurn);
        else
            _bottom.displayTemporaryMessage(R.string.str_AnotherTurn);
    }

    /**
     * Gets called when they tap and say they they are ready before the other player
     */
    public void waitingForOtherPlayer(Player player, Player otherPlayer) {
        /*if (!GameActivity.IS_TEST) {
            if (player.getBoard().isPlayerA(player))
                _bottom.displayPermanentMessage("Waiting for " + otherPlayer.getName());
            else
                _top.displayPermanentMessage("Waiting for "+otherPlayer.getName());
        }*/
        if (player.getBoard().isPlayerA(player))
            _bottom.displayPermanentMessage("Waiting for " + otherPlayer.getName());
        else
            _top.displayPermanentMessage("Waiting for "+otherPlayer.getName());
    }

    public void countdown(int number) {
        _top.displayPermanentMessage(String.valueOf(number));
        _bottom.displayPermanentMessage(String.valueOf(number));
    }

}
