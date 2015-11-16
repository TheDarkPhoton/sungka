package com.example.deathgull.sungka_project;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Tests the activity in which game stats are stored.
 */
public class StatisticsActivityTest extends ActivityInstrumentationTestCase2<StatisticsActivity> {
    private static final String dummyData = "player_statistics_dummy";
    private static final String emptyData = "player_statistics_empty";
    private static final String TAG = "StatsActivity";

    private View selected;

//    private StatisticsActivity activity;

    public StatisticsActivityTest() {
        super(StatisticsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);
    }

    public void testNoStats() {
        Intent intent = new Intent();
        intent.putExtra(StatisticsActivity.DATA_FILE, emptyData);
        setActivityIntent(intent);

        StatisticsActivity activity = getActivity();

        TextView statsEmptyMsg = (TextView) activity.findViewById(R.id.statsEmptyText);
        assertNotNull(statsEmptyMsg);
    }

    public void testStats() {
        Intent intent = new Intent();
        intent.putExtra(StatisticsActivity.DATA_FILE, dummyData);
        setActivityIntent(intent);

        StatisticsActivity activity = getActivity();

        final Spinner spinner = (Spinner) activity.findViewById(R.id.user_spinner);

        // spinner should have one item for the leaderboard, and two for the players
        assertEquals(3, spinner.getCount());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.requestFocus();
                spinner.setSelection(0);
            }
        });

        getInstrumentation().waitForIdleSync();

        this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
        this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
        this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

        int pos = spinner.getSelectedItemPosition();
        assertEquals(1, pos);
        assertEquals("Chewie", (String) spinner.getItemAtPosition(pos));

        // Chewie has the best W/L ratio, and so should be at the head of the list of stats.
        // BUT, he is the second line in the fake source data.
        PlayerStatistic stats = activity.getStatistics(dummyData).get(1);
        TypedArray ids = activity.getResources().obtainTypedArray(R.array.statsScores);


        TextView numWon = (TextView) activity.findViewById(ids.getResourceId(0,0));
        TextView numLost = (TextView) activity.findViewById(ids.getResourceId(1,0));
        TextView numDrawn = (TextView) activity.findViewById(ids.getResourceId(2,0));
        TextView pctWon = (TextView) activity.findViewById(ids.getResourceId(3,0));
        TextView gamesPlayed = (TextView) activity.findViewById(ids.getResourceId(4,0));
        TextView rank = (TextView) activity.findViewById(ids.getResourceId(5,0));
        TextView avgMoveTime = (TextView) activity.findViewById(ids.getResourceId(6,0));
        TextView maxShells = (TextView) activity.findViewById(ids.getResourceId(7,0));
        TextView maxMoves = (TextView) activity.findViewById(ids.getResourceId(8,0));
        ids.recycle();

        assertEquals(numWon.getText().toString(), String.valueOf(stats.getGamesWon()));
        assertEquals(numLost.getText().toString(), String.valueOf(stats.getGamesLost()));
        assertEquals(numDrawn.getText().toString(), String.valueOf(stats.getGamesDrawn()));
        assertEquals(pctWon.getText().toString(), String.format("%d%%", stats.getWinLossRatioPercentage()));
        assertEquals(gamesPlayed.getText().toString(), String.valueOf(stats.getGamesPlayed()));
        assertEquals(rank.getText().toString(), "1");
        assertEquals(avgMoveTime.getText().toString(), stats.getAverageMoveTime());
        assertEquals(maxShells.getText().toString(), String.valueOf(stats.getMaxNumShellsCollected()));
        assertEquals(maxMoves.getText().toString(), String.valueOf(stats.getMaxConsecutiveMoves()));
    }
}
