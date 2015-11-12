package helpers;

import android.content.Context;
import android.widget.FrameLayout;

import com.example.deathgull.sungka_project.R;

import game.player.Player;
import game.player.Side;
import helpers.frontend.YourMoveTextView;

/**
 * Created by martinkubat on 07/11/15.
 */
public class MessageManager {
    private YourMoveTextView[] _textViews = new YourMoveTextView[2];

    private FrameLayout _parent;
    private int _currentSideIndex;

    /**
     * Constructor
     * @param context of the activity
     * @param parent that the labels will be inset in
     */
    public MessageManager(Context context, FrameLayout parent) {
        _parent = parent;

        for (int i = 0; i < 2; i++) {
            _textViews[i] = new YourMoveTextView(context, Side.values()[i]);
            _parent.addView(_textViews[i]);
        }

        _parent = parent;

    }

    /**
     * Called when a player starts a move
     * @param player that is currently playing
     */
    public void onMoveStart(Player player) {
        if (_currentSideIndex == player.getSide().ordinal())
            return;

        _currentSideIndex = player.getSide().ordinal();

        _textViews[_currentSideIndex].setText(R.string.str_YourTurn);
        _textViews[_currentSideIndex].setIsCurrentTurn(true);

        _textViews[1 - _currentSideIndex].setIsCurrentTurn(false);

    }

    /**
     * Called when the game is over
     * @param isDraw whether the game resulted in a draw
     * @param winningPlayer the winning player
     */
    public void gameOver(boolean isDraw, Player winningPlayer) {

        if (isDraw) {
            _textViews[0].displayPermanentMessage(R.string.str_ItsADraw);
            _textViews[1].displayPermanentMessage(R.string.str_ItsADraw);
        } else {
            int winningPlayerSideIndex = winningPlayer.getSide().ordinal();
            int losingPlayerSideIndex = 1 - winningPlayerSideIndex;

            _textViews[winningPlayerSideIndex].displayPermanentMessage(R.string.str_YouWon);
            _textViews[losingPlayerSideIndex].displayPermanentMessage(R.string.str_YouLost);

        }


    }

    /**
     * Called when player gets another turn
     * @param player that gets another turn
     */
    public void playerGetsAnotherTurn(Player player) {
        _textViews[player.getSide().ordinal()].displayTemporaryMessage(R.string.str_AnotherTurn);

    }

    /**
     * Called when player got robbed
     * @param player that got robbed
     */
    public void playerGotRobbed(Player player) {
        _textViews[player.getSide().ordinal()].displayTemporaryMessage(R.string.str_YouWereRobbed);
        _textViews[1 - player.getSide().ordinal()].displayTemporaryMessage(R.string.str_YouRobbedOtherPlayer);
    }

    /**
     * Called when player got robbed of his final move
     * @param player that got robbed of his final move
     */
    public void playerGotRobbedOfHisFinalMove(Player player) {
        _textViews[player.getSide().ordinal()].displayTemporaryMessage(R.string.str_YouWereRobbed);
    }

}
