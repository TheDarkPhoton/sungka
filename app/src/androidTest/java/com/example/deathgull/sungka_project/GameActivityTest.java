package com.example.deathgull.sungka_project;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.Button;

import helpers.frontend.CupButton;
import helpers.frontend.YourMoveTextView;

/**
 * Test the game activity. All these tests skip the simultaneous first move and assume a
 * Human vs Human game.
 */
public class GameActivityTest extends ActivityInstrumentationTestCase2<GameActivity> {
    private GameActivity activity;
    private YourMoveTextView textP1;
    private YourMoveTextView textP2;

    public GameActivityTest() {
        super(GameActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Bundle bundle = new Bundle();
        bundle.putString(GameActivity.PLAYER_ONE, "Player One");
        bundle.putString(GameActivity.PLAYER_TWO, "Player Two");
        bundle.putBoolean("is_test", true);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setActivityIntent(intent);

        setActivityInitialTouchMode(true);

        activity = getActivity();
        textP1 = (YourMoveTextView) activity.findViewById(R.id.moveTextPlayer1);
        textP2 = (YourMoveTextView) activity.findViewById(R.id.moveTextPlayer2);
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

    /**
     * Make sure that the GameActivity is being created properly.
     */
    public void testActivityExists() {
        assertNotNull(activity);
    }

    /**
     * Make sure that the CupButtons are being made properly.
     */
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

        assertNotNull(cup1_store); assertNotNull(cup2_store);
        assertNotNull(cup1_1); assertNotNull(cup1_2);
        assertNotNull(cup1_3); assertNotNull(cup1_4);
        assertNotNull(cup1_5); assertNotNull(cup1_6);
        assertNotNull(cup1_7); assertNotNull(cup2_1);
        assertNotNull(cup2_2); assertNotNull(cup2_3);
        assertNotNull(cup2_4); assertNotNull(cup2_5);
        assertNotNull(cup2_6); assertNotNull(cup2_7);
    }

    /**
     * Make sure that stores are initiated with 0 shells inside.
     */
    public void testStoreInit() {
        CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);

        assertEquals("0", cup1_store.getText().toString());
        assertEquals("0", cup2_store.getText().toString());
    }

    /**
     * Make sure that ShellCups are being initiated with the correct number of shells inside.
     */
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

    /**
     * Make sure that regular move behaviour is working properly - i.e. does the selected cup
     * end up being empty, and have a number of cups equal to the number of shells picked up
     * had their shell count incremented by 1?
     */
    public void testCupMove() {
        CupButton cup1_4 = (CupButton) activity.findViewById(R.id.cup1_4);
        CupButton cup1_5 = (CupButton) activity.findViewById(R.id.cup1_5);
        CupButton cup1_6 = (CupButton) activity.findViewById(R.id.cup1_6);
        CupButton cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
        CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        CupButton cup2_1 = (CupButton) activity.findViewById(R.id.cup2_1);
        CupButton cup2_2 = (CupButton) activity.findViewById(R.id.cup2_2);
        CupButton cup2_3 = (CupButton) activity.findViewById(R.id.cup2_3);
        CupButton cup2_4 = (CupButton) activity.findViewById(R.id.cup2_4);

        String yourTurn = activity.getResources().getString(R.string.str_YourTurn);

        assertEquals(yourTurn, textP1.getText().toString());

        TouchUtils.clickView(this, cup1_4);

        waitForAnimations();

        assertEquals(yourTurn, textP2.getText().toString());
        assertEquals("0", cup1_4.getText().toString());
        assertEquals("8", cup1_5.getText().toString());
        assertEquals("8", cup1_6.getText().toString());
        assertEquals("8", cup1_7.getText().toString());
        assertEquals("1", cup1_store.getText().toString());
        assertEquals("8", cup2_1.getText().toString());
        assertEquals("8", cup2_2.getText().toString());
        assertEquals("8", cup2_3.getText().toString());
        assertEquals("7", cup2_4.getText().toString());
    }

    /**
     * As above, but in this case we are interested in whether the opponent's store was
     * skipped over when dropping shells.
     */
    public void testCupMove2() {
        final CupButton cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
        CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);
        CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        CupButton cup1_2 = (CupButton) activity.findViewById(R.id.cup1_2);

