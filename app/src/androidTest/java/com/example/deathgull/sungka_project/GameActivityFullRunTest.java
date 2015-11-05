package com.example.deathgull.sungka_project;

import android.test.SingleLaunchActivityTestCase;
import android.test.TouchUtils;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import helpers.CupButton;

/**
 * Play a full game of sungka, running tests along the way.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GameActivityFullRunTest extends SingleLaunchActivityTestCase<GameActivity> {

    private GameActivity activity;
    private CupButton cup1_1;
    private CupButton cup1_2;
    private CupButton cup1_3;
    private CupButton cup1_4;
    private CupButton cup1_5;
    private CupButton cup1_6;
    private CupButton cup1_7;
    private CupButton cup1_store;
    private CupButton cup2_1;
    private CupButton cup2_2;
    private CupButton cup2_3;
    private CupButton cup2_4;
    private CupButton cup2_5;
    private CupButton cup2_6;
    private CupButton cup2_7;
    private CupButton cup2_store;

    public GameActivityFullRunTest() {
        super("com.example.deathgull.sungka_project", GameActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        activity = getActivity();

        cup1_1 = (CupButton) activity.findViewById(R.id.cup1_1);
        cup1_2 = (CupButton) activity.findViewById(R.id.cup1_2);
        cup1_3 = (CupButton) activity.findViewById(R.id.cup1_3);
        cup1_4 = (CupButton) activity.findViewById(R.id.cup1_4);
        cup1_5 = (CupButton) activity.findViewById(R.id.cup1_5);
        cup1_6 = (CupButton) activity.findViewById(R.id.cup1_6);
        cup1_7 = (CupButton) activity.findViewById(R.id.cup1_7);
        cup1_store = (CupButton) activity.findViewById(R.id.cup1_store);
        cup2_1 = (CupButton) activity.findViewById(R.id.cup2_1);
        cup2_2 = (CupButton) activity.findViewById(R.id.cup2_2);
        cup2_3 = (CupButton) activity.findViewById(R.id.cup2_3);
        cup2_4 = (CupButton) activity.findViewById(R.id.cup2_4);
        cup2_5 = (CupButton) activity.findViewById(R.id.cup2_5);
        cup2_6 = (CupButton) activity.findViewById(R.id.cup2_6);
        cup2_7 = (CupButton) activity.findViewById(R.id.cup2_7);
        cup2_store = (CupButton) activity.findViewById(R.id.cup2_store);
    }

    public void test1_Exists() {
        assertNotNull(activity);
    }

    public void test2_ShellText() {
        TouchUtils.clickView(this, cup1_3);

        getInstrumentation().waitForIdleSync();

        assertEquals("0", cup1_3.getText().toString());
        assertEquals("8", cup1_4.getText().toString());
        assertEquals("8", cup1_5.getText().toString());
        assertEquals("8", cup1_6.getText().toString());
        assertEquals("8", cup1_7.getText().toString());
        assertEquals("1", cup1_store.getText().toString());
        assertEquals("8", cup2_1.getText().toString());
        assertEquals("8", cup2_2.getText().toString());
    }

    public void test3_CantClickCup() {
        TouchUtils.clickView(this, cup1_5);
        getInstrumentation().waitForIdleSync();
        assertEquals("8", cup1_5.getText().toString());
    }

    public void test4_RobShell1() {
        // preamble
        TouchUtils.clickView(this, cup2_4);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, cup1_4);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, cup2_1);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, cup1_1);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, cup2_2);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, cup1_2);
        getInstrumentation().waitForIdleSync();

        // cup1_2 and cup2_2 should be empty at this point
        // cup2_4 should have 2 shells
        TouchUtils.clickView(this, cup2_4);
        getInstrumentation().waitForIdleSync();

        assertEquals("0", cup1_2.getText().toString());
        assertEquals("0", cup2_2.getText().toString());
        assertEquals("4", cup1_store.getText().toString());
        assertEquals("3", cup2_store.getText().toString());
    }
}
