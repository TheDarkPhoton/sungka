package helpers.frontend;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.deathgull.sungka_project.R;

public class YourMoveTextView extends TextView {

    public YourMoveTextView(Context context, boolean playerA) {
        super(context);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);

        this.setText(R.string.str_YourTurn);
        this.setGravity(Gravity.CENTER);
        this.setTextSize(30.0f);
        this.setHeight(130);

        if (playerA){
            layoutParams.gravity = Gravity.BOTTOM;
            this.setTextColor(Color.WHITE);
        } else {
            layoutParams.gravity = Gravity.TOP;
            this.setTextColor(Color.BLACK);
            this.setRotation(180);
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