        // set cup1_7 to have 9 shells
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cup1_7.addShellImages(activity, 2);
            }
        });
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, cup1_7);

        waitForAnimations();

        assertEquals("0", cup2_store.getText().toString());
        assertEquals("8", cup1_1.getText().toString());
        assertEquals("7", cup1_2.getText().toString());
    }

    /**
     * Make sure that when the last shell drops in the player's store, the opponent cannot
     * make a move, but the original player can.
     */
    public void testPlayerGetsAnotherMove() {
        CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        CupButton cup2_3 = (CupButton) activity.findViewById(R.id.cup2_3);
        CupButton cup1_4 = (CupButton) activity.findViewById(R.id.cup1_4);

        TouchUtils.clickView(this, cup1_1);
        waitForAnimations();

        // no move should happen
        TouchUtils.clickView(this, cup2_3);
        waitForAnimations();

        assertEquals("7", cup2_3.getText().toString());

        String anotherTurn = activity.getResources().getString(R.string.str_AnotherTurn);
        assertEquals(anotherTurn, textP1.getText().toString());

        TouchUtils.clickView(this, cup1_4);
        waitForAnimations();


        assertEquals("0", cup1_4.getText().toString());
    }

    /**
     * Make sure that when the last shell drops in an empty cup belonging to the player, that
     * shell plus the shells in the cup opposite are transferred into the store.
     */
    public void testRobOpponent() {
        final CupButton cup1_3 = (CupButton) activity.findViewById(R.id.cup1_3);
        final CupButton cup1_5 = (CupButton) activity.findViewById(R.id.cup1_5);
        CupButton cup2_3 = (CupButton) activity.findViewById(R.id.cup2_3);
        CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);

        // set cup1_3 to have 2 shells
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cup1_3.removeAllShells();
                cup1_5.removeAllShells();
                cup1_3.addShellImages(activity, 2);
            }
        });
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, cup1_3);
        waitForAnimations();

        String youRobbed = activity.getResources().getString(R.string.str_YouRobbedOtherPlayer);
        String youWereRobbed = activity.getResources().getString(R.string.str_YouWereRobbed);

        assertEquals(youRobbed, textP1.getText().toString());
        assertEquals(youWereRobbed, textP2.getText().toString());

        assertEquals("8", cup1_store.getText().toString());
        assertEquals("0", cup1_5.getText().toString());
        assertEquals("0", cup2_3.getText().toString());
    }

    /**
     * As above, but the initiating and ending cup are the same.
     */
    public void testRobOpponent2() {
        final CupButton cup1_3 = (CupButton) activity.findViewById(R.id.cup1_3);
        CupButton cup2_5 = (CupButton) activity.findViewById(R.id.cup2_5);
        CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);

        // set cup1_3 to have 15 shells
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cup1_3.addShellImages(activity, 8);
            }
        });
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, cup1_3);
        waitForAnimations();

        assertEquals("0", cup2_5.getText().toString());
        assertEquals("0", cup1_3.getText().toString());
        assertEquals("10", cup1_store.getText().toString());
    }

    /**
     * Make sure that when all of the shell cups belonging to one player are empty, the other
     * player can keep moving.
     */
    public void testCupsEmptyExtraMove() {
        final CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        final CupButton cup1_2 = (CupButton) activity.findViewById(R.id.cup1_2);
        final CupButton cup1_3 = (CupButton) activity.findViewById(R.id.cup1_3);
        final CupButton cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
        final CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        final CupButton cup2_1 = (CupButton) activity.findViewById(R.id.cup2_1);
        final CupButton cup2_2 = (CupButton) activity.findViewById(R.id.cup2_2);
        final CupButton cup2_3 = (CupButton) activity.findViewById(R.id.cup2_3);
        final CupButton cup2_4 = (CupButton) activity.findViewById(R.id.cup2_4);
        final CupButton cup2_5 = (CupButton) activity.findViewById(R.id.cup2_5);
        final CupButton cup2_6 = (CupButton) activity.findViewById(R.id.cup2_6);
        final CupButton cup2_7 = (CupButton) activity.findViewById(R.id.cup2_7);
        final CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cup1_1.removeAllShells();
                cup1_2.removeAllShells();
                cup1_3.removeAllShells();
                cup2_1.removeAllShells();
                cup2_2.removeAllShells();
                cup2_3.removeAllShells();
                cup2_4.removeAllShells();
                cup2_5.removeAllShells();
                cup2_6.removeAllShells();
                cup2_7.removeAllShells();
                cup2_store.addShellImages(activity, 20);
                cup1_store.addShellImages(activity, 20);
                cup1_1.addShellImages(activity, 6);
                cup1_2.addShellImages(activity, 4);
                cup1_3.addShellImages(activity, 2);
            }
        });
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, cup1_1);
        waitForAnimations();

        String anotherTurn = activity.getResources().getString(R.string.str_AnotherTurn);
        assertEquals(anotherTurn, textP1.getText().toString());

        TouchUtils.clickView(this, cup1_2);
        waitForAnimations();

        TouchUtils.clickView(this, cup1_3);
        waitForAnimations();

        assertEquals("0", cup1_1.getText().toString());
        assertEquals("0", cup1_2.getText().toString());
        assertEquals("0", cup1_3.getText().toString());
        assertEquals("10", cup1_7.getText().toString());
    }

    /**
     * Make sure that when a move that ends in a store (normally giving that player another turn)
     * leaves all other shell cups empty, the turn transfers to the other player.
     */
    public void testCupsEmptyNoExtraMove() {
        final CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        final CupButton cup1_2 = (CupButton) activity.findViewById(R.id.cup1_2);
        final CupButton cup1_3 = (CupButton) activity.findViewById(R.id.cup1_3);
        final CupButton cup1_4 = (CupButton) activity.findViewById(R.id.cup1_4);
        final CupButton cup1_5 = (CupButton) activity.findViewById(R.id.cup1_5);
        final CupButton cup1_6 = (CupButton) activity.findViewById(R.id.cup1_6);
        final CupButton cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
        final CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        final CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);
        CupButton cup2_1 = (CupButton) activity.findViewById(R.id.cup2_1);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cup2_store.addShellImages(activity, 20);
                cup1_store.addShellImages(activity, 20);
                cup1_1.removeAllShells();
                cup1_2.removeAllShells();
                cup1_3.removeAllShells();
                cup1_4.removeAllShells();
                cup1_5.removeAllShells();
                cup1_6.removeAllShells();
                cup1_7.removeAllShells();
                cup1_7.addShellImages(activity, 1);
            }
        });
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, cup1_7);
        waitForAnimations();

        TouchUtils.clickView(this, cup2_1);
        waitForAnimations();

        assertEquals("0", cup2_1.getText().toString());
    }

    /**
     * Make sure that when all the shells are in the stores, the "return to menu" button becomes
     * visible and the player's move texts update to reflect who won/lost.
     */
    public void testGameOver() {
        final CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        final CupButton cup1_2 = (CupButton) activity.findViewById(R.id.cup1_2);
        final CupButton cup1_3 = (CupButton) activity.findViewById(R.id.cup1_3);
        final CupButton cup1_4 = (CupButton) activity.findViewById(R.id.cup1_4);
        final CupButton cup1_5 = (CupButton) activity.findViewById(R.id.cup1_5);
        final CupButton cup1_6 = (CupButton) activity.findViewById(R.id.cup1_6);
        final CupButton cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
        final CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        final CupButton cup2_1 = (CupButton) activity.findViewById(R.id.cup2_1);
        final CupButton cup2_2 = (CupButton) activity.findViewById(R.id.cup2_2);
        final CupButton cup2_3 = (CupButton) activity.findViewById(R.id.cup2_3);
        final CupButton cup2_4 = (CupButton) activity.findViewById(R.id.cup2_4);
        final CupButton cup2_5 = (CupButton) activity.findViewById(R.id.cup2_5);
        final CupButton cup2_6 = (CupButton) activity.findViewById(R.id.cup2_6);
        final CupButton cup2_7 = (CupButton) activity.findViewById(R.id.cup2_7);
        final CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cup1_1.removeAllShells();
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
                cup1_store.addShellImages(activity, 67);
                cup2_store.addShellImages(activity, 30);
                cup1_7.addShellImages(activity, 1);
            }
        });
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, cup1_7);
        waitForAnimations();

        String winText = activity.getResources().getString(R.string.str_YouWon);
        String loseText = activity.getResources().getString(R.string.str_YouLost);

        assertEquals(winText, textP1.getText().toString());
        assertEquals(loseText, textP2.getText().toString());

        Button btnReturn = (Button) activity.findViewById(R.id.btn_ReturnToMenu);
        assertEquals(View.VISIBLE, btnReturn.getVisibility());
    }

    /**
     * Make sure that when a robbing move also ends the game, the game correctly identifies this
     * as being game over.
     */
    public void testGameOverOnRob() {
        final CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        final CupButton cup1_2 = (CupButton) activity.findViewById(R.id.cup1_2);
        final CupButton cup1_3 = (CupButton) activity.findViewById(R.id.cup1_3);
        final CupButton cup1_4 = (CupButton) activity.findViewById(R.id.cup1_4);
        final CupButton cup1_5 = (CupButton) activity.findViewById(R.id.cup1_5);
        final CupButton cup1_6 = (CupButton) activity.findViewById(R.id.cup1_6);
        final CupButton cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
        final CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        final CupButton cup2_1 = (CupButton) activity.findViewById(R.id.cup2_1);
        final CupButton cup2_2 = (CupButton) activity.findViewById(R.id.cup2_2);
        final CupButton cup2_3 = (CupButton) activity.findViewById(R.id.cup2_3);
        final CupButton cup2_4 = (CupButton) activity.findViewById(R.id.cup2_4);
        final CupButton cup2_5 = (CupButton) activity.findViewById(R.id.cup2_5);
        final CupButton cup2_6 = (CupButton) activity.findViewById(R.id.cup2_6);
        final CupButton cup2_7 = (CupButton) activity.findViewById(R.id.cup2_7);
        final CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cup1_1.removeAllShells();
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
                cup1_store.addShellImages(activity, 66);
                cup2_store.addShellImages(activity, 30);
                cup1_3.addShellImages(activity, 1);
                cup2_4.addShellImages(activity, 1);
            }
        });
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, cup1_3);
        waitForAnimations();

        String winText = activity.getResources().getString(R.string.str_YouWon);
        String loseText = activity.getResources().getString(R.string.str_YouLost);

        assertEquals(winText, textP1.getText().toString());
        assertEquals(loseText, textP2.getText().toString());

        Button btnReturn = (Button) activity.findViewById(R.id.btn_ReturnToMenu);
        assertEquals(View.VISIBLE, btnReturn.getVisibility());
    }

    /**
     * Make sure that when the game ends with both stores having equal numbers of shells,
     * the game correctly displays the outcome as a draw.
     */
    public void testGameOverDraw() {
        final CupButton cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        final CupButton cup1_2 = (CupButton) activity.findViewById(R.id.cup1_2);
        final CupButton cup1_3 = (CupButton) activity.findViewById(R.id.cup1_3);
        final CupButton cup1_4 = (CupButton) activity.findViewById(R.id.cup1_4);
        final CupButton cup1_5 = (CupButton) activity.findViewById(R.id.cup1_5);
        final CupButton cup1_6 = (CupButton) activity.findViewById(R.id.cup1_6);
        final CupButton cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
        final CupButton cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        final CupButton cup2_1 = (CupButton) activity.findViewById(R.id.cup2_1);
        final CupButton cup2_2 = (CupButton) activity.findViewById(R.id.cup2_2);
        final CupButton cup2_3 = (CupButton) activity.findViewById(R.id.cup2_3);
        final CupButton cup2_4 = (CupButton) activity.findViewById(R.id.cup2_4);
        final CupButton cup2_5 = (CupButton) activity.findViewById(R.id.cup2_5);
        final CupButton cup2_6 = (CupButton) activity.findViewById(R.id.cup2_6);
        final CupButton cup2_7 = (CupButton) activity.findViewById(R.id.cup2_7);
        final CupButton cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cup1_1.removeAllShells();
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
                cup1_store.addShellImages(activity, 48);
                cup2_store.addShellImages(activity, 49);
                cup1_7.addShellImages(activity, 1);
            }
        });
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, cup1_7);
        waitForAnimations();

        String drawText = activity.getResources().getString(R.string.str_ItsADraw);

        assertEquals(drawText, textP1.getText().toString());
        assertEquals(drawText, textP2.getText().toString());

        Button btnReturn = (Button) activity.findViewById(R.id.btn_ReturnToMenu);
        assertEquals(View.VISIBLE, btnReturn.getVisibility());
    }
}
