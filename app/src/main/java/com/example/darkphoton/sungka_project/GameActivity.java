package com.example.darkphoton.sungka_project;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import game.Board;
import game.Game;

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

        shells = new Drawable[]{
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell1, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell2, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell3, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell4, null),
        };

        game = new Game();
        board = game.getBoard();
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

        layoutBase.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutBase.setY((layoutMaster.getHeight()/2) - (layoutBase.getHeight()/2));
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
    }

    /**
     * Calculate sizes for layoutBase
     */
    private void initView() {
        //Calculate sizes of store cups and small cups
        CupMargins sizes = new CupMargins(_screenWidth, _screenHeight);

        //Initialise button array
        cupButtons = new CupButton[16];

        int topColumnIndex = 7;
        int bottomColumnIndex = 1;
        for(int i = 0; i < 7; i++) {
            //PLAYER A shell cup
            CupButton btn = new CupButton(this, board.getCup(i), PlayerType.A, CupType.SHELL, sizes, i);
            btn.addToLayout(layoutBase, bottomColumnIndex++, 2);
            cupButtons[i] = btn;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            //PlayerB shell cup
            btn = new CupButton(this, board.getCup(i+8), PlayerType.B, CupType.SHELL, sizes, i+8);
            btn.addToLayout(layoutBase, topColumnIndex--, 0);
            cupButtons[i+8] = btn;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        //PLAYER A store
        CupButton btnPlayerA = new CupButton(this, board.getCup(7), PlayerType.A, CupType.PLAYER, sizes, 7);
        btnPlayerA.addToLayout(layoutBase, 8, 1);
        cupButtons[7] = btnPlayerA;

        //PLAYER B store
        CupButton btnPlayerB = new CupButton(this, board.getCup(15), PlayerType.B, CupType.PLAYER, sizes, 15);
        btnPlayerB.addToLayout(layoutBase, 0, 1);
        cupButtons[15] = btnPlayerB;
    }
}