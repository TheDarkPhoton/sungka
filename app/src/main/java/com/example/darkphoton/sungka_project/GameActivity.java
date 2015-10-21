package com.example.darkphoton.sungka_project;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;

public class GameActivity extends Activity {

    //Base layout
    private GridLayout layout;
    //Screen size
    private int _screenWidth, _screenHeight;
    //Button array - 0 to 6 = player small cups, 7 = player store cup, 8 to 14 = opponent small cups, 15 = opponent store cup
    private Button[] cupButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide navigation bar and system bar
        hideNav();

        //Create base layout and parameters
        layout = new GridLayout(this);
        layout.setColumnCount(9);
        layout.setRowCount(3);
        layout.setBackgroundResource(R.drawable.background);

        LayoutParams params = new LayoutParams();
        params.height = LayoutParams.MATCH_PARENT;
        params.width = LayoutParams.MATCH_PARENT;
        //layout.setBackgroundResource();

        //Set view to base layout
        setContentView(layout, params);

        //Set screen size
        setScreenSize();

        //Programmatically create and lay out elements
        initView();
    }

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        //_screenWidth = layout.getWidth();
        //_screenHeight = layout.getHeight();
    }

    /**
     * Calculate sizes for layout
     */
    private void initView() {
        //Calculate sizes of store cups and small cups
        int storeSize, cupSize;

        //Store cups are 15% of the screen width
        storeSize = (int) (_screenWidth * 0.156);

        //Small cups are 6.5% of the screen width
        cupSize = (int) (_screenWidth * 0.078);

        //Calculate spacings
        int spaceTop, spaceLeft, spaceSmall, spaceStoreTop;

        //Space from store inner edge
        spaceSmall = (int) (_screenWidth * 0.005);

        //Space from store top
        spaceStoreTop = (int) (_screenHeight * 0.05);

        //Space from left
        spaceLeft = (_screenWidth - (((storeSize * 2) + (cupSize * 7) + (spaceSmall * 14)))) / 2;

        //Space from top
        spaceTop = (_screenHeight - ((storeSize + (cupSize * 2) + (spaceStoreTop * 2)))) / 2;

        //Send sizes and spaces to create and lay out all buttons
        formatView(storeSize, cupSize, spaceTop, spaceLeft, spaceSmall, spaceStoreTop);
    }

    private void formatView(int storeSize, int cupSize, int spaceTop, int spaceLeft, int spaceSmall, int spaceStoreTop) {
        //Initialise button array
        cupButtons = new Button[16];

        //Create buttons
        for(int i = 0; i < 16; i++) cupButtons[i] = new Button(this);

        // A scale factor for text sizes
        float scaleFactor = cupSize / 199.0f;

        //Set params for small cups
        //Player
        for(int i = 0; i < 7; i++) {
            Button button = cupButtons[i];
            button.setTextColor(Color.parseColor("#FFFFFF"));
            button.setText(String.valueOf(i));
            button.setTextSize(30 * scaleFactor);
            button.setBackgroundResource(R.drawable.player_smallcup);

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
            params.rowSpec = GridLayout.spec(2);
            params.bottomMargin = spaceTop;
            params.leftMargin = spaceSmall;
            params.rightMargin = spaceSmall;
            button.setLayoutParams(params);
            layout.addView(button);
        }
        //Opponent
        int columnIndex = 1;
        for(int i = 14; i > 7; i--) {
            Button button = cupButtons[i];
            button.setText(String.valueOf(i));
            button.setTextSize(30 * scaleFactor);
            button.setBackgroundResource(R.drawable.opponent_smallcup);

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
            params.columnSpec = GridLayout.spec(columnIndex++);
            params.rowSpec = GridLayout.spec(0);
            params.topMargin = spaceTop;
            params.leftMargin = spaceSmall;
            params.rightMargin = spaceSmall;
            button.setLayoutParams(params);
            layout.addView(button);
        }

        //Set attributes for store cups
        //Player
        Button playerStore = cupButtons[7];
        playerStore.setBackgroundResource(R.drawable.player_bigcup);
        playerStore.setText(String.valueOf(7));
        playerStore.setTextSize(50 * scaleFactor);

        //Set params and add to view
        LayoutParams playerParams = new LayoutParams();
        playerParams.width = storeSize;
        playerParams.height = storeSize;
        playerParams.columnSpec = GridLayout.spec(8);
        playerParams.rowSpec = GridLayout.spec(1);
        playerParams.rightMargin = spaceLeft;
        playerParams.leftMargin = spaceSmall;
        playerParams.topMargin = spaceStoreTop;
        playerParams.bottomMargin = spaceStoreTop;
        playerStore.setLayoutParams(playerParams);
        layout.addView(playerStore);

        //Opponent
        Button opponentStore = cupButtons[15];
        opponentStore.setBackgroundResource(R.drawable.opponent_bigcup);
        opponentStore.setText(String.valueOf(15));
        opponentStore.setTextColor(Color.parseColor("#FFFFFF"));
        opponentStore.setTextSize(50 * scaleFactor);

        //Set params and add to view
        LayoutParams opponentParams = new LayoutParams();
        opponentParams.width = storeSize;
        opponentParams.height = storeSize;
        opponentParams.columnSpec = GridLayout.spec(0);
        opponentParams.rowSpec = GridLayout.spec(1);
        opponentParams.rightMargin = spaceSmall;
        opponentParams.leftMargin = spaceLeft;
        opponentParams.topMargin = spaceStoreTop;
        opponentParams.bottomMargin = spaceStoreTop;
        opponentStore.setLayoutParams(opponentParams);
        layout.addView(opponentStore);
    }

    /**
     * Get array of buttons
     * @return - button array for all cups
     */
    public Button[] getCupButtons() {
        return cupButtons;
    }

    public void setButtonCount(int id, int count) {
        cupButtons[id].setText(String.valueOf(count));
    }

    public void handleButton(int id) {
        System.out.println("Hole " + id + " was pressed.");
    }
}
