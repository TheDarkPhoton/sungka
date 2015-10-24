package com.example.darkphoton.sungka_project;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import game.Game;
import game.HandOfShells;

public class GameActivity extends Activity {
    public enum PlayerType{
        A, B
    }
    public enum CupType {
        Player, Shell
    }

    private static final String TAG = "GameActivity";

    //Master layout
    private FrameLayout layoutMaster;
    //Base layout
    private GridLayout layoutBase;
    //Top Layout
    private RelativeLayout layoutTop;

    //Screen size
    private int _screenWidth, _screenHeight;
    //Button array - 0 to 6 = player small cups, 7 = player store cup, 8 to 14 = opponent small cups, 15 = opponent store cup
    private Button[] cupButtons;
    private TextView[] cupTexts;
    //Arraylist for arraylists of shells within buttons - arrays follow cup indexing
    private ArrayList<ArrayList<ImageView>> cupShells;
    //Array of shell images
    private Drawable[] shells;
    private int cupSize, storeSize;

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide navigation bar and system bar
        hideNav();

        //Setup layouts
        FrameLayout.LayoutParams params = initLayouts();

        //Set view to base layout
        setContentView(layoutMaster, params);

        //Set screen size
        setScreenSize();

        //Programmatically create and lay out elements
        initView();

        game = new Game();
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
        layoutBase.setRowCount(5);

        //Create top layout
        layoutTop = new RelativeLayout(this);
        layoutBase.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        //Add layouts to master
        layoutMaster.addView(layoutBase);
        layoutMaster.addView(layoutTop);

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
    }

    /**
     * Calculate sizes for layoutBase
     */
    private void initView() {
        //Calculate sizes of store cups and small cups
        CupMargins sizes = new CupMargins(_screenWidth, _screenHeight, cupSize);

        //Send sizes and spaces to create and lay out all buttons
        formatView(sizes);

        // Wait for gridLayout to finish drawing so that we can get co-ordinates of subviews.
        layoutBase.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initialiseCups();                                                                   // initialise shellcups with 7 shells each
            }
        });
    }

    private void formatView(CupMargins sizes) {
        //Initialise button array
        cupButtons = new Button[16];
        cupTexts = new TextView[16];
        //Initialise shell arrays
        cupShells = new ArrayList<>(16);
        for(int i = 0; i < 16; i++) {
            cupShells.add(new ArrayList<ImageView>());
        }
        //Add shell types to array
        shells = new Drawable[]{
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell1, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell2, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell3, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell4, null),
        };

        //Create buttons + texts
        for(int i = 0; i < 16; i++) cupButtons[i] = new Button(this);
        for(int i = 0; i < 16; i++) cupTexts[i] = new TextView(this);

        //Set params for small cups
        //Player
        for(int i = 0; i < 7; i++) {
            CupButton btn = new CupButton(this, PlayerType.A, CupType.Shell, sizes, i);
            btn.addToLayout(layoutBase, i, 3, 4);
            cupButtons[i] = btn;
        }
        CupButton btnPlayerA = new CupButton(this, PlayerType.A, CupType.Player, sizes, 7);
        btnPlayerA.addToLayout(layoutBase, 8, 2, 3);
        cupButtons[7] = btnPlayerA;


        //Opponent
        int columnIndex = 1;
        for(int i = 14; i > 7; i--) {
            CupButton btn = new CupButton(this, PlayerType.B, CupType.Shell, sizes, i);
            btn.addToLayout(layoutBase, columnIndex, 1, 0);
            cupButtons[i] = btn;
//
//            //Button
//            Button button = cupButtons[i];
//            button.setBackgroundResource(R.drawable.opponent_smallcup);
//            //Text
//            TextView text = cupTexts[i];
//            text.setText(String.valueOf(i));
//            text.setTextSize(30 * sizes.scale);
//
//            // Add listener for click
//            final int id = i;
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    handleButton(id);
//                }
//            });
//
//            //Set params and add to view
//            LayoutParams params = new LayoutParams();
//            params.width = cupSize;
//            params.height = cupSize;
//            params.columnSpec = GridLayout.spec(columnIndex);
//            params.rowSpec = GridLayout.spec(1);
//            params.leftMargin = sizes.spaceSmall;
//            params.rightMargin = sizes.spaceSmall;
//            button.setLayoutParams(params);
//            layoutBase.addView(button);
//
//            LayoutParams paramsText = new LayoutParams();
//            paramsText.width = LayoutParams.WRAP_CONTENT;
//            paramsText.height = LayoutParams.WRAP_CONTENT;
//            paramsText.columnSpec = GridLayout.spec(columnIndex);
//            paramsText.rowSpec = GridLayout.spec(0);
//            paramsText.topMargin = sizes.spaceTop;
//            paramsText.setGravity(Gravity.CENTER);
//            text.setLayoutParams(paramsText);
//            layoutBase.addView(text);

            columnIndex++;
        }

        CupButton btnPlayerB = new CupButton(this, PlayerType.B, CupType.Player, sizes, 15);
        btnPlayerB.addToLayout(layoutBase, 0, 2, 1);
        cupButtons[15] = btnPlayerB;

