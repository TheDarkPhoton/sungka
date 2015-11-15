package com.example.deathgull.sungka_project;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import helpers.frontend.CupButton;
import helpers.frontend.YourMoveTextView;

/**
 * Tests the different modes of play, i.e. Human vs Human; Human vs AI; Human vs Remote.
 */
public class GameActivityModesTest extends ActivityInstrumentationTestCase2<GameActivity> {
    private Bundle bundle;
    private Intent intent;
    private GameActivity activity;
    private YourMoveTextView textP1;
    private YourMoveTextView textP2;

    public GameActivityModesTest() {
        super(GameActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        intent = new Intent();
        bundle = new Bundle();
        bundle.putString(GameActivity.PLAYER_ONE, "Player One");
    }

    private void waitForAnimations() {
        boolean animationRunning = true;

        while (animationRunning) {
            getInstrumentation().waitForIdleSync();
            if (activity.animationFinished()) {
                animationRunning = false;
            }
        }
    }

    private void waitForCountdown() {
        boolean countingDown = true;

        while(countingDown) {
            try {
                if (Integer.valueOf(textP1.getText().toString()) <= 0) {
                    getInstrumentation().waitForIdleSync();
                }
            } catch (NumberFormatException e) {
                try {
                    Thread.sleep(1000, 0);
                } catch (InterruptedException i) {
                    countingDown = false;
                }
                countingDown = false;
            }
        }
    }

    private void initPlayerTexts() {
        textP1 = (YourMoveTextView) activity.findViewById(R.id.moveTextPlayer1);
        textP2 = (YourMoveTextView) activity.findViewById(R.id.moveTextPlayer2);
    }

    public void testFirstMove() {
        bundle.putString(GameActivity.PLAYER_TWO, "Player Two");
        intent.putExtras(bundle);
        setActivityIntent(intent);

        activity = getActivity();
        initPlayerTexts();

        String yourTurn = activity.getResources().getString(R.string.str_YourTurn);
        String tapWhenReady = activity.getResources().getString(R.string.str_tapWhenYourReady);
        String waiting = "Waiting for Player Two";

        assertEquals(tapWhenReady, textP1.getText().toString());
        assertEquals(tapWhenReady, textP2.getText().toString());

        View vTop = activity.findViewById(R.id.readyTopView);
        View vBot = activity.findViewById(R.id.readyBottomView);



        TouchUtils.tapView(this, vBot);
        getInstrumentation().waitForIdleSync();
        assertEquals(waiting, textP1.getText().toString());


        TouchUtils.tapView(this, vTop);
        waitForCountdown();

        assertEquals(yourTurn, textP1.getText().toString());
        assertEquals(yourTurn, textP2.getText().toString());
    }
}
