package com.example.deathgull.sungka_project;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;

import game.player.Player;
import helpers.frontend.StatisticsCellView;
import helpers.frontend.StatisticsColumnView;
import helpers.frontend.StatisticsRowView;

public class StatisticsActivity extends Activity {

    private ScrollView _contentLayout;
    private TextView _averageMoveTimeTextView;
    private TextView _highestCaptureInOneTurnText;
    private TextView _averageScorePerGameText;
    private TextView _highestNumberOfConsecutiveTurns;
    private Spinner _playerSpinner;
    private int previousSpinnerIndex;
    TextView[] labelTextViews = new TextView[9];
    TextView[] valueTextViews = new TextView[9];

    private ArrayList<PlayerStatistic> _playerStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


        _contentLayout = (ScrollView) findViewById(R.id.content_layout);

        _playerSpinner = (Spinner) findViewById(R.id.user_spinner);

        // Get data
        _playerStatistics = getStatistics();
        Collections.sort(_playerStatistics);

        setupSpinner();
//        setupScoreView();
        previousSpinnerIndex = 0;
    }

    /**
     * Sets up the spinner
     */
    public void setupSpinner() {
        // Create an array of player names
        String[] playerNames = new String[_playerStatistics.size() + 1];
        playerNames[0] = "All Players";

        for (int i = 0; i < _playerStatistics.size(); i++) {
            playerNames[i + 1] = _playerStatistics.get(i).getPlayerName();
        }

        // Setup spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, playerNames);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_layout);
        _playerSpinner.setAdapter(spinnerAdapter);
        _playerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateDataByIndex(position);
                previousSpinnerIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Populate data by index
     * @param fakeIndex of the player in the spinner
     */
    private void populateDataByIndex(int fakeIndex) {
        if (fakeIndex == 0) {
            // Setup leaderboard
            if (previousSpinnerIndex != 0)
                setupLeaderboard();

        } else {
            if (previousSpinnerIndex == 0)
                setupScoreView();

            // Show scores
            int index = fakeIndex - 1;

            LinkedHashMap<String, Object> playerStatistics = new LinkedHashMap<>(9);

            playerStatistics.put("Games Won", _playerStatistics.get(index).getGamesWon());
            playerStatistics.put("Games Lost", _playerStatistics.get(index).getGamesLost());
            playerStatistics.put("Games Drawn", _playerStatistics.get(index).getGamesDrawn());
            playerStatistics.put("Percentage of Wins", _playerStatistics.get(index).getWinLossRatioPercentage() + "%");
            playerStatistics.put("Games Played", _playerStatistics.get(index).getGamesPlayed());
            playerStatistics.put("Ranking", calculateRankingForIndex(index));
            playerStatistics.put("Average Move Time", _playerStatistics.get(index).getAverageMoveTime());
            playerStatistics.put("Maximum Number of Shells Collected", _playerStatistics.get(index).getMaxNumShellsCollected());
            playerStatistics.put("Maximum Consecutive Moves", _playerStatistics.get(index).getMaxConsecutiveMoves());


            int a = 0;
            for (Map.Entry<String, Object> entry: playerStatistics.entrySet()) {
                labelTextViews[a].setText(entry.getKey());
                valueTextViews[a].setText("" + entry.getValue());
                a++;
            }
        }
    }

    /**
     * Setup the score view
     */
    private void setupScoreView() {
        Log.i("StatisticsActivity", "setupScoreView");

        // Just in case
        _contentLayout.removeAllViews();

        _contentLayout.setBackgroundColor(Color.BLACK);

        LinearLayout rowLayout = new StatisticsRowView(this);
        _contentLayout.addView(rowLayout);

        for (int i = 0; i < 3; i++) {

            LinearLayout columnLayout = new StatisticsColumnView(this);
            rowLayout.addView(columnLayout);

            for (int j = 0; j < 3; j++) {
                StatisticsCellView elementLayout = new StatisticsCellView(this);
                columnLayout.addView(elementLayout);

                labelTextViews[i * 3 + j] = elementLayout.getLabelTextView();
                valueTextViews[i * 3 + j] = elementLayout.getValueTextViews();
            }
        }




    }

    /**
     * Setup the leaderboard
     */
    private void setupLeaderboard() {
        // TODO
    }

    private Number calculateRankingForIndex(int index) {
        return index + 1;

    }


    /**
     * Populate data with a user's statistics.
     */

    public ArrayList<PlayerStatistic> getStatistics(){
        return GameActivity.readStats(getApplicationContext());
    }

}
