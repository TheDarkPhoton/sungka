package helpers.frontend;

import android.content.Context;
import android.graphics.Color;
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

    public YourMoveTextView(Context context, Side side) {
        super(context);

        _side = side;

        _layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(_layoutParams);

        this.setText(R.string.str_YourTurn);
        this.setGravity(Gravity.CENTER);
        this.setTextSize(30.0f);
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
     * Shows this view
     */
    public void show() {
        this.setAlpha(1.0f);
    }

    /**
     * Hides this view
     */
    public void hide() {
        this.setAlpha(0.0f);
    }
}
