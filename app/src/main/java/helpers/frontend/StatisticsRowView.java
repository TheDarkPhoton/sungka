package helpers.frontend;

import android.content.Context;
import android.widget.LinearLayout;

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
