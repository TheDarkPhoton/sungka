package com.example.deathgull.sungka_project;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.test.TouchUtils;

import helpers.frontend.CupButton;

/**
 * Tests for what happens when a game ends.
 */
public class GameActivityGameOverTest extends ActivityUnitTestCase<GameActivity> {
    private GameActivity activity;
    private Intent intent;
    private Bundle bundle;

    public GameActivityGameOverTest() {
        super(GameActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        bundle = new Bundle();
        bundle.putString(GameActivity.PLAYER_ONE, "Player One");
        bundle.putString(GameActivity.PLAYER_TWO, "Player Two");
        bundle.putBoolean("is_test", true);

        intent = new Intent(getInstrumentation().getTargetContext(), GameActivity.class);
        intent.putExtras(bundle);
        startActivity(intent, null, null);
    }

    public void testPoop() {
        CupButton cup1_1 = (CupButton) getActivity().findViewById(R.id.cup1_1);
        CupButton cup1_2 = (CupButton) getActivity().findViewById(R.id.cup1_2);
        CupButton cup1_3 = (CupButton) getActivity().findViewById(R.id.cup1_3);
        CupButton cup1_4 = (CupButton) getActivity().findViewById(R.id.cup1_4);
        CupButton cup1_5 = (CupButton) getActivity().findViewById(R.id.cup1_5);
        CupButton cup1_6 = (CupButton) getActivity().findViewById(R.id.cup1_6);
        CupButton cup1_7 = (CupButton) getActivity().findViewById(R.id.cup1_7);
        CupButton cup2_1 = (CupButton) getActivity().findViewById(R.id.cup2_1);
        CupButton cup2_2 = (CupButton) getActivity().findViewById(R.id.cup2_2);
        CupButton cup2_3 = (CupButton) getActivity().findViewById(R.id.cup2_3);
        CupButton cup2_4 = (CupButton) getActivity().findViewById(R.id.cup2_4);
        CupButton cup2_5 = (CupButton) getActivity().findViewById(R.id.cup2_5);
        CupButton cup2_6 = (CupButton) getActivity().findViewById(R.id.cup2_6);
        CupButton cup2_7 = (CupButton) getActivity().findViewById(R.id.cup2_7);

        cup1_2.removeAllShells();
        cup1_3.removeAllShells();
        cup1_4.removeAllShells();
        cup1_5.removeAllShells();
        cup1_6.removeAllShells();
        cup1_7.removeAllShells();
        cup2_1.removeAllShells();
        cup2_2.removeAllShells();
        cup2_3.removeAllShells();
        cup2_4.removeAllShells();
        cup2_5.removeAllShells();
        cup2_6.removeAllShells();
        cup2_7.removeAllShells();

        cup1_1.performClick();
        getInstrumentation().waitForIdleSync();
        cup1_2.removeAllShells();
        cup1_3.removeAllShells();
        cup1_4.removeAllShells();
        cup1_5.removeAllShells();
        cup1_6.removeAllShells();

        getInstrumentation().waitForIdleSync();

        cup1_7.performClick();
        getInstrumentation().waitForIdleSync();

        /*try {
            Thread.sleep(5500, 0);
        } catch (InterruptedException e) {
            System.out.println("poop");
        }*/

        assertEquals("0", cup1_1.getText().toString());
        assertEquals("0", cup1_2.getText().toString());
        assertEquals("0", cup1_3.getText().toString());
        assertEquals("0", cup1_4.getText().toString());
        assertEquals("0", cup1_5.getText().toString());
        assertEquals("0", cup1_6.getText().toString());
        assertEquals("0", cup1_7.getText().toString());
        assertTrue(isFinishCalled());
    }
}
