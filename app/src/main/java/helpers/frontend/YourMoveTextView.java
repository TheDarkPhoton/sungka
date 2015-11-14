package helpers.frontend;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.deathgull.sungka_project.R;

import game.player.Player;

public class YourMoveTextView extends TextView {
    private FrameLayout.LayoutParams _layoutParams;
    private boolean _temporaryMessageDisplayed = false;
    Handler h = new Handler();
    private boolean _isCurrentTurn = true;
    private boolean _isPermanentMessageShown = false;
    private Player _player;


    /**
     * Constructor
     * @param context of the parent activity
     */
    public YourMoveTextView(Context context, boolean isTop) {
        super(context);

        _layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        this.setLayoutParams(_layoutParams);

        this.setText(R.string.str_tapWhenYourReady);

        this.setGravity(Gravity.CENTER);

        this.setTextSize(getResources().getDimensionPixelSize(R.dimen.your_turn_text_size));
        this.setHeight(130);

        if (isTop) {
            _layoutParams.gravity = Gravity.TOP;
            setTextColor(Color.BLACK);
            setRotation(180);
        } else {
            _layoutParams.gravity = Gravity.BOTTOM;
            setTextColor(Color.WHITE);
        }
    }


    /**
     * Momentarily display a message
     * @param stringResource of the string to display
     */
    public void displayTemporaryMessage(@StringRes int stringResource) {
        setText(stringResource);

        _temporaryMessageDisplayed = true;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!_isPermanentMessageShown) {
                    revertToDefault();
                }

                _temporaryMessageDisplayed = false;

            }
        }, 3000);


    }

    /**
     * Permanently display a message
     * @param stringResource of the string to display
     */
    public void displayPermanentMessage(@StringRes int stringResource) {
        setText(stringResource);
    }

    public void displayPermanentMessage(String string) {
        setText(string);
    }

    /**
     * Display the "YOUR TURN" label
     */
    private void revertToDefault() {
        if (_isCurrentTurn) {
            setText(R.string.str_YourTurn);
        } else {
            setText("");
        }
    }

    /**
     * Set if the side that the textview is located in is currently playing
     * @param isCurrentTurn
     */
    public void setIsCurrentTurn(boolean isCurrentTurn) {
        _isCurrentTurn = isCurrentTurn;
        if (!_temporaryMessageDisplayed) {
            revertToDefault();
        }
    }
}
