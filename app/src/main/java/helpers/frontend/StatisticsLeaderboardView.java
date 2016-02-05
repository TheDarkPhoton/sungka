package helpers.frontend;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import game.player.PlayerStatistic;

import java.util.ArrayList;


/**
 * Created by martinkubat on 12/11/15.
 */
public class StatisticsLeaderboardView extends LinearLayout {

    private StatisticsOnPlayerClickListener onPlayerClickListener;

    public StatisticsLeaderboardView(Context context, final ArrayList<PlayerStatistic> playerStatistics) {
        super(context);

        setOrientation(VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        setLayoutParams(layoutParams);

        layoutParams.gravity = Gravity.CENTER;
        setGravity(Gravity.CENTER);

        int ranking = 1;
        for (final PlayerStatistic playerStatistic: playerStatistics) {
            TextView textView = new TextView(context);
            textView.setText(ranking + ". " + playerStatistic.getPlayerName());
            textView.setTextColor(Color.BLACK);

            int greenColor = Color.argb((int) 255 / (ranking + 1), 80, 227, 194);

            textView.setBackgroundColor(greenColor);
            textView.setWidth(100);


            int padding = 20;
            textView.setPadding(padding,padding,padding,padding);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 16);
            addView(textView);

            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPlayerClickListener != null)
                        onPlayerClickListener.didTapOnPlayer(playerStatistics.indexOf(playerStatistic));
                }
            });

            ranking++;
        }
    }

    /**
     * Sets a listener for when a player is selected
     * @param onPlayerClickListener 
     */
    public void setOnPlayerClickListener(StatisticsOnPlayerClickListener onPlayerClickListener) {
        this.onPlayerClickListener = onPlayerClickListener;
    }


}
