package helpers.frontend;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by martinkubat on 12/11/15.
 */
public class StatisticsColumnView extends LinearLayout {

    public StatisticsColumnView(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setBaselineAligned(false);
        LinearLayout.LayoutParams columnLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        int margin = 40;
        columnLayoutParams.setMargins(margin, margin, margin, margin);

        setLayoutParams(columnLayoutParams);
        setWeightSum(1.5f);
    }
}