//        //Set attributes for store cups
//        //Player
//        Button playerStore = cupButtons[7];
//        playerStore.setBackgroundResource(R.drawable.player_bigcup);
//        TextView playerText = cupTexts[7];
//        playerText.setText(String.valueOf(7));
//        playerText.setTextColor(Color.parseColor("#FFFFFF"));
//        playerText.setTextSize(40 * sizes.scale);
//
//        //Set params and add to view
//        LayoutParams playerParams = new LayoutParams();
//        playerParams.width = storeSize;
//        playerParams.height = storeSize;
//        playerParams.columnSpec = GridLayout.spec(8);
//        playerParams.rowSpec = GridLayout.spec(2);
//        playerParams.rightMargin = sizes.spaceLeft;
//        playerParams.leftMargin = sizes.spaceSmall;
//        playerParams.topMargin = sizes.spaceStoreTop;
//        playerParams.bottomMargin = sizes.spaceStoreTop;
//        playerStore.setLayoutParams(playerParams);
//        layoutBase.addView(playerStore);
//
//        LayoutParams paramsPlayerText = new LayoutParams();
//        paramsPlayerText.width = LayoutParams.WRAP_CONTENT;
//        paramsPlayerText.height = LayoutParams.WRAP_CONTENT;
//        paramsPlayerText.columnSpec = GridLayout.spec(8);
//        paramsPlayerText.rowSpec = GridLayout.spec(3);
//        paramsPlayerText.rightMargin = sizes.spaceSmall;
//        paramsPlayerText.setGravity(Gravity.CENTER);
//        playerText.setLayoutParams(paramsPlayerText);
//        layoutBase.addView(playerText);
//
//        playerStore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Store pressed");
////                test1();
//            }
//        });

