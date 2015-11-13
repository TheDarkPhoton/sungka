package helpers.frontend;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.deathgull.sungka_project.R;

/**
 * Created by martinkubat on 12/11/15.
 */
public class StatisticsRowView extends LinearLayout {


    public StatisticsRowView(Context context) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        setLayoutParams(layoutParams);

    }

    /**
     * Created by martinkubat on 30/10/15.
     */
    public static class YourMoveTextView extends TextView {
        private FrameLayout.LayoutParams _layoutParams;
        private boolean _temporaryMessageDisplayed = false;
        Handler h = new Handler();
        private boolean _isCurrentTurn = true;
        private boolean _isPermanentMessageShown = false;

        /**
         * Constructor
         * @param context of the parent activity
         */
        public YourMoveTextView(Context context) {
            super(context);

            _layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.setLayoutParams(_layoutParams);

            revertToDefault();
            this.setGravity(Gravity.CENTER);

            Log.i("YourMove", "Text size " + getResources().getDimensionPixelSize(R.dimen.your_turn_text_size));

            this.setTextSize(getResources().getDimensionPixelSize(R.dimen.your_turn_text_size));
            this.setHeight(130);
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
}
