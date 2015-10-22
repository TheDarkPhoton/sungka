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
        //Store cups are 15.6% of the screen width
        storeSize = (int) (_screenWidth * 0.156);

        //Small cups are 7.8% of the screen width
        cupSize = (int) (_screenWidth * 0.078);

        // A scale factor for text sizes
        float scaleFactor = cupSize / 199.0f;

        //Calculate spacings
        int spaceTop, spaceLeft, spaceSmall, spaceStoreTop;

        //Space from store inner edge
        spaceSmall = (int) (_screenWidth * 0.005);

        //Space from store top
        spaceStoreTop = (int) (_screenHeight * 0.05);

        //Space from left
        spaceLeft = (_screenWidth - (((storeSize * 2) + (cupSize * 7) + (spaceSmall * 14)))) / 2;

        //Space from top
        spaceTop = ((_screenHeight - ((storeSize + (cupSize * 2) + (spaceStoreTop * 2)))) / 2) - cupSize / 2;

        //Send sizes and spaces to create and lay out all buttons
        formatView(storeSize, cupSize, spaceTop, spaceLeft, spaceSmall, spaceStoreTop, scaleFactor);

        // initialise shellcups with 7 shells each
        initialiseCups();
    }

    private void formatView(int storeSize, int cupSize, int spaceTop, int spaceLeft, int spaceSmall, int spaceStoreTop, float scaleFactor) {
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
            //Button
            Button button = cupButtons[i];
            button.setBackgroundResource(R.drawable.player_smallcup);
            //Text
            TextView text = cupTexts[i];
            text.setTextColor(Color.parseColor("#FFFFFF"));
            text.setText(String.valueOf(i));
            text.setTextSize(30 * scaleFactor);

            // Add listener for click
            final int id = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButton(id);
                }
            });

            //Set params and add to view
            LayoutParams params = new LayoutParams();
            params.width = cupSize;
            params.height = cupSize;
            params.columnSpec = GridLayout.spec(i + 1);
            params.rowSpec = GridLayout.spec(3);
            params.leftMargin = spaceSmall;
            params.rightMargin = spaceSmall;
            button.setLayoutParams(params);
            layoutBase.addView(button);

            LayoutParams paramsText = new LayoutParams();
            paramsText.width = LayoutParams.WRAP_CONTENT;
            paramsText.height = LayoutParams.WRAP_CONTENT;
            paramsText.columnSpec = GridLayout.spec(i + 1);
            paramsText.rowSpec = GridLayout.spec(4);
            paramsText.bottomMargin = spaceTop;
            paramsText.setGravity(Gravity.CENTER);
            text.setLayoutParams(paramsText);
            layoutBase.addView(text);
        }
        //Opponent
        int columnIndex = 1;
        for(int i = 14; i > 7; i--) {
            //Button
            Button button = cupButtons[i];
            button.setBackgroundResource(R.drawable.opponent_smallcup);
            //Text
            TextView text = cupTexts[i];
            text.setText(String.valueOf(i));
            text.setTextSize(30 * scaleFactor);

            // Add listener for click
            final int id = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButton(id);
                }
            });

            //Set params and add to view
            LayoutParams params = new LayoutParams();
            params.width = cupSize;
            params.height = cupSize;
            params.columnSpec = GridLayout.spec(columnIndex);
            params.rowSpec = GridLayout.spec(1);
            params.leftMargin = spaceSmall;
            params.rightMargin = spaceSmall;
            button.setLayoutParams(params);
            layoutBase.addView(button);

            LayoutParams paramsText = new LayoutParams();
            paramsText.width = LayoutParams.WRAP_CONTENT;
            paramsText.height = LayoutParams.WRAP_CONTENT;
            paramsText.columnSpec = GridLayout.spec(columnIndex);
            paramsText.rowSpec = GridLayout.spec(0);
            paramsText.topMargin = spaceTop;
            paramsText.setGravity(Gravity.CENTER);
            text.setLayoutParams(paramsText);
            layoutBase.addView(text);

            columnIndex++;
        }

        //Set attributes for store cups
        //Player
        Button playerStore = cupButtons[7];
        playerStore.setBackgroundResource(R.drawable.player_bigcup);
        TextView playerText = cupTexts[7];
        playerText.setText(String.valueOf(7));
        playerText.setTextColor(Color.parseColor("#FFFFFF"));
        playerText.setTextSize(40 * scaleFactor);

        //Set params and add to view
        LayoutParams playerParams = new LayoutParams();
        playerParams.width = storeSize;
        playerParams.height = storeSize;
        playerParams.columnSpec = GridLayout.spec(8);
        playerParams.rowSpec = GridLayout.spec(2);
        playerParams.rightMargin = spaceLeft;
        playerParams.leftMargin = spaceSmall;
        playerParams.topMargin = spaceStoreTop;
        playerParams.bottomMargin = spaceStoreTop;
        playerStore.setLayoutParams(playerParams);
        layoutBase.addView(playerStore);

        LayoutParams paramsPlayerText = new LayoutParams();
        paramsPlayerText.width = LayoutParams.WRAP_CONTENT;
        paramsPlayerText.height = LayoutParams.WRAP_CONTENT;
        paramsPlayerText.columnSpec = GridLayout.spec(8);
        paramsPlayerText.rowSpec = GridLayout.spec(3);
        paramsPlayerText.rightMargin = spaceSmall;
        paramsPlayerText.setGravity(Gravity.CENTER);
        playerText.setLayoutParams(paramsPlayerText);
        layoutBase.addView(playerText);

        playerStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Store pressed");
//                test1();
            }
        });

        //Opponent
        Button opponentStore = cupButtons[15];
        opponentStore.setBackgroundResource(R.drawable.opponent_bigcup);
        TextView opponentText = cupTexts[15];
        opponentText.setText(String.valueOf(15));
        opponentText.setTextSize(40 * scaleFactor);

        //Set params and add to view
        LayoutParams opponentParams = new LayoutParams();
        opponentParams.width = storeSize;
        opponentParams.height = storeSize;
        opponentParams.columnSpec = GridLayout.spec(0);
        opponentParams.rowSpec = GridLayout.spec(2);
        opponentParams.rightMargin = spaceSmall;
        opponentParams.leftMargin = spaceLeft;
        opponentParams.topMargin = spaceStoreTop;
        opponentParams.bottomMargin = spaceStoreTop;
        opponentStore.setLayoutParams(opponentParams);
        layoutBase.addView(opponentStore);

        LayoutParams paramsOpponentText = new LayoutParams();
        paramsOpponentText.width = LayoutParams.WRAP_CONTENT;
        paramsOpponentText.height = LayoutParams.WRAP_CONTENT;
        paramsOpponentText.columnSpec = GridLayout.spec(0);
        paramsOpponentText.rowSpec = GridLayout.spec(1);
        paramsOpponentText.leftMargin = spaceSmall;
        paramsOpponentText.setGravity(Gravity.CENTER);
        opponentText.setLayoutParams(paramsOpponentText);
        layoutBase.addView(opponentText);

        opponentStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Store pressed");
//                test();
            }
        });
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
            HandOfShells hand = game.fetchHand(id);
            setButtonCount(id, 0, false);

            while (hand.isNotEmpty()) {
                id = hand.next();
                hand.dropShell();

                if (id % 8 == 7) {
                    // cup is PlayerCup
                    addButtonCount(id, true);
                } else {
                    // cup is ShellCup
                    addButtonCount(id, false);
                }

//                wait(500);
            }
        } else {
            Log.i(TAG, "Invalid move");
        }
    }

    public void test() {
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
