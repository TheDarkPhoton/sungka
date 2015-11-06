package com.example.deathgull.sungka_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class StatisticsActivity extends AppCompatActivity {

    private TextView _averageMoveTimeTextView;
    private TextView _highestCaptureInOneTurnText;
    private TextView _averageScorePerGameText;
    private TextView _highestNumberOfConsecutiveTurns;
    private Spinner _playerSpinner;
    private String[] _exampleArrayOfPlayersPleaseIntegrateWithPlayerModelInTheFuture = {"Player 1", "Player 2", "Player 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        _averageMoveTimeTextView = (TextView) findViewById(R.id.average_move_time_value_text);
        _highestCaptureInOneTurnText = (TextView) findViewById(R.id.highest_capture_in_one_turn_value_text);
        _averageScorePerGameText = (TextView) findViewById(R.id.average_score_per_game_value_text);
        _highestNumberOfConsecutiveTurns = (TextView) findViewById(R.id.highest_capture_in_one_turn_value_text);

        _playerSpinner = (Spinner) findViewById(R.id.user_spinner);

        setupSpinner();
    }

    /**
     * Sets up the spinner
     */
    public void setupSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, _exampleArrayOfPlayersPleaseIntegrateWithPlayerModelInTheFuture);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_layout);
        _playerSpinner.setAdapter(spinnerAdapter);
    }

}
