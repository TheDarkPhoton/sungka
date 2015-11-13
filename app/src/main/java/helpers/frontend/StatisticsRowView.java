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
}
