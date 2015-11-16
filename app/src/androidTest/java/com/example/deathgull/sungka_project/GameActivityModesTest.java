package com.example.deathgull.sungka_project;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.View;

import helpers.frontend.CupButton;
import helpers.frontend.YourMoveTextView;

/**
 * Tests the first move in Human vs Human and Human vs AI modes.
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

    /**
     * Make sure that the text notifications function properly during the simultaneous
     * first move.
     */
    public void testFirstMove_Text() {
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

        TouchUtils.clickView(this, vBot);
        getInstrumentation().waitForIdleSync();
        assertEquals(waiting, textP1.getText().toString());


        TouchUtils.clickView(this, vTop);
        waitForCountdown();

        assertEquals(yourTurn, textP1.getText().toString());
        assertEquals(yourTurn, textP2.getText().toString());
    }

    public void testFirstMove_Move() {
        bundle.putString(GameActivity.PLAYER_TWO, "Player Two");
        intent.putExtras(bundle);
        setActivityIntent(intent);

        activity = getActivity();
        initPlayerTexts();

        String yourTurn = activity.getResources().getString(R.string.str_YourTurn);

        View vTop = activity.findViewById(R.id.readyTopView);
        View vBot = activity.findViewById(R.id.readyBottomView);
        CupButton cup1_3 = (CupButton) activity.findViewById(R.id.cup1_3);
        CupButton cup1_6 = (CupButton) activity.findViewById(R.id.cup1_6);
        CupButton cup2_7 = (CupButton) activity.findViewById(R.id.cup2_7);
        CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);

        TouchUtils.clickView(this, vBot);
        TouchUtils.clickView(this, vTop);
        waitForCountdown();

        TouchUtils.clickView(this, cup1_3);
        TouchUtils.clickView(this, cup2_7);
        waitForAnimations(true);

        assertEquals("1", cup1_3.getText().toString());
        assertEquals("0", cup2_7.getText().toString());
        assertEquals("9", cup1_6.getText().toString());
        assertEquals("1", cup1_store.getText().toString());
        assertEquals("1", cup2_store.getText().toString());
        assertEquals(yourTurn, textP1.getText().toString());
    }

    /**
     * Make sure that first move functionality functions correctly when both players get extra
     * moves on their first turns.
     */
    public void testFirstMove_ExtraMoves() {
        bundle.putString(GameActivity.PLAYER_TWO, "Player Two");
        intent.putExtras(bundle);
        setActivityIntent(intent);

        activity = getActivity();
        initPlayerTexts();

        String yourTurn = activity.getResources().getString(R.string.str_YourTurn);

        View vTop = activity.findViewById(R.id.readyTopView);
        View vBot = activity.findViewById(R.id.readyBottomView);
        CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        CupButton cup1_4 = (CupButton) activity.findViewById(R.id.cup1_4);
        CupButton cup2_1 = (CupButton) activity.findViewById(R.id.cup2_1);
        CupButton cup2_6 = (CupButton) activity.findViewById(R.id.cup2_6);
        CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);

        TouchUtils.clickView(this, vBot);
        TouchUtils.clickView(this, vTop);
        waitForCountdown();

        TouchUtils.clickView(this, cup1_1);
        TouchUtils.clickView(this, cup2_1);

        try {
            Thread.sleep(6000, 0);
        } catch (InterruptedException e) {
            Log.i("test", "held");

        }

        assertEquals(yourTurn, textP1.getText().toString());
        assertEquals(yourTurn, textP2.getText().toString());


        TouchUtils.clickView(this, cup1_4);
        TouchUtils.clickView(this, cup2_6);
        waitForAnimations(true);

        assertEquals("1", cup1_4.getText().toString());
        assertEquals("1", cup1_1.getText().toString());
        assertEquals("1", cup2_1.getText().toString());
        assertEquals("0", cup2_6.getText().toString());
        assertEquals("2", cup1_store.getText().toString());
        assertEquals("2", cup2_store.getText().toString());

        assertEquals(yourTurn, textP1.getText().toString());
        assertNotSame(yourTurn, textP2.getText().toString());

        // make sure that player 1 has the current turn

        TouchUtils.clickView(this, cup1_4);
        waitForAnimations(false);

        assertEquals("0", cup1_4.getText().toString());
    }

    public void testAiGameStart() {
        bundle.putString(GameActivity.PLAYER_TWO, "AI Player");
        bundle.putInt(GameActivity.AI_DIFF, 50);
        intent.putExtras(bundle);
        setActivityIntent(intent);

        activity = getActivity();
        initPlayerTexts();

        String tapWhenReady = activity.getResources().getString(R.string.str_tapWhenYourReady);
        String waiting = "Waiting for Player One";

        assertEquals(tapWhenReady, textP1.getText().toString());
        assertEquals(waiting, textP2.getText().toString());

        View vBot = activity.findViewById(R.id.readyBottomView);
        CupButton cup1_3 = (CupButton) activity.findViewById(R.id.cup1_3);
        CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);

        TouchUtils.clickView(this, vBot);
        waitForCountdown();

        TouchUtils.clickView(this, cup1_3);
        waitForAnimations(true);

        assertEquals("1", cup1_store.getText().toString());
        assertEquals("1", cup2_store.getText().toString());
    }

    private void waitForAnimations(boolean isFirstTurn) {
        boolean animationRunning = true;

        while (animationRunning) {
            getInstrumentation().waitForIdleSync();
            if (activity.animationFinished(isFirstTurn)) {
                animationRunning = false;
            }
        }
    }

    private void waitForCountdown() {
        boolean countingDown = true;

        while(countingDown) {
            try {
                if (Integer.valueOf(textP1.getText().toString()) >= 0) {
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
}