//
//        //Opponent
//        Button opponentStore = cupButtons[15];
//        opponentStore.setBackgroundResource(R.drawable.opponent_bigcup);
//        TextView opponentText = cupTexts[15];
//        opponentText.setText(String.valueOf(15));
//        opponentText.setTextSize(40 * sizes.scale);
//
//        //Set params and add to view
//        LayoutParams opponentParams = new LayoutParams();
//        opponentParams.width = storeSize;
//        opponentParams.height = storeSize;
//        opponentParams.columnSpec = GridLayout.spec(0);
//        opponentParams.rowSpec = GridLayout.spec(2);
//        opponentParams.rightMargin = sizes.spaceSmall;
//        opponentParams.leftMargin = sizes.spaceLeft;
//        opponentParams.topMargin = sizes.spaceStoreTop;
//        opponentParams.bottomMargin = sizes.spaceStoreTop;
//        opponentStore.setLayoutParams(opponentParams);
//        layoutBase.addView(opponentStore);
//
//        LayoutParams paramsOpponentText = new LayoutParams();
//        paramsOpponentText.width = LayoutParams.WRAP_CONTENT;
//        paramsOpponentText.height = LayoutParams.WRAP_CONTENT;
//        paramsOpponentText.columnSpec = GridLayout.spec(0);
//        paramsOpponentText.rowSpec = GridLayout.spec(1);
//        paramsOpponentText.leftMargin = sizes.spaceSmall;
//        paramsOpponentText.setGravity(Gravity.CENTER);
//        opponentText.setLayoutParams(paramsOpponentText);
//        layoutBase.addView(opponentText);
//
//        opponentStore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "Store pressed");
////                test();
//            }
//        });
    }

    /**
     * Update visual display of cup - number of shells etc
     * @param id - array index of button
     */
    private void updateCup(int id, boolean isStore) {
        Button cup = cupButtons[id];
        int count = Integer.parseInt(cupTexts[id].getText().toString());
        int coords[] = new int[2];
        cup.getLocationInWindow(coords);
        int x = coords[0]; int y = coords[1];
        int shellHeight = (int) (cupSize * 0.2);
        int shellWidth = (int) (cupSize * 0.2);
        int radius = cupSize/2 - shellHeight;
        int x1 = x + cupSize/2;
        int y1 = y + cupSize/2;
        if(isStore) {
            radius = storeSize/2 - shellHeight;
            x1 = x + storeSize/2;
            y1 = y + storeSize/2;
        }

        ArrayList<ImageView> shellList = cupShells.get(id);
        int currentShellCount = shellList.size();
        Log.d(TAG, "New count = " + count + ", current shell count = " + currentShellCount);

        if(currentShellCount < count) {
            //increase number of shells
            //find number to add - loop through
            for(int i = currentShellCount; i < count; i++) {
                //Add new shell
                ImageView shell = new ImageView(this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    shell.setBackground(shells[new Random().nextInt(shells.length)]);
                } else {
                    shell.setBackgroundResource(R.drawable.shell1);
                }
                double angle = new Random().nextDouble() * (Math.PI * 2);
                double r = new Random().nextDouble();
                int shellX = (int) (x1 + ((radius * r)* Math.cos(angle)));
                int shellY = (int) (y1 + ((radius * r)* Math.sin(angle)));

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(shellWidth, shellHeight);
                params.leftMargin = shellX;
                params.topMargin = shellY;
                shell.setLayoutParams(params);
                shell.setPivotX(shellWidth / 2);
                shell.setPivotY(shellHeight / 2);
                shell.setRotation(new Random().nextInt(360));
                shellList.add(shell);
                layoutTop.addView(shell);
            }
        } else if(currentShellCount > count) {
            //decrease number of shells
            //find number to remove - loop through
            for(int i = currentShellCount -1; i >= count; i--) {
                ImageView shell = shellList.get(i);
                layoutTop.removeView(shell);
                shellList.remove(i);
                Log.d(TAG, "removed");
            }
        }
    }

    /**
     * Distribute shells from a cup
     * @param id The id of the cup
     */
    public void distributeShells(int id) {
        // TODO: merge functionality of this method with that
        // TODO: of handleButton(), and then use this method
        // TODO: with the onClickListener instead of handleButton()
        ArrayList<ImageView> shells = cupShells.get(id);

        int[] currentCoords = new int[2];
        cupButtons[id].getLocationInWindow(currentCoords);

        int[] nextCoords = new int[2];
        cupButtons[(id+1)%16].getLocationInWindow(nextCoords);

        long delay = 0;


        for (ImageView shellView: shells) {
            AnimationSet animationSet = new AnimationSet(true);
            TranslateAnimation horizontalAnimation = new TranslateAnimation(
                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, nextCoords[0] - currentCoords[0],
                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, nextCoords[1] - currentCoords[1]);
            horizontalAnimation.setDuration(1000);

//            TranslateAnimation verticalAnimationUp = new TranslateAnimation(
//                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, 0,
//                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, -200
//            );
//            verticalAnimationUp.setDuration(500);
//
//            TranslateAnimation verticalAnimationDown = new TranslateAnimation(
//                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, 0,
//                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, -200
//            );
//            verticalAnimationUp.setDuration(500);

            animationSet.addAnimation(horizontalAnimation);
//            animationSet.addAnimation(verticalAnimationUp);

            animationSet.setStartOffset(delay);
            animationSet.setFillAfter(true);

            shellView.startAnimation(animationSet);

            delay += 100;
        }
    }

    /**
     * Get array of buttons
     * @return - button array for all cups
     */
    public Button[] getCupButtons() {
        return cupButtons;
    }

    /**
     * Set the count displayed on a button
     * @param id - id of cup or store: points to array index
     * @param count - count to update to
     */
    public void setButtonCount(int id, int count, boolean isStore) {
        cupTexts[id].setText(String.valueOf(count));
        updateCup(id, isStore);
    }

    public void addButtonCount(int id, boolean isStore) {
        int count = Integer.valueOf(cupTexts[id].getText().toString());
        setButtonCount(id, count + 1, isStore);
    }

    /**
     * Test for button press
     * @param id - array index of button
     */
    public void handleButton(int id) {
        Log.i(TAG, "Hole " + id + " was pressed.");

        if (game.isValidMove(id)) {
            final HandOfShells hand = game.fetchHand(id);

            animateCupTransfer(hand, cupShells.get(id).size());
        } else {
            Log.i(TAG, "Invalid move");
        }
    }

    /**
     * Recursive function to animate the transfer a set number of shells from a cup depending on the
     * state of the HandOfShells
     * @param hand The HandOfShells which captures the state of the hand after a move
     * @param remainingShellsInHand The number of shells that will be transfered from the set cup.
     */
    public void animateCupTransfer(final HandOfShells hand, final int remainingShellsInHand) {
        Log.i(TAG, "animateCupTransfer("+hand+", "+remainingShellsInHand+")");
        if (hand.isNotEmpty()) {
            final int nextId = hand.next() % 16;
            final int currentId = (nextId - 1) % 16;

            // Get the shell image views to animate
            ArrayList<ImageView> shellsInCup = cupShells.get(currentId);
            final ArrayList<ImageView> shellsInHand = new ArrayList<>(remainingShellsInHand);
            Log.i(TAG, "shellsInHand: " + shellsInHand.size());
//            Log.i(TAG, nextId + ": " + cupShells.get(currentId ).size() + ", " + currentId + ": " + shellsInCup.size() + ", " + nextId + ": " + cupShells.get(nextId).size());
//            Log.i(TAG, "shellsInCurrentCup: " + );
//            Log.i(TAG, "nextId: " + );
//            Log.i(TAG, "shellsInNextCup: " + );

            // Only get the correct amount of shell imageviews from the cup
            for (int i = 0; i < Math.min(remainingShellsInHand, shellsInCup.size()); i++) {
                ImageView tempShell = shellsInCup.get(i);
                shellsInHand.add(tempShell);
            }

            // Get coordinates of current cup
            int[] currentCoords = new int[2];
            cupButtons[currentId].getLocationInWindow(currentCoords);

            // Get coordinates of next cup
            int[] nextCoords = new int[2];
            cupButtons[nextId].getLocationInWindow(nextCoords);

            int xChange = nextCoords[0] - currentCoords[0];
            int yChange = nextCoords[1] - currentCoords[1];

            // Add delay to each shell animation to create "train" effect
            long delay = 0;

            for (ImageView shellView: shellsInHand) {

                // Setup animation for shell
                AnimationSet animationSet = new AnimationSet(true);



                TranslateAnimation horizontalAnimation = new TranslateAnimation(
                    Animation.ABSOLUTE, 0, Animation.ABSOLUTE, xChange,
                    Animation.ABSOLUTE, 0, Animation.ABSOLUTE, yChange);
                horizontalAnimation.setDuration(1000);

                animationSet.addAnimation(horizontalAnimation);

                // Temporary vertical jump solution
                // TODO: Make shells follow a quadratic curve
//            TranslateAnimation verticalAnimationUp = new TranslateAnimation(
//                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, 0,
//                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, -200
//            );
//            verticalAnimationUp.setDuration(500);
//
//            TranslateAnimation verticalAnimationDown = new TranslateAnimation(
//                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, 0,
//                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, 200
//            );
//            verticalAnimationUp.setDuration(500);

//            animationSet.addAnimation(verticalAnimationUp);
//            animationSet.addAnimation(verticalAnimationDown);

                animationSet.setStartOffset(delay);
                animationSet.setFillAfter(true);
                shellView.startAnimation(animationSet);

                delay += 100;

                // Only apply listener after first shell ends animation
                if (delay != 100)
                    continue;

                hand.dropShell();

                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Transfer Shell ImageViews to correct cup container
                        cupShells.get(nextId).removeAll(shellsInHand);
                        cupShells.get(nextId).addAll(shellsInHand);

                        animateCupTransfer(hand, remainingShellsInHand - 1);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }


    }

    public void initialiseCups() {
        setButtonCount(0, 7, false);
        setButtonCount(1, 7, false);
        setButtonCount(2, 7, false);
        setButtonCount(3, 7, false);
        setButtonCount(4, 7, false);
        setButtonCount(5, 7, false);
        setButtonCount(6, 7, false);
        setButtonCount(7, 0, true);
        setButtonCount(8, 7, false);
        setButtonCount(9, 7, false);
        setButtonCount(10, 7, false);
        setButtonCount(11, 7, false);
        setButtonCount(12, 7, false);
        setButtonCount(13, 7, false);
        setButtonCount(14, 7, false);
        setButtonCount(15, 0, true);
    }

    public void test1() {
        setButtonCount(0, 5, false);
        setButtonCount(1, 10, false);
        setButtonCount(2, 7, false);
        setButtonCount(3, 3, false);
        setButtonCount(4, 0, false);
        setButtonCount(5, 15, false);
        setButtonCount(6, 2, false);
        setButtonCount(7, 20, true);
        setButtonCount(8, 8, false);
        setButtonCount(9, 1, false);
        setButtonCount(10, 3, false);
        setButtonCount(11, 9, false);
        setButtonCount(12, 7, false);
        setButtonCount(13, 1, false);
        setButtonCount(14, 10, false);
        setButtonCount(15, 30, true);
    }
}
