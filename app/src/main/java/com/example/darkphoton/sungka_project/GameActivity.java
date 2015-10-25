package com.example.darkphoton.sungka_project;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

import animator.AnimatorSet;
import animator.LinearAnimator;
import game.Board;
import game.Game;
import game.HandOfShells;

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

        for (int i = 0; i < shells.length; i++) {
            Bitmap b = ((BitmapDrawable)shells[i]).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, (int)(sizes.cup * 0.2), (int)(sizes.cup * 0.2), false);
            shells[i] = new BitmapDrawable(getResources(), bitmapResized);
        }

        //Initialise button array
        cupButtons = new CupButton[16];

        int topColumnIndex = 7;
        int bottomColumnIndex = 1;
        for(int i = 0; i < 7; i++) {
            //PLAYER A shell cup
            CupButton btn = new CupButton(this, board.getCup(i), PlayerType.A, CupType.SHELL, sizes, i);
            btn.addToLayout(layoutBase, bottomColumnIndex++, 2);
            cupButtons[i] = btn;

            final int indexA = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Index A");
                    moveShells(indexA);
                }
            });

            //PlayerB shell cup
            btn = new CupButton(this, board.getCup(i+8), PlayerType.B, CupType.SHELL, sizes, i+8);
            btn.addToLayout(layoutBase, topColumnIndex--, 0);
            cupButtons[i+8] = btn;

            final int indexB = i+8;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Index B");
                    moveShells(indexB);
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

    private void moveShells(int index) {
        HandOfShells hand = board.pickUpShells(index);
        if (hand == null)
            return;

//        ArrayList<View> saved = new ArrayList<>();
        ArrayList<View> images = cupButtons[index].getShells();

        CupButton b = cupButtons[index + 1];

//        ArrayList<AnimationSet> sets = new ArrayList<>();
//        for (int i = 0; i < images.size(); i++) {
//            sets.add(new AnimationSet(true));
//        }

        Random r = new Random();
        int counter = 0;
        while (hand.isNotEmpty()){
            ++index;
            hand.nextCup();

            for (int i = 0; i < images.size(); i++) {
                View image = images.get(i);
                float[] coord = b.randomPositionInCup(r, image);
                image.postDelayed(new DelayedTranslation(image, coord, 1000), 1100 * counter);
            }

            if (hand.dropShell()){
                ImageView image = (ImageView)images.remove(images.size() - 1);
                b.addShell(image);
//                saved.add(image);
            }

            b = cupButtons[(index + 1) % 15];
            ++counter;
//            break;
        }

//        for (int i = 0; i < saved.size(); i++) {
//            saved.get(i).startAnimation(sets.get(i));
//        }
    }
}