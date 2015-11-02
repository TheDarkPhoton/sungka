package com.example.darkphoton.sungka_project;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.UiThreadTest;
import android.util.Log;

/**
 * Test the game activity.
 */
public class GameActivityTest extends ActivityInstrumentationTestCase2<GameActivity> {

    private static final String TAG = "GameActivity";

    private GameActivity activity;
    private boolean wasAbleToMove;

    public GameActivityTest() {
        super(GameActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);

        activity = getActivity();

        // TODO will need to change things when first player is no longer fixed
    }

    public void testNotNull() {
        assertNotNull(activity);
    }

    public void testCupsExist() {
        CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        CupButton cup1_2 = (CupButton) activity.findViewById(R.id.cup1_2);
        CupButton cup1_3 = (CupButton) activity.findViewById(R.id.cup1_3);
        CupButton cup1_4 = (CupButton) activity.findViewById(R.id.cup1_4);
        CupButton cup1_5 = (CupButton) activity.findViewById(R.id.cup1_5);
        CupButton cup1_6 = (CupButton) activity.findViewById(R.id.cup1_6);
        CupButton cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
        CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        CupButton cup2_1 = (CupButton) activity.findViewById(R.id.cup2_1);
        CupButton cup2_2 = (CupButton) activity.findViewById(R.id.cup2_2);
        CupButton cup2_3 = (CupButton) activity.findViewById(R.id.cup2_3);
        CupButton cup2_4 = (CupButton) activity.findViewById(R.id.cup2_4);
        CupButton cup2_5 = (CupButton) activity.findViewById(R.id.cup2_5);
        CupButton cup2_6 = (CupButton) activity.findViewById(R.id.cup2_6);
        CupButton cup2_7 = (CupButton) activity.findViewById(R.id.cup2_7);
        CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);

