package com.example.darkphoton.sungka_project;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

import game.Board;
import game.Game;
import game.HandOfShells;
import game.BoardState;

public class GameActivity extends Activity {
    private static final String TAG = "GameActivity";

    //Master layout
    private FrameLayout layoutMaster;
    //Base layout
    private GridLayout layoutBase;

    //Screen size
    private int _screenWidth, _screenHeight;

    private CupButton[] cupButtons;
    private Game game;
    private Board board;

    public static Drawable[] shells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shells = new Drawable[]{
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell1, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell2, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell3, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell4, null),
        };

        game = new Game();
        board = game.getBoard();

        //Hide navigation bar and system bar
        hideNav();

        //Set screen size
        setScreenSize();

        //Setup layouts
        FrameLayout.LayoutParams params = initLayouts();

        //Set view to base layout
        setContentView(layoutMaster, params);

        //Programmatically create and lay out elements
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) { return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        game.start();
    }

    /**
     * Hide navigation bar and system bar for all API levels
     */
    private void hideNav() {
        //Hide the navigation bar on all API levels
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    /**
     *
     * @return - parameters for master layout
     */
    private FrameLayout.LayoutParams initLayouts() {

        //Create FrameLayout and parameters
        layoutMaster = new FrameLayout(this);
        layoutMaster.setBackgroundResource(R.drawable.background);

        //Create base layout and parameters
        layoutBase = new GridLayout(this);
        layoutBase.setLayoutParams(new GridLayout.LayoutParams());
        layoutBase.setColumnCount(9);
        layoutBase.setRowCount(3);

        //Create top layout
        layoutBase.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        //Add layouts to master
        layoutMaster.addView(layoutBase);

        layoutMaster.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean once = false;

            @Override
            public void onGlobalLayout() {
                layoutBase.setY((layoutMaster.getHeight()/2) - (layoutBase.getHeight()/2));

                if (once)
                    return;
                once = true;
                
                for (CupButton btn: cupButtons) {
                    btn.initShellLocation();
                }
            }
        });

        return new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    /**
     * Gets the size of the device screen
     */
    private void setScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        _screenWidth = displayMetrics.widthPixels;
        _screenHeight = displayMetrics.heightPixels;

        //Calculate sizes of store cups and small cups
        CupButton.generateSizes(_screenWidth,_screenHeight);
    }

    /**
     * Initialises all cup buttons in the right location on the screen.
     */
    private void initView() {
        for (int i = 0; i < shells.length; i++) {
            Bitmap b = ((BitmapDrawable)shells[i]).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, (int)(CupButton.sizes.cup * 0.2), (int)(CupButton.sizes.cup * 0.2), false);
            shells[i] = new BitmapDrawable(getResources(), bitmapResized);
        }

        //Initialise button array
        cupButtons = new CupButton[16];

        int topColumnIndex = 7;
        int bottomColumnIndex = 1;
        for(int i = 0; i < 7; i++) {
            //PLAYER A shell cup
            CupButton btn = new CupButton(this, board.getCup(i), CupButton.PLAYER_A, CupButton.CUP);
            btn.addToLayout(layoutBase, bottomColumnIndex++, 2);
            cupButtons[i] = btn;

            final int indexA = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveShells(indexA);
                }
            });

            //PlayerB shell cup
            btn = new CupButton(this, board.getCup(i+8), CupButton.PLAYER_B, CupButton.CUP);
            btn.addToLayout(layoutBase, topColumnIndex--, 0);
            cupButtons[i+8] = btn;

            final int indexB = i+8;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveShells(indexB);
                }
            });
        }
        //PLAYER A store
        CupButton btnPlayerA = new CupButton(this, board.getCup(7), CupButton.PLAYER_A, CupButton.STORE);
        btnPlayerA.addToLayout(layoutBase, 8, 1);
        cupButtons[7] = btnPlayerA;

        //PLAYER B store
        CupButton btnPlayerB = new CupButton(this, board.getCup(15), CupButton.PLAYER_B, CupButton.STORE);
        btnPlayerB.addToLayout(layoutBase, 0, 1);
        cupButtons[15] = btnPlayerB;
    }

    private final Random r = new Random();                                  //Object for random number generation
    private boolean animationInProgress = false;                            //Prevents buttons clicks while animation is in progress

    /**
     * Prepares variables for the animation.
     * @param index the cup button pressed.
     */
    private void moveShells(int index){
        if (animationInProgress)
            return;

        HandOfShells hand = board.pickUpShells(index);
        if (hand == null)
            return;

        animationInProgress = true;
        ArrayList<View> images = cupButtons[index].getShells();
        moveShellsRec(hand, images, 300);
    }

    /**
     * Recursive animation method, performs single animation from cup a to cup b.
     * @param hand Used to control the state of the backend.
     * @param images Images to be moved from the clicked cup.
     * @param duration Duration of the animation.
     */
    private void moveShellsRec(final HandOfShells hand, final ArrayList<View> images, final int duration) {
        int index = hand.getNextCup();
        CupButton b = cupButtons[index];

        for (int i = 0; i < images.size(); i++) {
            View image = images.get(i);
            float[] coord = b.randomPositionInCup(image);
            new ShellTranslation(b, image, coord, duration).startAnimation();
        }

        final HandOfShells robersHand = hand.dropShell();
        b.addShell((ImageView) images.remove(images.size() - 1));

        b.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (images.size() > 0) {
                    moveShellsRec(hand, images, duration);
                } else if (robersHand != null) {
                    moveRobOpponent(robersHand, duration * 2);
                    showAnimationMessages();
                } else {
                    animationInProgress = false;
                    showAnimationMessages();
                }
            }
        }, duration + 25);
    }

    /**
     * Method that handles the case where the opponents cup is robbed because shell
     * landed in the current players cup.
     * @param robbersHand The index of the opponents cup to be robbed.
     * @param duration Duration of the animation.
     */
    private void moveRobOpponent(final HandOfShells robbersHand, final int duration){
        CupButton playersCup;
        CupButton robbedCup;

        if (board.isPlayerA(robbersHand.belongsToPlayer())){
            playersCup = cupButtons[15];
            robbedCup = cupButtons[robbersHand.currentCupIndex()];
            robbersHand.setNextCup(15);
        }
        else{
            playersCup = cupButtons[7];
            robbedCup = cupButtons[robbersHand.currentCupIndex()];
            robbersHand.setNextCup(7);
        }

        ArrayList<View> images = robbedCup.getShells();

        for (int i = 0; i < images.size(); i++) {
            View image = images.get(i);
            float[] coord = playersCup.randomPositionInCup(image);
            new ShellTranslation(playersCup, image, coord, duration).startAnimation();
        }

        robbersHand.dropAllShells();
        playersCup.addShells(images);

        playersCup.postDelayed(new Runnable() {
            @Override
            public void run() {
                animationInProgress = false;
                showAnimationMessages();
            }
        }, duration + 10);
    }

    /**
     * Displays messages that explains what happened during animation. TODO Display info messages on the screen.
     */
    private void showAnimationMessages(){
        ArrayList<BoardState> msgs = Board.getMessages();
        for (BoardState state : msgs) {
            switch (state){
                case PLAYER_A_TURN:
                    Log.i(TAG, board.getPlayerA().getName() + "'s turn.");
                    break;
                case PLAYER_B_TURN:
                    Log.i(TAG, board.getPlayerB().getName() + "'s turn.");
                    break;
                case PLAYER_A_HAS_NO_VALID_MOVE:
                    Log.i(TAG, board.getPlayerA().getName() + " has no valid move");
                    break;
                case PLAYER_B_HAS_NO_VALID_MOVE:
                    Log.i(TAG, board.getPlayerB().getName() + " has no valid move");
                    break;
                case PLAYER_A_GETS_ANOTHER_TURN:
                    Log.i(TAG, board.getPlayerA().getName() + " gets another turn.");
                    break;
                case PLAYER_B_GETS_ANOTHER_TURN:
                    Log.i(TAG, board.getPlayerB().getName() + " gets another turn.");
                    break;
                case PLAYER_A_WAS_ROBBED:
                    Log.i(TAG, board.getPlayerA().getName() + " was robbed.");
                    break;
                case PLAYER_B_WAS_ROBBED:
                    Log.i(TAG, board.getPlayerB().getName() + " was robbed.");
                    break;
                case PLAYER_A_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                    Log.i(TAG, board.getPlayerA().getName() + " was robbed of his final move... " + board.getPlayerB().getName() + " gets another turn.");
                    break;
                case PLAYER_B_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                    Log.i(TAG, board.getPlayerB().getName() + " was robbed of his final move... " + board.getPlayerA().getName() + " gets another turn.");
                    break;
                case GAME_OVER:
                    Log.i(TAG, "Game Over!!!");
                    break;
            }
        }

        msgs.clear();
    }
}