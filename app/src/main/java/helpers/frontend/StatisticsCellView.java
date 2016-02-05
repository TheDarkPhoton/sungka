package helpers.frontend;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by martinkubat on 12/11/15.
 */
public class StatisticsCellView extends LinearLayout {


    private TextView labelTextView;
    private TextView valueTextView;

    public StatisticsCellView(Context context) {
        super(context);

        LayoutParams cellLayoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        cellLayoutParams.weight = 0.5f;

        this.setOrientation(LinearLayout.VERTICAL);

        this.setGravity(Gravity.CENTER);
        this.setLayoutParams(cellLayoutParams);

        // Label
        labelTextView = new TextView(context);
        TableLayout.LayoutParams labelParams = new TableLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        labelTextView.setLayoutParams(labelParams);

        labelTextView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 7);
        labelTextView.setTextColor(Color.BLACK);
        this.addView(labelTextView);

        // Value
        valueTextView = new TextView(context);
        TableLayout.LayoutParams valueParams = new TableLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        valueTextView.setLayoutParams(valueParams);

        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 27);
        valueTextView.setTextColor(Color.parseColor("#50E3C2"));
        this.addView(valueTextView);

    }


    public TextView getValueTextViews() {
        return valueTextView;
    }


    public TextView getLabelTextView() {
        return labelTextView;
    }

}
