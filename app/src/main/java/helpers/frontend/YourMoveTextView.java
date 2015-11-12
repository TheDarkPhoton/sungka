package helpers.frontend;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.deathgull.sungka_project.R;

import game.player.Side;


/**
 * Created by martinkubat on 30/10/15.
 */
public class YourMoveTextView extends TextView {
    private Side _side;
    private FrameLayout.LayoutParams _layoutParams;
    private boolean _temporaryMessageDisplayed = false;
    Handler h = new Handler();
    private boolean _isCurrentTurn = true;
    private boolean _isPermanentMessageShown = false;

    /**
     * Constructor
     * @param context of the parent activity
     * @param side that the text view is located in
     */
    public YourMoveTextView(Context context, Side side) {
        super(context);

        _side = side;

        _layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(_layoutParams);

        revertToDefault();
        this.setGravity(Gravity.CENTER);

        Log.i("YourMove", "Text size " + getResources().getDimensionPixelSize(R.dimen.your_turn_text_size));

        this.setTextSize(getResources().getDimensionPixelSize(R.dimen.your_turn_text_size));
        this.setHeight(130);

        switch (_side) {
            case A:
                _layoutParams.gravity = Gravity.BOTTOM;
                this.setTextColor(Color.WHITE);
                break;
            case B:
                _layoutParams.gravity = Gravity.TOP;
                this.setTextColor(Color.BLACK);
                this.setRotation(180);
                break;
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