        assertNotNull(cup1_store);
        assertNotNull(cup2_store);
        assertNotNull(cup1_1);
        assertNotNull(cup1_2);
        assertNotNull(cup1_3);
        assertNotNull(cup1_4);
        assertNotNull(cup1_5);
        assertNotNull(cup1_6);
        assertNotNull(cup1_7);
        assertNotNull(cup2_1);
        assertNotNull(cup2_2);
        assertNotNull(cup2_3);
        assertNotNull(cup2_4);
        assertNotNull(cup2_5);
        assertNotNull(cup2_6);
        assertNotNull(cup2_7);
    }

    public void testStoreInit() {
        CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);

        assertEquals("0", cup1_store.getText().toString());
        assertEquals("0", cup2_store.getText().toString());
    }

    public void testCupInit() {
        CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        CupButton cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
        CupButton cup2_1 = (CupButton) activity.findViewById(R.id.cup2_1);
        CupButton cup2_7 = (CupButton) activity.findViewById(R.id.cup2_7);

        // testing every cup is overkill. They're made in a loop so let's just check
        // the first and last ones to be made
        assertEquals("7", cup1_1.getText().toString());
        assertEquals("7", cup1_7.getText().toString());
        assertEquals("7", cup2_1.getText().toString());
        assertEquals("7", cup2_7.getText().toString());
    }

    public void testCupMove() {
        final CupButton cup1_4 = (CupButton) activity.findViewById(R.id.cup1_1);
        CupButton cup1_5 = (CupButton) activity.findViewById(R.id.cup1_2);
//        final CupButton cup1_4 = (CupButton) activity.findViewById(R.id.cup1_4);
//        CupButton cup1_5 = (CupButton) activity.findViewById(R.id.cup1_5);
//        CupButton cup1_6 = (CupButton) activity.findViewById(R.id.cup1_6);
//        CupButton cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
//        CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
//        CupButton cup2_1 = (CupButton) activity.findViewById(R.id.cup2_1);
//        CupButton cup2_2 = (CupButton) activity.findViewById(R.id.cup2_2);
//        CupButton cup2_3 = (CupButton) activity.findViewById(R.id.cup2_3);

        Log.i(TAG, "cup1_2 has " + cup1_5.getText().toString());

        Runnable move = new Runnable() {
            @Override
            public void run() {
                cup1_4.performClick();
            }
        };

//        getInstrumentation().runOnMainSync(move);
        activity.runOnUiThread(move);

//        TouchUtils.clickView(this, cup1_4);

        getInstrumentation().waitForIdleSync();
        Log.i(TAG, "cup1_2 has " + cup1_5.getText().toString());

        assertEquals("0", cup1_4.getText().toString());
        assertEquals("8", cup1_5.getText().toString());
//        assertEquals("8", cup1_6.getText().toString());
//        assertEquals("8", cup1_7.getText().toString());
//        assertEquals("1", cup1_store.getText().toString());
//        assertEquals("8", cup2_1.getText().toString());
//        assertEquals("8", cup2_2.getText().toString());
//        assertEquals("8", cup2_3.getText().toString());
    }

    /*public void testCupMove2() {
        final CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        CupButton cup1_6 = (CupButton) activity.findViewById(R.id.cup1_6);
        CupButton cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
        CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);

        getInstrumentation().waitForIdleSync();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cup1_1.performClick();
            }
        });

        getInstrumentation().waitForIdleSync();

        assertEquals("1", cup1_1.getText().toString());
        assertEquals("9", cup1_6.getText().toString());
        assertEquals("8", cup1_7.getText().toString());
        assertEquals("0", cup2_store.getText().toString());
    }

    public void testChangePlayer() {
        final CupButton cup1_4 = (CupButton) activity.findViewById(R.id.cup1_5);
        final CupButton cup1_6 = (CupButton) activity.findViewById(R.id.cup1_6);
        final CupButton cup2_2 = (CupButton) activity.findViewById(R.id.cup2_2);

        Runnable firstMove = new Runnable() {
            @Override
            public void run() {
                cup1_6.performClick();
            }
        };

        Runnable secondMove = new Runnable() {
            @Override
            public void run() {
                wasAbleToMove = cup1_4.performClick();
            }
        };

        Runnable thirdMove = new Runnable() {
            @Override
            public void run() {
                wasAbleToMove = cup2_2.performClick();
            }
        };

        activity.runOnUiThread(firstMove);
        getInstrumentation().waitForIdleSync();

        activity.runOnUiThread(secondMove);
        boolean move = wasAbleToMove;

        getInstrumentation().waitForIdleSync();
        activity.runOnUiThread(thirdMove);

        assertFalse(move);
        assertTrue(wasAbleToMove);


    }

    public void testGetAnotherTurn() {
        final CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        final CupButton cup1_5 = (CupButton) activity.findViewById(R.id.cup1_2);
        final CupButton cup2_2 = (CupButton) activity.findViewById(R.id.cup2_2);

        Runnable firstMove = new Runnable() {
            @Override
            public void run() {
                cup1_1.performClick();
            }
        };

        Runnable secondMove = new Runnable() {
            @Override
            public void run() {
                wasAbleToMove = cup2_2.performClick();
            }
        };

        Runnable thirdMove = new Runnable() {
            @Override
            public void run() {
                wasAbleToMove = cup1_5.performClick();
            }
        };

        activity.runOnUiThread(firstMove);
        getInstrumentation().waitForIdleSync();

        activity.runOnUiThread(secondMove);
        boolean move = wasAbleToMove;

        getInstrumentation().waitForIdleSync();
        activity.runOnUiThread(thirdMove);

        assertFalse(move);
        assertTrue(wasAbleToMove);
    }

    public void testShellCapture() {
        final CupButton cup1_3 = (CupButton) activity.findViewById(R.id.cup1_4);
        CupButton cup2_5 = (CupButton) activity.findViewById(R.id.cup2_5);
        CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cup1_3.performClick();
            }
        });

        getInstrumentation().waitForIdleSync();

        assertEquals("0", cup1_3.getText().toString());
        assertEquals("0", cup2_5.getText().toString());
        assertEquals("10", cup1_store.getText().toString());
    }*/
}
